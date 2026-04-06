package br.sptrans.scd.channel.adapter.port.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.ContactChannelResponseDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.CreateContactChannelRequest;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UpdateContactChannelRequest;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ContactChannelMapper;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase.CreateContactChannelCommand;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase.UpdateContactChannelCommand;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/contact-channels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contatos do Canal v1", description = "Endpoints para gerenciamento de contatos do canal")
public class ContactChannelController {

    private final ContactChannelUseCase contactChannelUseCase;
    private final UserResolverHelper userResolverHelper;
    private final ContactChannelMapper contactChannelMapper;

    @PostMapping
        @Operation(summary = "Cadastra um novo contato do canal")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato do canal cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<ContactChannelResponseDTO> createContactChannel(
            @RequestBody CreateContactChannelRequest request) {
        User usuario = userResolverHelper.getCurrentUser();
        ContactChannel result = contactChannelUseCase.createContactChannel(new CreateContactChannelCommand(
                request.codContato(),
                request.codFornecedor(),
                request.codEmpregador(),
                request.desContato(),
                request.desEmailContato(),
                request.numDDD(),
                request.numFone(),
                request.numFoneRamal(),
                request.numFax(),
                request.numFaxRamal(),
                request.stEntidadeContato(),
                request.desComentarios(),
                request.codTipoDocumento(),
                request.codDocumento(),
                request.codCanal(),
                usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(contactChannelMapper.toResponseDTO(result));
    }

    @PutMapping("/{codContato}")
        @Operation(summary = "Atualiza dados de um contato do canal")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato do canal atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<ContactChannelResponseDTO> updateContactChannel(
            @PathVariable String codContato,
            @RequestBody UpdateContactChannelRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        ContactChannel result = contactChannelUseCase.updateContactChannel(codContato, new UpdateContactChannelCommand(
                request.codFornecedor(),
                request.codEmpregador(),
                request.desContato(),
                request.desEmailContato(),
                request.numDDD(),
                request.numFone(),
                request.numFoneRamal(),
                request.numFax(),
                request.numFaxRamal(),
                request.stEntidadeContato(),
                request.desComentarios(),
                request.codTipoDocumento(),
                request.codDocumento(),
                request.codCanal(),
                idUsuario));
        return ResponseEntity.ok(contactChannelMapper.toResponseDTO(result));
    }

    @GetMapping("/{codContato}")
    @Operation(summary = "Busca contato do canal por código")
    public ResponseEntity<ContactChannelResponseDTO> findByContactChannel(@PathVariable String codContato) {
        return ResponseEntity.ok(contactChannelMapper.toResponseDTO(contactChannelUseCase.findByContactChannel(codContato)));
    }

    @GetMapping
    @Operation(summary = "Lista contatos do canal, com filtro opcional por canal")
    public ResponseEntity<PageResponse<ContactChannelResponseDTO>> findAllContactChannels(
            @RequestParam(required = false) String codCanal,
            Pageable pageable) {
        Page<ContactChannel> page = contactChannelUseCase.findAllContactChannels(codCanal, pageable);
        return ResponseEntity.ok(PageResponse.fromPage(page.map(contactChannelMapper::toResponseDTO)));
    }

    @DeleteMapping("/{codContato}")
    @Operation(summary = "Remove um contato do canal")
    public ResponseEntity<Void> deleteContactChannel(@PathVariable String codContato) {
        contactChannelUseCase.deleteContactChannel(codContato);
        return ResponseEntity.noContent().build();
    }

 
}

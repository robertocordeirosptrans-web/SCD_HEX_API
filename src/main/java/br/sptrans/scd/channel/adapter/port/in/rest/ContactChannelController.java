package br.sptrans.scd.channel.adapter.port.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase.CreateContactChannelCommand;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase.UpdateContactChannelCommand;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/contact-channels")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contatos do Canal v1", description = "Endpoints para gerenciamento de contatos do canal")
public class ContactChannelController {

    private final ContactChannelUseCase contactChannelUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo contato do canal")
    public ResponseEntity<ContactChannel> createContactChannel(
            @RequestBody CreateContactChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
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
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codContato}")
    @Operation(summary = "Atualiza dados de um contato do canal")
    public ResponseEntity<ContactChannel> updateContactChannel(
            @PathVariable String codContato,
            @RequestBody UpdateContactChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
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
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codContato}")
    @Operation(summary = "Busca contato do canal por código")
    public ResponseEntity<ContactChannel> findByContactChannel(@PathVariable String codContato) {
        return ResponseEntity.ok(contactChannelUseCase.findByContactChannel(codContato));
    }

    @GetMapping
    @Operation(summary = "Lista contatos do canal, com filtro opcional por canal")
    public ResponseEntity<PageResponse<ContactChannel>> findAllContactChannels(
            @RequestParam(required = false) String codCanal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ContactChannel> all = contactChannelUseCase.findAllContactChannels(codCanal);
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @DeleteMapping("/{codContato}")
    @Operation(summary = "Remove um contato do canal")
    public ResponseEntity<Void> deleteContactChannel(@PathVariable String codContato) {
        contactChannelUseCase.deleteContactChannel(codContato);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateContactChannelRequest(
            String codContato,
            String codFornecedor,
            String codEmpregador,
            String desContato,
            String desEmailContato,
            Integer numDDD,
            Integer numFone,
            Integer numFoneRamal,
            Integer numFax,
            Integer numFaxRamal,
            String stEntidadeContato,
            String desComentarios,
            String codTipoDocumento,
            String codDocumento,
            String codCanal) {}

    public record UpdateContactChannelRequest(
            String codFornecedor,
            String codEmpregador,
            String desContato,
            String desEmailContato,
            Integer numDDD,
            Integer numFone,
            Integer numFoneRamal,
            Integer numFax,
            Integer numFaxRamal,
            String stEntidadeContato,
            String desComentarios,
            String codTipoDocumento,
            String codDocumento,
            String codCanal) {}
}

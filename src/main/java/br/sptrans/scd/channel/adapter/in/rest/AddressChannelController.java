package br.sptrans.scd.channel.adapter.in.rest;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import br.sptrans.scd.channel.adapter.in.rest.dto.AddressChannelResponseDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.CreateAddressChannelRequest;
import br.sptrans.scd.channel.adapter.in.rest.dto.UpdateAddressChannelRequest;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.AddressChannelMapper;
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase;
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase.CreateAddressChannelCommand;
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase.UpdateAddressChannelCommand;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/address-channels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Endereços do Canal v1", description = "Endpoints para gerenciamento de endereços do canal")
public class AddressChannelController {

    private static final Logger log = LoggerFactory.getLogger(AddressChannelController.class);

    private final AddressChannelUseCase addressChannelUseCase;
    private final UserResolverHelper userResolverHelper;
    private final AddressChannelMapper addressChannelMapper;

    @PostMapping
    @Operation(summary = "Cadastra um novo endereço do canal")
    public ResponseEntity<AddressChannelResponseDTO> createAddressChannel(
            @RequestBody CreateAddressChannelRequest request) {
        log.info("REST POST /address-channels — Criando endereço: {}", request.codEndereco());
        User usuario = userResolverHelper.getCurrentUser();
        AddressChannel result = addressChannelUseCase.createAddressChannel(new CreateAddressChannelCommand(
                request.codEndereco(),
                request.codEmpregador(),
                request.desLogradouro(),
                request.codFornecedor(),
                request.codTipoEndereco(),
                request.codCEP(),
                request.desBairro(),
                request.desCidade(),
                request.desUF(),
                request.numDDD(),
                request.numFone(),
                request.numFax(),
                request.desObs(),
                request.stEnderecos(),
                request.desNumero(),
                request.codCanalId(),
                usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(addressChannelMapper.toResponseDTO(result));
    }

    @PutMapping("/{codEndereco}")
    @Operation(summary = "Atualiza dados de um endereço do canal")
    public ResponseEntity<AddressChannelResponseDTO> updateAddressChannel(
            @PathVariable String codEndereco,
            @RequestBody UpdateAddressChannelRequest request) {
        log.info("REST PUT /address-channels/{} — Atualizando endereço", codEndereco);
        Long idUsuario = userResolverHelper.getCurrentUserId();
        AddressChannel result = addressChannelUseCase.updateAddressChannel(codEndereco, new UpdateAddressChannelCommand(
                request.codEmpregador(),
                request.desLogradouro(),
                request.codFornecedor(),
                request.codTipoEndereco(),
                request.codCEP(),
                request.desBairro(),
                request.desCidade(),
                request.desUF(),
                request.numDDD(),
                request.numFone(),
                request.numFax(),
                request.desObs(),
                request.stEnderecos(),
                request.desNumero(),
                request.codCanal(),
                idUsuario));
        return ResponseEntity.ok(addressChannelMapper.toResponseDTO(result));
    }

    @GetMapping("/{codEndereco}")
    @Operation(summary = "Busca endereço do canal por código")
    public ResponseEntity<AddressChannelResponseDTO> findByAddressChannel(@PathVariable String codEndereco) {
        return ResponseEntity.ok(addressChannelMapper.toResponseDTO(addressChannelUseCase.findByAddressChannel(codEndereco)));
    }

    @GetMapping
    @Operation(summary = "Lista endereços do canal, com filtro opcional por canal")
    public ResponseEntity<PageResponse<AddressChannelResponseDTO>> findAllAddressChannels(
            @RequestParam(required = false) String codCanal,
            Pageable pageable) {
        Page<AddressChannel> page = addressChannelUseCase.findAllAddressChannels(codCanal, pageable);
        return ResponseEntity.ok(PageResponse.fromPage(page.map(addressChannelMapper::toResponseDTO)));
    }

    @DeleteMapping("/{codEndereco}")
    @Operation(summary = "Remove um endereço do canal")
    public ResponseEntity<Void> deleteAddressChannel(@PathVariable String codEndereco) {
        log.info("REST DELETE /address-channels/{}", codEndereco);
        addressChannelUseCase.deleteAddressChannel(codEndereco);
        return ResponseEntity.noContent().build();
    }



}

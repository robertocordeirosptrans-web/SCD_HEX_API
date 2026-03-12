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
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase;
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase.CreateAddressChannelCommand;
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase.UpdateAddressChannelCommand;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.shared.dto.PageResponse;
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

    private final AddressChannelUseCase addressChannelUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo endereço do canal")
    public ResponseEntity<AddressChannel> createAddressChannel(
            @RequestBody CreateAddressChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
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
                request.codCanal(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codEndereco}")
    @Operation(summary = "Atualiza dados de um endereço do canal")
    public ResponseEntity<AddressChannel> updateAddressChannel(
            @PathVariable String codEndereco,
            @RequestBody UpdateAddressChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
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
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codEndereco}")
    @Operation(summary = "Busca endereço do canal por código")
    public ResponseEntity<AddressChannel> findByAddressChannel(@PathVariable String codEndereco) {
        return ResponseEntity.ok(addressChannelUseCase.findByAddressChannel(codEndereco));
    }

    @GetMapping
    @Operation(summary = "Lista endereços do canal, com filtro opcional por canal")
    public ResponseEntity<PageResponse<AddressChannel>> findAllAddressChannels(
            @RequestParam(required = false) String codCanal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AddressChannel> all = addressChannelUseCase.findAllAddressChannels(codCanal);
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @DeleteMapping("/{codEndereco}")
    @Operation(summary = "Remove um endereço do canal")
    public ResponseEntity<Void> deleteAddressChannel(@PathVariable String codEndereco) {
        addressChannelUseCase.deleteAddressChannel(codEndereco);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateAddressChannelRequest(
            String codEndereco,
            String codEmpregador,
            String desLogradouro,
            String codFornecedor,
            String codTipoEndereco,
            String codCEP,
            String desBairro,
            String desCidade,
            String desUF,
            Integer numDDD,
            Integer numFone,
            Integer numFax,
            String desObs,
            String stEnderecos,
            String desNumero,
            String codCanal) {}

    public record UpdateAddressChannelRequest(
            String codEmpregador,
            String desLogradouro,
            String codFornecedor,
            String codTipoEndereco,
            String codCEP,
            String desBairro,
            String desCidade,
            String desUF,
            Integer numDDD,
            Integer numFone,
            Integer numFax,
            String desObs,
            String stEnderecos,
            String desNumero,
            String codCanal) {}
}

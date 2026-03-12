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
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase.CreateProductChannelCommand;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase.UpdateProductChannelCommand;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/product-channels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais de Produto v1", description = "Endpoints para gerenciamento de canais de produto")
public class ProductChannelController {

    private final ProductChannelUseCase productChannelUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo canal de produto")
    public ResponseEntity<ProductChannel> createProductChannel(
            @RequestBody CreateProductChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        ProductChannel result = productChannelUseCase.createProductChannel(
                new CreateProductChannelCommand(
                        request.codCanal(),
                        request.codProduto(),
                        request.qtdLimiteComercializacao(),
                        request.qtdMinimaEstoque(),
                        request.qtdMaximaEstoque(),
                        request.qtdMinimaRessuprimento(),
                        request.qtdMaximaRessuprimento(),
                        request.codOrgaoEmissor(),
                        request.vlFace(),
                        request.codStatus(),
                        request.codConvenio(),
                        request.tipoOperHM(),
                        request.flgCarac(),
                        idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Atualiza um canal de produto")
    public ResponseEntity<ProductChannel> updateProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            @RequestBody UpdateProductChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        ProductChannel result = productChannelUseCase.updateProductChannel(codCanal, codProduto,
                new UpdateProductChannelCommand(
                        request.qtdLimiteComercializacao(),
                        request.qtdMinimaEstoque(),
                        request.qtdMaximaEstoque(),
                        request.qtdMinimaRessuprimento(),
                        request.qtdMaximaRessuprimento(),
                        request.codOrgaoEmissor(),
                        request.vlFace(),
                        request.codStatus(),
                        request.codConvenio(),
                        request.tipoOperHM(),
                        request.flgCarac(),
                        idUsuario));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Busca canal de produto por canal e produto")
    public ResponseEntity<ProductChannel> findProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        return ResponseEntity.ok(productChannelUseCase.findProductChannel(codCanal, codProduto));
    }

    @GetMapping
    @Operation(summary = "Lista canais de produto com filtro opcional por canal ou produto")
    public ResponseEntity<PageResponse<ProductChannel>> findProductChannels(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) String codProduto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ProductChannel> all;
        if (codCanal != null) {
            all = productChannelUseCase.findByCodCanal(codCanal);
        } else if (codProduto != null) {
            all = productChannelUseCase.findByCodProduto(codProduto);
        } else {
            all = productChannelUseCase.findAllProductChannels();
        }
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @DeleteMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Remove um canal de produto")
    public ResponseEntity<Void> deleteProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        productChannelUseCase.deleteProductChannel(codCanal, codProduto);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────

    public record CreateProductChannelRequest(
            String codCanal,
            String codProduto,
            Integer qtdLimiteComercializacao,
            Integer qtdMinimaEstoque,
            Integer qtdMaximaEstoque,
            Integer qtdMinimaRessuprimento,
            Integer qtdMaximaRessuprimento,
            Integer codOrgaoEmissor,
            Integer vlFace,
            String codStatus,
            Integer codConvenio,
            Integer tipoOperHM,
            String flgCarac) {}

    public record UpdateProductChannelRequest(
            Integer qtdLimiteComercializacao,
            Integer qtdMinimaEstoque,
            Integer qtdMaximaEstoque,
            Integer qtdMinimaRessuprimento,
            Integer qtdMaximaRessuprimento,
            Integer codOrgaoEmissor,
            Integer vlFace,
            String codStatus,
            Integer codConvenio,
            Integer tipoOperHM,
            String flgCarac) {}
}

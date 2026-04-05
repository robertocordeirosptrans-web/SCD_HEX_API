package br.sptrans.scd.product.adapter.port.in.rest;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.product.application.port.in.ProductUseCase;
import br.sptrans.scd.product.application.port.in.ProductUseCase.CreateProductCommand;
import br.sptrans.scd.product.application.port.in.ProductUseCase.CreateVersionCommand;
import br.sptrans.scd.product.application.port.in.ProductUseCase.UpdateProductCommand;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ProductVersion;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/product")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Produtos v1", description = "Endpoints para gerenciamento de produtos - Versão 1")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final UserPersistencePort userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo produto")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
        @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> createProduct(
            @RequestBody CreateProductRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        productUseCase.createProduct(new CreateProductCommand(
                request.codProduto(),
                request.desProduto(),
                request.desEmissorResponsavel(),
                request.desUtilizacao(),
                request.flgBloqFabricacao(),
                request.flgBloqVenda(),
                request.flgBloqDistribuicao(),
                request.flgBloqTroca(),
                request.flgBloqAquisicao(),
                request.flgBloqPedido(),
                request.flgBloqDevolucao(),
                request.flgInicializado(),
                request.flgComercializado(),
                request.flgRestManual(),
                request.codEntidade(),
                request.codTipoCartao(),
                request.codClassificacaoPessoa(),
                request.codTipoProduto(),
                request.codTecnologia(),
                request.codModalidade(),
                request.codFamilia(),
                request.codEspecie() ,
                idUsuario
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos, com filtro opcional de status")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<Product>> findAllProducts(
            @RequestParam(required = false) String codStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Product> all = productUseCase.findAllProducts(codStatus);
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @GetMapping("/{codProduto}")

    @Operation(summary = "Busca produto por código")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> findByProduct(@PathVariable String codProduto) {
        return ResponseEntity.ok(productUseCase.findByProduct(codProduto));
    }

    @PutMapping("/{codProduto}")
    @Operation(summary = "Atualiza dados de um produto")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateProduct(
            @PathVariable String codProduto,
            @RequestBody UpdateProductRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        productUseCase.updateProduct(codProduto, new UpdateProductCommand(
                request.desProduto(),
                request.desEmissorResponsavel(),
                request.desUtilizacao(),
                request.flgBloqFabricacao(),
                request.flgBloqVenda(),
                request.flgBloqDistribuicao(),
                request.flgBloqTroca(),
                request.flgBloqAquisicao(),
                request.flgBloqPedido(),
                request.flgBloqDevolucao(),
                request.flgInicializado(),
                request.flgComercializado(),
                request.flgRestManual(),
                request.codEntidade(),
                request.codTipoCartao(),
                request.codClassificacaoPessoa(),
                request.codTipoProduto(),
                request.codTecnologia(),
                request.codModalidade(),
                request.codFamilia(),
                request.codEspecie(),
                idUsuario
        ));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codProduto}/activate")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ativa um produto")
    public ResponseEntity<Void> activateProduct(
            @PathVariable String codProduto,
            Authentication authentication) {
        productUseCase.activateProduct(codProduto, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codProduto}/inactivate")
    @Operation(summary = "Inativa um produto")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> inactivateProduct(
            @PathVariable String codProduto,
            Authentication authentication) {
        productUseCase.inactivateProduct(codProduto, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    // ── Versões ───────────────────────────────────────────────────────────────
    @PostMapping("/{codProduto}/versions")
    @Operation(summary = "Cria uma nova versão para um produto")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductVersion> createNewVersion(
            @PathVariable String codProduto,
            @RequestBody CreateVersionRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        ProductVersion version = productUseCase.createNewVersion(codProduto, new CreateVersionCommand(
                request.dtValidade(),
                request.dtVidaInicio(),
                request.dtVidaFim(),
                request.dtLiberacao(),
                request.dtLancamento(),
                request.dtVendaInicio(),
                request.dtVendaFim(),
                request.dtUsoIni(),
                request.dtUsoFim(),
                request.dtTrocaIni(),
                request.dtTrocaFim(),
                request.flgBloqFabricacao(),
                request.flgBloqVenda(),
                request.flgBloqDistribuicao(),
                request.flgBloqTroca(),
                request.flgBloqAquisicao(),
                request.flgBloqPedido(),
                request.flgBloqDevolucao(),
                request.desProdutoVersoes(),
                idUsuario
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(version);
    }

    @GetMapping("/versions/{codVersao}")
    @Operation(summary = "Busca uma versão de produto por código")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductVersion> findByVersion(@PathVariable String codVersao) {
        return ResponseEntity.ok(productUseCase.findByVersion(codVersao));
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateProductRequest(
            String codProduto,
            String desProduto,
            String desEmissorResponsavel,
            String desUtilizacao,
            String flgBloqFabricacao,
            String flgBloqVenda,
            String flgBloqDistribuicao,
            String flgBloqTroca,
            String flgBloqAquisicao,
            String flgBloqPedido,
            String flgBloqDevolucao,
            String flgInicializado,
            String flgComercializado,
            String flgRestManual,
            String codEntidade,
            String codTipoCartao,
            String codClassificacaoPessoa,
            String codTipoProduto,
            String codTecnologia,
            String codModalidade,
            String codFamilia,
            String codEspecie
            ) {

    }

    public record UpdateProductRequest(
            String desProduto,
            String desEmissorResponsavel,
            String desUtilizacao,
            String flgBloqFabricacao,
            String flgBloqVenda,
            String flgBloqDistribuicao,
            String flgBloqTroca,
            String flgBloqAquisicao,
            String flgBloqPedido,
            String flgBloqDevolucao,
            String flgInicializado,
            String flgComercializado,
            String flgRestManual,
            String codEntidade,
            String codTipoCartao,
            String codClassificacaoPessoa,
            String codTipoProduto,
            String codTecnologia,
            String codModalidade,
            String codFamilia,
            String codEspecie
            ) {

    }

    public record CreateVersionRequest(
            LocalDateTime dtValidade,
            LocalDateTime dtVidaInicio,
            LocalDateTime dtVidaFim,
            LocalDateTime dtLiberacao,
            LocalDateTime dtLancamento,
            LocalDateTime dtVendaInicio,
            LocalDateTime dtVendaFim,
            LocalDateTime dtUsoIni,
            LocalDateTime dtUsoFim,
            LocalDateTime dtTrocaIni,
            LocalDateTime dtTrocaFim,
            String flgBloqFabricacao,
            String flgBloqVenda,
            String flgBloqDistribuicao,
            String flgBloqTroca,
            String flgBloqAquisicao,
            String flgBloqPedido,
            String flgBloqDevolucao,
            String desProdutoVersoes
            ) {

    }
}
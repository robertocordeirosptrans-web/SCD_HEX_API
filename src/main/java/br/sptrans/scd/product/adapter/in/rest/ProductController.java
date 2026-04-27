package br.sptrans.scd.product.adapter.in.rest;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.product.adapter.in.rest.dto.ProductRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.ProductResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.ProductVersionDetailResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.ProductVersionRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductMapper;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.adapter.specification.ProductSpecification;
import br.sptrans.scd.product.application.port.in.ProductUseCase;
import br.sptrans.scd.product.application.port.in.ProductUseCase.CreateProductCommand;
import br.sptrans.scd.product.application.port.in.ProductUseCase.CreateVersionCommand;
import br.sptrans.scd.product.application.port.in.ProductUseCase.UpdateProductCommand;
import br.sptrans.scd.product.domain.CardType;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ProductVersion;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CadPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/product")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Produtos v1", description = "Endpoints para gerenciamento de produtos - Versão 1")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final UserResolverHelper userResolverHelper;
    private final ProductMapper productMapper;

    @PostMapping
    @Operation(summary = "Cadastra um novo produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_CADPRO + "')")

    public ResponseEntity<Void> createProduct(
            @Valid @RequestBody ProductRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
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
                request.codEspecie(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos, com filtros dinâmicos")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_LISPRO + "')")
    public ResponseEntity<PageResponse<ProductResponseDTO>> findAllProducts(
            @RequestParam Map<String, String> filters,
            Pageable pageable) {
        Specification<ProductEntityJpa> spec = ProductSpecification.filterProducts(filters);
        Page<Product> page = productUseCase.findAllProducts(spec, pageable);
        Page<ProductResponseDTO> dtoPage = page.map(productMapper::toResponseDTO);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/tp_cartoes")
    @Operation(summary = "Lista todos os tipos de cartões")
    public ResponseEntity<List<CardType>> getTypeCards() {
        return ResponseEntity.ok(productUseCase.findAllCardTypes());
    }

    @GetMapping("/{codProduto}")
    @Operation(summary = "Busca produto por código")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_BUSPROPORCOD + "')")
    public ResponseEntity<Product> findByProduct(@PathVariable String codProduto) {

        return ResponseEntity.ok(productUseCase.findByProduct(codProduto));
    }

    @PutMapping("/{codProduto}")
    @Operation(summary = "Atualiza dados de um produto")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_ATUPRO + "')")
    public ResponseEntity<Void> updateProduct(
            @PathVariable String codProduto,
            @Valid @RequestBody ProductRequest request) {

        Long idUsuario = userResolverHelper.getCurrentUserId();
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
                idUsuario));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codProduto}/activate")

    @Operation(summary = "Ativa um produto")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_ATIPRO + "')")
    public ResponseEntity<Void> activateProduct(
            @PathVariable String codProduto) {

        productUseCase.activateProduct(codProduto, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codProduto}/inactivate")
    @Operation(summary = "Inativa um produto")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_INAPRO + "')")
    public ResponseEntity<Void> inactivateProduct(
            @PathVariable String codProduto) {
        productUseCase.inactivateProduct(codProduto, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    // ── Versões ───────────────────────────────────────────────────────────────
    @PostMapping("/{codProduto}/versions")
    @Operation(summary = "Cria uma nova versão para um produto")

    public ResponseEntity<ProductVersion> createNewVersion(
            @PathVariable String codProduto,
            @Valid @RequestBody ProductVersionRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        ProductVersion version = productUseCase.createNewVersion(codProduto, new CreateVersionCommand(
                request.dtValidade(),
                request.dtVidaInicio(),
                request.dtVidaFim(),
                request.dtLiberacao(),
                request.dtLancamento(),
                request.dtVendaInicio(),
                request.dtVendaFim(),
                request.dtUsoInicio(),
                request.dtUsoFim(),
                request.dtTrocaInicio(),
                request.dtTrocaFim(),
                request.flgBloqFabricacao(),
                request.flgBloqVenda(),
                request.flgBloqDistribuicao(),
                request.flgBloqTroca(),
                request.flgBloqAquisicao(),
                request.flgBloqPedido(),
                request.flgBloqDevolucao(),
                request.desProdutoVersoes(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(version);
    }

    @GetMapping("/{codProduto}/versions")
    @Operation(summary = "Lista o histórico de versionamento de um produto")
    public ResponseEntity<PageResponse<ProductVersionDetailResponseDTO>> findAllVersion(
            @PathVariable String codProduto,
            Pageable pageable) {
        var dtoPage = productUseCase.findAllVersion(codProduto, pageable)
                .map(v -> ProductVersionDetailResponseDTO.builder()
                        .codVersao(v.getCodVersao())
                        .codProduto(v.getCodProduto())
                        .dtValidade(v.getDtValidade())
                        .dtVidaInicio(v.getDtVidaInicio())
                        .dtVidaFim(v.getDtVidaFim())
                        .dtLiberacao(v.getDtLiberacao())
                        .dtLancamento(v.getDtLancamento())
                        .dtVendaInicio(v.getDtVendaInicio())
                        .dtVendaFim(v.getDtVendaFim())
                        .dtUsoIni(v.getDtUsoInicio())
                        .dtUsoFim(v.getDtUsoFim())
                        .dtTrocaIni(v.getDtTrocaInicio())
                        .dtTrocaFim(v.getDtTrocaFim())
                        .flgBloqFabricacao(v.getFlgBloqFabricacao())
                        .flgBloqVenda(v.getFlgBloqVenda())
                        .flgBloqDistribuicao(v.getFlgBloqDistribuicao())
                        .flgBloqTroca(v.getFlgBloqTroca())
                        .flgBloqAquisicao(v.getFlgBloqAquisicao())
                        .flgBloqPedido(v.getFlgBloqPedido())
                        .flgBloqDevolucao(v.getFlgBloqDevolucao())
                        .dtCadastro(v.getDtCadastro())
                        .dtManutencao(v.getDtManutencao())
                        .stProdutosVersoes(v.getCodStatus())
                        .desProdutoVersoes(v.getDesProdutoVersoes())
                        .usuarioCadastro(UserSimpleMapper.toDto(v.getIdUsuarioCadastro()))
                        .usuarioManutencao(UserSimpleMapper.toDto(v.getIdUsuarioManutencao()))
                        .build());
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

}
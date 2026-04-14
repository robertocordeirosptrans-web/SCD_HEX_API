package br.sptrans.scd.product.adapter.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.product.adapter.in.rest.dto.ProductsTypeResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.CreateProductsTypeCommand;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.UpdateProductsTypeCommand;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/products-types")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tipos de Produto v1", description = "Endpoints para gerenciamento de tipos de produto")
public class ProductsTypeController {

    private final ProductsTypeManagementUseCase productsTypeManagementUseCase;
    private final UserResolverHelper userResolverHelper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_CADTIPDEPRO + "')")
    @Operation(summary = "Cadastra um novo tipo de produto")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
            public ResponseEntity<ProductType> createProductsType(
                @Valid @RequestBody br.sptrans.scd.product.adapter.in.rest.dto.ProductTypesRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        ProductType productType = productsTypeManagementUseCase.create(
            new CreateProductsTypeCommand(
                request.codTipoProduto(),
                request.desTipoProduto(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(productType);
        }

    @PutMapping("/{codTipoProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_ATUTIPDEPRO + "')")
    @Operation(summary = "Atualiza dados de um tipo de produto")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
            public ResponseEntity<ProductType> updateProductsType(
                @PathVariable String codTipoProduto,
                @Valid @RequestBody br.sptrans.scd.product.adapter.in.rest.dto.ProductTypesRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        ProductType productType = productsTypeManagementUseCase.update(codTipoProduto,
            new UpdateProductsTypeCommand(request.desTipoProduto(), idUsuario));
        return ResponseEntity.ok(productType);
        }

    @GetMapping("/{codTipoProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_BUSTIPDEPROPORCOD + "')")
    @Operation(summary = "Busca tipo de produto por código")
    public ResponseEntity<ProductsTypeResponseDTO> findByProductsType(@PathVariable String codTipoProduto) {
        ProductType productType = productsTypeManagementUseCase.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_TYPE_NOT_FOUND));
        ProductsTypeResponseDTO dto = new ProductsTypeResponseDTO(
            productType.getCodTipoProduto(),
            productType.getDesTipoProduto(),
            productType.getCodStatus(),
            productType.getDtCadastro(),
            productType.getDtManutencao(),
            UserSimpleMapper.toDto(productType.getIdUsuarioCadastro()),
            UserSimpleMapper.toDto(productType.getIdUsuarioManutencao())
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_LISTIPDEPRO + "')")
    @Operation(summary = "Lista todos os tipos de produto, com filtro opcional de status")
    public ResponseEntity<PageResponse<ProductsTypeResponseDTO>> findAllProductsTypes(
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        Page<ProductsTypeResponseDTO> dtoPage = productsTypeManagementUseCase.findAll(codStatus, pageable)
            .map(productType -> new ProductsTypeResponseDTO(
                productType.getCodTipoProduto(),
                productType.getDesTipoProduto(),
                productType.getCodStatus(),
                productType.getDtCadastro(),
                productType.getDtManutencao(),
                UserSimpleMapper.toDto(productType.getIdUsuarioCadastro()),
                UserSimpleMapper.toDto(productType.getIdUsuarioManutencao())
            ));
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PatchMapping("/{codTipoProduto}/activate")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_ATITIPDEPRO + "')")
    @Operation(summary = "Ativa um tipo de produto")
    public ResponseEntity<Void> activateProductsType(
            @PathVariable String codTipoProduto) {
        productsTypeManagementUseCase.activate(codTipoProduto, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codTipoProduto}/inactivate")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_INATIPDEPRO + "')")
    @Operation(summary = "Inativa um tipo de produto")
    public ResponseEntity<Void> inactivateProductsType(
            @PathVariable String codTipoProduto) {
        productsTypeManagementUseCase.inactivate(codTipoProduto, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codTipoProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.PRO_REMTIPDEPRO + "')")
    @Operation(summary = "Remove um tipo de produto")
    public ResponseEntity<Void> deleteProductsType(@PathVariable String codTipoProduto) {
        productsTypeManagementUseCase.delete(codTipoProduto);
        return ResponseEntity.noContent().build();
    }
}

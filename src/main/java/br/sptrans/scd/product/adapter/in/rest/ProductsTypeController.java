package br.sptrans.scd.product.adapter.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.CreateProductsTypeCommand;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.UpdateProductsTypeCommand;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/products-types")
@RequiredArgsConstructor
@Tag(name = "Tipos de Produto v1", description = "Endpoints para gerenciamento de tipos de produto")
public class ProductsTypeController {

    private final ProductsTypeManagementUseCase productsTypeManagementUseCase;

    @PostMapping
    @Operation(summary = "Cadastra um novo tipo de produto")
    public ResponseEntity<ProductType> createProductsType(@RequestBody CreateProductsTypeCommand command) {
        ProductType productType = productsTypeManagementUseCase.createProductsType(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(productType);
    }

    @PutMapping("/{codTipoProduto}")
    @Operation(summary = "Atualiza dados de um tipo de produto")
    public ResponseEntity<ProductType> updateProductsType(
            @PathVariable String codTipoProduto,
            @RequestBody UpdateProductsTypeCommand command) {
        ProductType productType = productsTypeManagementUseCase.updateProductsType(codTipoProduto, command);
        return ResponseEntity.ok(productType);
    }

    @GetMapping("/{codTipoProduto}")
    @Operation(summary = "Busca tipo de produto por código")
    public ResponseEntity<ProductType> findByProductsType(@PathVariable String codTipoProduto) {
        return ResponseEntity.ok(productsTypeManagementUseCase.findByProductsType(codTipoProduto));
    }

    @GetMapping
    @Operation(summary = "Lista todos os tipos de produto, com filtro opcional de status")
    public ResponseEntity<List<ProductType>> findAllProductsTypes(
            @RequestParam(required = false) String codStatus) {
        return ResponseEntity.ok(productsTypeManagementUseCase.findAllProductsTypes(codStatus));
    }

    @PatchMapping("/{codTipoProduto}/activate")
    @Operation(summary = "Ativa um tipo de produto")
    public ResponseEntity<Void> activateProductsType(
            @PathVariable String codTipoProduto,
            @RequestParam Long idUsuario) {
        productsTypeManagementUseCase.activateProductsType(codTipoProduto, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codTipoProduto}/inactivate")
    @Operation(summary = "Inativa um tipo de produto")
    public ResponseEntity<Void> inactivateProductsType(
            @PathVariable String codTipoProduto,
            @RequestParam Long idUsuario) {
        productsTypeManagementUseCase.inactivateProductsType(codTipoProduto, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codTipoProduto}")
    @Operation(summary = "Remove um tipo de produto")
    public ResponseEntity<Void> deleteProductsType(@PathVariable String codTipoProduto) {
        productsTypeManagementUseCase.deleteProductsType(codTipoProduto);
        return ResponseEntity.noContent().build();
    }
}

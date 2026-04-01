package br.sptrans.scd.product.adapter.port.in.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.adapter.port.in.rest.dto.ProductsTypeResponseDTO;
import br.sptrans.scd.product.adapter.port.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.CreateProductsTypeCommand;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.UpdateProductsTypeCommand;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/products-types")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tipos de Produto v1", description = "Endpoints para gerenciamento de tipos de produto")
public class ProductsTypeController {

    private final ProductsTypeManagementUseCase productsTypeManagementUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo tipo de produto")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<ProductType> createProductsType(
            @RequestBody CreateProductsTypeRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        ProductType productType = productsTypeManagementUseCase.createProductsType(
                new CreateProductsTypeCommand(request.codTipoProduto(), request.desTipoProduto(), idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(productType);
    }

    @PutMapping("/{codTipoProduto}")
    @Operation(summary = "Atualiza dados de um tipo de produto")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<ProductType> updateProductsType(
            @PathVariable String codTipoProduto,
            @RequestBody UpdateProductsTypeRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        ProductType productType = productsTypeManagementUseCase.updateProductsType(codTipoProduto,
                new UpdateProductsTypeCommand(request.desTipoProduto(), idUsuario));
        return ResponseEntity.ok(productType);
    }

    @GetMapping("/{codTipoProduto}")
    @Operation(summary = "Busca tipo de produto por código")
    public ResponseEntity<ProductsTypeResponseDTO> findByProductsType(@PathVariable String codTipoProduto) {
        ProductType productType = productsTypeManagementUseCase.findByProductsType(codTipoProduto);
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
    @Operation(summary = "Lista todos os tipos de produto, com filtro opcional de status")
    public ResponseEntity<PageResponse<ProductsTypeResponseDTO>> findAllProductsTypes(
            @RequestParam(required = false) String codStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ProductType> all = productsTypeManagementUseCase.findAllProductsTypes(codStatus);
        List<ProductsTypeResponseDTO> dtos = all.stream()
            .map(productType -> new ProductsTypeResponseDTO(
                productType.getCodTipoProduto(),
                productType.getDesTipoProduto(),
                productType.getCodStatus(),
                productType.getDtCadastro(),
                productType.getDtManutencao(),
                UserSimpleMapper.toDto(productType.getIdUsuarioCadastro()),
                UserSimpleMapper.toDto(productType.getIdUsuarioManutencao())
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
    }

    @PatchMapping("/{codTipoProduto}/activate")
    @Operation(summary = "Ativa um tipo de produto")
    public ResponseEntity<Void> activateProductsType(
            @PathVariable String codTipoProduto,
            Authentication authentication) {
        productsTypeManagementUseCase.activateProductsType(codTipoProduto, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codTipoProduto}/inactivate")
    @Operation(summary = "Inativa um tipo de produto")
    public ResponseEntity<Void> inactivateProductsType(
            @PathVariable String codTipoProduto,
            Authentication authentication) {
        productsTypeManagementUseCase.inactivateProductsType(codTipoProduto, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codTipoProduto}")
    @Operation(summary = "Remove um tipo de produto")
    public ResponseEntity<Void> deleteProductsType(@PathVariable String codTipoProduto) {
        productsTypeManagementUseCase.deleteProductsType(codTipoProduto);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateProductsTypeRequest(String codTipoProduto, String desTipoProduto) {}
    public record UpdateProductsTypeRequest(String desTipoProduto) {}
}

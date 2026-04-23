package br.sptrans.scd.channel.adapter.in.rest;

import java.util.List;

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
import br.sptrans.scd.channel.adapter.in.rest.dto.ChannelByProductDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.CreateProductChannelRequest;
import br.sptrans.scd.channel.adapter.in.rest.dto.ProductChDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.ProductChResponseDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.UpdateProductChannelRequest;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.ProductChannelMapper;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase.CreateProductChannelCommand;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase.UpdateProductChannelCommand;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.product.adapter.in.rest.dto.UserSimpleMapper;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/product-channels")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais de Produto v1", description = "Endpoints para gerenciamento de canais de produto")

public class ProductChannelController {


    private static final Logger log = LoggerFactory.getLogger(ProductChannelController.class);

    private final ProductChannelUseCase productChannelUseCase;
    private final UserResolverHelper userResolverHelper;
    private final ProductChannelMapper productChannelMapper;

        @PostMapping
        @PreAuthorize("hasAuthority('" + CadPermissions.ASS_CADASS + "')")
    @Operation(summary = "Cadastra um novo canal de produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal de produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ProductChannel> createProductChannel(
            @Valid @RequestBody CreateProductChannelRequest request) {
        log.info("REST POST /product-channels — Canal: {}, Produto: {}", request.codCanal(), request.codProduto());
        User usuario = userResolverHelper.getCurrentUser();
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
                        request.codTipoOperHM(),
                        request.flgCarac(),
                        usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    
    @GetMapping("/available-products")
    @PreAuthorize("hasAuthority('" + CadPermissions.ASS_LISASS + "')")
    @Operation(summary = "Lista produtos disponíveis por canal, status e tipo")
    public ResponseEntity<List<br.sptrans.scd.channel.adapter.in.rest.dto.CodigoDescricaoDTO>> findAvailableProductsByChannel(
            @RequestParam String codCanal,
            @RequestParam String stCanaisProdutos,
            @RequestParam String stProdutos) {
        var produtos = productChannelUseCase.findProdutosCodigoDescricaoByChannel(codCanal, stCanaisProdutos, stProdutos)
            .stream()
            .map(dto -> new br.sptrans.scd.channel.adapter.in.rest.dto.CodigoDescricaoDTO(dto.getCodigo(), dto.getDescricao()))
            .toList();
        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(produtos);
    }

        @PutMapping("/{codCanal}/{codProduto}")
        @PreAuthorize("hasAuthority('" + CadPermissions.ASS_ATUASS + "')")
    @Operation(summary = "Atualiza um canal de produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal de produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ProductChannel> updateProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            @Valid @RequestBody UpdateProductChannelRequest request) {
        log.info("REST PUT /product-channels/{}/{} — Atualizando", codCanal, codProduto);
        User usuario = userResolverHelper.getCurrentUser();
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
                        request.codTipoOperHM(),
                        request.flgCarac(),
                        usuario));
        return ResponseEntity.ok(result);
    }

        @GetMapping("/{codCanal}/{codProduto}")
        @PreAuthorize("hasAuthority('" + CadPermissions.ASS_BUSASSPORCOD + "')")
    @Operation(summary = "Busca canal de produto por canal e produto")
    public ResponseEntity<PageResponse<ProductChDTO>> findProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            Pageable pageable) {
        Page<ProductChannel> pageResult = productChannelUseCase.findAllProductChannels(pageable);

        Page<ProductChDTO> dtoPage = pageResult.map(channel -> new ProductChDTO(
                channel.getQtdLimiteComercializacao(),
                channel.getQtdMinimaEstoque(),
                channel.getQtdMaximaEstoque(),
                channel.getQtdMinimaRessuprimento(),
                channel.getQtdMaximaRessuprimento(),
                channel.getCodOrgaoEmissor(),
                channel.getVlFace(),
                channel.getCodStatus(),
                channel.getDtCadastro() != null ? channel.getDtCadastro().toString() : null,
                channel.getDtManutencao() != null ? channel.getDtManutencao().toString() : null,
                channel.getCodConvenio(),
                channel.getCodTipoOperHM(),
                channel.getFlgCarac(),
                channel.getId() != null ? channel.getId().getCodProduto() : null,
                channel.getId() != null ? channel.getId().getCodCanal() : null,
                UserSimpleMapper.toDto(channel.getIdUsuarioCadastro()),
                UserSimpleMapper.toDto(channel.getIdUsuarioManutencao())));

        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

        @GetMapping
        @PreAuthorize("hasAuthority('" + CadPermissions.ASS_LISASS + "')")
    @Operation(summary = "Lista canais de produto com filtro opcional por canal ou produto")
    public ResponseEntity<PageResponse<ProductChResponseDTO>> findProductChannels(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) String codProduto,
            Pageable pageable) {
        Page<ProductChResponseDTO> dtoPage = productChannelUseCase.findProjections(codCanal, codProduto, pageable)
                .map(productChannelMapper::toResponseDTO);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

        @DeleteMapping("/{codCanal}/{codProduto}")
        @PreAuthorize("hasAuthority('" + CadPermissions.ASS_REMASS + "')")
    @Operation(summary = "Remove um canal de produto")
    public ResponseEntity<Void> deleteProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        log.info("REST DELETE /product-channels/{}/{}", codCanal, codProduto);
        productChannelUseCase.deleteProductChannel(codCanal, codProduto);
        return ResponseEntity.noContent().build();
    }

        @GetMapping("/by-product/{codProduto}")
        @PreAuthorize("hasAuthority('" + CadPermissions.ASS_BUSASSPORCOD + "')")
    @Operation(summary = "Lista os canais e vigências de convênio de um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canais encontrados"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<PageResponse<ChannelByProductDTO>> findChannelsByProduct(
            @PathVariable String codProduto,
            Pageable pageable) {
        log.info("REST GET /product-channels/by-product/{}", codProduto);
        var page = productChannelUseCase.findChannelsByProduct(codProduto, pageable);
        if (page.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var dtoPage = page.map(p -> new ChannelByProductDTO(
                p.getCodCanal(),
                p.getDesCanal(),
                p.getDtInicioValidade(),
                p.getDtFimValidade(),
                p.getCodStatus()));
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

}

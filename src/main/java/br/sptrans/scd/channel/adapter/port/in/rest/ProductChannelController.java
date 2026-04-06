package br.sptrans.scd.channel.adapter.port.in.rest;

import java.util.List;
import java.util.stream.Collectors;

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
import br.sptrans.scd.channel.adapter.port.in.rest.dto.CreateProductChannelRequest;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.ProductChDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.ProductChResponseDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UpdateProductChannelRequest;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ProductChannelMapper;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase.CreateProductChannelCommand;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase.UpdateProductChannelCommand;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.product.adapter.port.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final UserResolverHelper userResolverHelper;
    private final ProductChannelMapper productChannelMapper;


    @PostMapping
    @Operation(summary = "Cadastra um novo canal de produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal de produto cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
        public ResponseEntity<ProductChannel> createProductChannel(
            @RequestBody CreateProductChannelRequest request) {
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

    @PutMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Atualiza um canal de produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal de produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
        public ResponseEntity<ProductChannel> updateProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            @RequestBody UpdateProductChannelRequest request) {
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
            UserSimpleMapper.toDto(channel.getIdUsuarioManutencao())
        ));

        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping
    @Operation(summary = "Lista canais de produto com filtro opcional por canal ou produto")
    public ResponseEntity<PageResponse<ProductChResponseDTO>> findProductChannels(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) String codProduto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ProductChannelProjection> projections = productChannelUseCase.findProjections(codCanal, codProduto);
        List<ProductChResponseDTO> dtos = productChannelMapper.toResponseDTOList(projections);
        return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
    }

    @DeleteMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Remove um canal de produto")
    public ResponseEntity<Void> deleteProductChannel(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        productChannelUseCase.deleteProductChannel(codCanal, codProduto);
        return ResponseEntity.noContent().build();
    }


}

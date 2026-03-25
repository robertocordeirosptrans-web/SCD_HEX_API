package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;

public interface ProductChannelUseCase {

    ProductChannel createProductChannel(CreateProductChannelCommand command);

    ProductChannel updateProductChannel(String codCanal, String codProduto, UpdateProductChannelCommand command);

    ProductChannel findProductChannel(String codCanal, String codProduto);

    List<ProductChannel> findAllProductChannels();

    List<ProductChannel> findByCodCanal(String codCanal);

    List<ProductChannel> findByCodProduto(String codProduto);

    List<ProductChannelProjection>findProjections( String codCanal, String codProduto);

    void deleteProductChannel(String codCanal, String codProduto);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateProductChannelCommand(
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
            String flgCarac,
            Long idUsuarioCadastro) {}

    record UpdateProductChannelCommand(
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
            String flgCarac,
            Long idUsuarioManutencao) {}
}

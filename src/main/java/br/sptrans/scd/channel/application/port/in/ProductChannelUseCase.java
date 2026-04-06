package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

public interface ProductChannelUseCase {

        ProductChannel createProductChannel(CreateProductChannelCommand command);

        ProductChannel updateProductChannel(String codCanal, String codProduto, UpdateProductChannelCommand command);

        ProductChannel findProductChannel(String codCanal, String codProduto);

        List<ProductChannel> findAllProductChannels();

        List<ProductChannel> findByCodCanal(String codCanal);

        List<ProductChannel> findByCodProduto(String codProduto);

        List<ProductChannelProjection> findProjections(String codCanal, String codProduto);

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
                        ChannelDomainStatus codStatus,
                        Integer codConvenio,
                        Integer codTipoOperHM,
                        String flgCarac,
                        User usuarioCadastro) {
        }

        record UpdateProductChannelCommand(
                        Integer qtdLimiteComercializacao,
                        Integer qtdMinimaEstoque,
                        Integer qtdMaximaEstoque,
                        Integer qtdMinimaRessuprimento,
                        Integer qtdMaximaRessuprimento,
                        Integer codOrgaoEmissor,
                        Integer vlFace,
                        ChannelDomainStatus codStatus,
                        Integer codConvenio,
                        Integer codTipoOperHM,
                        String flgCarac,
                        User usuarioManutencao) {
        }
}

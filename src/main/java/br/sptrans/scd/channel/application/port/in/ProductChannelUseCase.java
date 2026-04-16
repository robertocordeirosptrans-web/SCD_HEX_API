package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.out.query.ChannelByProductProjection;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

public interface ProductChannelUseCase {

        ProductChannel createProductChannel(CreateProductChannelCommand command);

        ProductChannel updateProductChannel(String codCanal, String codProduto, UpdateProductChannelCommand command);

        ProductChannel findProductChannel(String codCanal, String codProduto);

        Page<ProductChannel> findAllProductChannels(Pageable pageable);

        Page<ProductChannel> findByCodCanal(String codCanal, Pageable pageable);

        Page<ProductChannel> findByCodProduto(String codProduto, Pageable pageable);

        List<ProductChannelProjection> findProjections(String codCanal, String codProduto);

        Page<ProductChannelProjection> findProjections(String codCanal, String codProduto, Pageable pageable);

        List<ChannelByProductProjection> findChannelsByProduct(String codProduto);

        Page<ChannelByProductProjection> findChannelsByProduct(String codProduto, Pageable pageable);

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

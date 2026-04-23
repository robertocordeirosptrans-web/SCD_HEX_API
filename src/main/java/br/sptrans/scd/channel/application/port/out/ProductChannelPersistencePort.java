
package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.application.port.out.query.ChannelByProductProjection;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;

public interface ProductChannelPersistencePort {

    /**
     * Busca produtos disponíveis por canal, status e tipo, retornando apenas código
     * e descrição.
     */
    List<br.sptrans.scd.channel.application.port.out.dto.ProdutoCodigoDescricaoDTO> findProdutosCodigoDescricaoByChannel(
            String codCanal, String stCanaisProdutos, String stProdutos);



    Optional<ProductChannel> findById(ProductChannelKey id);

    Page<ProductChannel> findAll(Pageable pageable);

    List<ProductChannelProjection> findCompletoByCanal(String codCanal);

    Page<ProductChannelProjection> findCompletoByCanal(String codCanal, Pageable pageable);

    Page<ProductChannel> findByCodCanal(String codCanal, Pageable pageable);

    Page<ProductChannel> findByCodProduto(String codProduto, Pageable pageable);

    List<ChannelByProductProjection> findChannelsByProduct(String codProduto);

    Page<ChannelByProductProjection> findChannelsByProduct(String codProduto, Pageable pageable);

    ProductChannel save(ProductChannel entity);

    void deleteById(ProductChannelKey id);

    boolean existsById(ProductChannelKey id);
}

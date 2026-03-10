package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;

public interface ProductChannelRepository {

    Optional<ProductChannel> findById(ProductChannelKey id);

    List<ProductChannel> findAll();

    List<ProductChannel> findByCodCanal(String codCanal);

    List<ProductChannel> findByCodProduto(String codProduto);

    ProductChannel save(ProductChannel entity);

    void deleteById(ProductChannelKey id);

    boolean existsById(ProductChannelKey id);
}

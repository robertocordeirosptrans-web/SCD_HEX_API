package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;

public interface ProductChannelPersistencePort {

    Optional<ProductChannel> findById(ProductChannelKey id);

    Page<ProductChannel> findAll(Pageable pageable);

    List<ProductChannelProjection> findCompletoByCanal(String codCanal);

    Page<ProductChannelProjection> findCompletoByCanal(String codCanal, Pageable pageable);

    Page<ProductChannel> findByCodCanal(String codCanal, Pageable pageable);

    Page<ProductChannel> findByCodProduto(String codProduto, Pageable pageable);

    ProductChannel save(ProductChannel entity);

    void deleteById(ProductChannelKey id);

    boolean existsById(ProductChannelKey id);
}

package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ProductChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.ProductChannelJpaRepository;
import br.sptrans.scd.channel.application.port.out.ProductChannelRepository;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductChannelAdapterJpa implements ProductChannelRepository {

    private final ProductChannelMapper productChannelMapper;
    private final ProductChannelJpaRepository productChannelJpaRepository;

    @Override
    public Optional<ProductChannel> findById(ProductChannelKey id) {
        return productChannelJpaRepository.findById(productChannelMapper.toEntityKey(id))
                .map(productChannelMapper::toDomain);
    }

    @Override
    public List<ProductChannel> findAll() {
        return productChannelJpaRepository.findAll().stream()
                .map(productChannelMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProductChannel> findByCodCanal(String codCanal) {
        return productChannelJpaRepository.findAll().stream()
                .filter(e -> e.getId().getCodCanal().equals(codCanal))
                .map(productChannelMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProductChannel> findByCodProduto(String codProduto) {
        return productChannelJpaRepository.findAll().stream()
                .filter(e -> e.getId().getCodProduto().equals(codProduto))
                .map(productChannelMapper::toDomain)
                .toList();
    }

    @Override
    public ProductChannel save(ProductChannel domain) {
        var entity = productChannelMapper.toEntity(domain);
        var saved = productChannelJpaRepository.save(entity);
        return productChannelMapper.toDomain(saved);
    }

    @Override
    public void deleteById(ProductChannelKey id) {
        productChannelJpaRepository.deleteById(productChannelMapper.toEntityKey(id));
    }

    @Override
    public boolean existsById(ProductChannelKey id) {
        return productChannelJpaRepository.existsById(productChannelMapper.toEntityKey(id));
    }

    @Override
    public Optional<ProductChannel> findByIdOtimized(String codCanal, String codProduto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

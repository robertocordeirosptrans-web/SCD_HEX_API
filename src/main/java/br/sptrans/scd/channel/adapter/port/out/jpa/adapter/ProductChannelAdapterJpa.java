package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ProductChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;
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
    private final UserPersistencePort userRepository;

    @Override
    public Optional<ProductChannel> findById(ProductChannelKey id) {
        return productChannelJpaRepository.findById(productChannelMapper.toEntityKey(id))
                .map(entity -> productChannelMapper.toDomain(entity, userRepository));
    }

    @Override
    public List<ProductChannel> findAll() {
        return productChannelJpaRepository.findAll().stream()
                .map(entity -> productChannelMapper.toDomain(entity, userRepository))
                .toList();
    }

    @Override
    public List<ProductChannel> findByCodCanal(String codCanal) {
        return productChannelJpaRepository.findByIdCodCanal(codCanal).stream()
                .map(entity -> productChannelMapper.toDomain(entity, userRepository))
                .toList();
    }

    @Override
    public List<ProductChannel> findByCodProduto(String codProduto) {
        return productChannelJpaRepository.findByIdCodProduto(codProduto).stream()
                .map(entity -> productChannelMapper.toDomain(entity, userRepository))
                .toList();
    }

    @Override
    public ProductChannel save(ProductChannel domain) {
        var entity = productChannelMapper.toEntity(domain);
        var saved = productChannelJpaRepository.save(entity);
        return productChannelMapper.toDomain(saved, userRepository);
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

    @Override
    public List<ProductChannelProjection> findCompletoByCanal(String codCanal) {
        if (codCanal == null || codCanal.isEmpty()) {
            throw new IllegalArgumentException("codCanal não pode ser nulo ou vazio");
        }
        try {
            return productChannelJpaRepository.findCompletoByCanal(codCanal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("codCanal deve ser um número inteiro", e);
        }
    }

}

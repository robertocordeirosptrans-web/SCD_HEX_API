package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ProductChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.ProductChannelJpaRepository;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.channel.application.port.out.ProductChannelPersistencePort;

import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductChannelAdapterJpa implements ProductChannelPersistencePort {

    private final ProductChannelMapper productChannelMapper;
    private final ProductChannelJpaRepository productChannelJpaRepository;
    private final UserQueryPort userQueryPort;

    private User resolveUser(Long id) {
        if (id == null) return null;
        return userQueryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
    }

    @Override
    public Optional<ProductChannel> findById(ProductChannelKey id) {
        return productChannelJpaRepository.findById(productChannelMapper.toEntityKey(id))
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                });
    }

    @Override
    public List<ProductChannel> findAll() {
        return productChannelJpaRepository.findAll().stream()
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                })
                .toList();
    }

    @Override
    public List<ProductChannel> findByCodCanal(String codCanal) {
        return productChannelJpaRepository.findByIdCodCanal(codCanal).stream()
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                })
                .toList();
    }

    @Override
    public List<ProductChannel> findByCodProduto(String codProduto) {
        return productChannelJpaRepository.findByIdCodProduto(codProduto).stream()
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                })
                .toList();
    }

    @Override
    public ProductChannel save(ProductChannel domain) {
        var entity = productChannelMapper.toEntity(domain);
        var saved = productChannelJpaRepository.save(entity);
        return productChannelMapper.toDomain(saved, domain.getIdUsuarioCadastro(), domain.getIdUsuarioManutencao());
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

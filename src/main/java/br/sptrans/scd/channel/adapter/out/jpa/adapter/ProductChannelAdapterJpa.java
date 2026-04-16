package br.sptrans.scd.channel.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.ProductChannelMapper;
import br.sptrans.scd.channel.adapter.out.jpa.repository.ProductChannelJpaRepository;
import br.sptrans.scd.channel.application.port.out.ProductChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.query.ChannelByProductProjection;
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
    public Page<ProductChannel> findAll(Pageable pageable) {
        return productChannelJpaRepository.findAll(pageable)
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                });
    }

    @Override
    public Page<ProductChannel> findByCodCanal(String codCanal, Pageable pageable) {
        return productChannelJpaRepository.findByIdCodCanal(codCanal, pageable)
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                });
    }

    @Override
    public Page<ProductChannel> findByCodProduto(String codProduto, Pageable pageable) {
        return productChannelJpaRepository.findByIdCodProduto(codProduto, pageable)
                .map(entity -> {
                    User userCad = resolveUser(entity.getIdUsuarioCadastro());
                    User userMan = resolveUser(entity.getIdUsuarioManutencao());
                    return productChannelMapper.toDomain(entity, userCad, userMan);
                });
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

    @Override
    public Page<ProductChannelProjection> findCompletoByCanal(String codCanal, Pageable pageable) {
        if (codCanal == null || codCanal.isEmpty()) {
            throw new IllegalArgumentException("codCanal não pode ser nulo ou vazio");
        }
        try {
            return productChannelJpaRepository.findCompletoByCanalPageable(codCanal, pageable);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("codCanal deve ser um número inteiro", e);
        }
    }

    @Override
    public List<ChannelByProductProjection> findChannelsByProduct(String codProduto) {
        return productChannelJpaRepository.findChannelsByProduct(codProduto);
    }

    @Override
    public Page<ChannelByProductProjection> findChannelsByProduct(String codProduto, Pageable pageable) {
        List<ChannelByProductProjection> all = productChannelJpaRepository.findChannelsByProduct(codProduto);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<ChannelByProductProjection> slice = all.subList(start, end);
        return new PageImpl<>(slice, pageable, all.size());
    }
    

}

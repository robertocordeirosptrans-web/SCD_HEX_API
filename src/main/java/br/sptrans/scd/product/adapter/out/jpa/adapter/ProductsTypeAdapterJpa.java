package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductsTypeMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ProductsTypeJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.ProductsTypePort;
import br.sptrans.scd.product.domain.ProductType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductsTypeAdapterJpa implements ProductsTypePort {

    private final ProductsTypeJpaRepository repository;
    private final ProductsTypeMapper productsTypeMapper;

    @Override
    public Optional<ProductType> findById(String codTipoProduto) {
        return repository.findById(codTipoProduto)
                .map(productsTypeMapper::toDomain);
    }

    @Override
    public boolean existsById(String codTipoProduto) {
        return repository.existsById(codTipoProduto);
    }

    @Override
    public List<ProductType> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(productsTypeMapper::toDomain)
                    .filter(t -> codStatus.equals(t.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(productsTypeMapper::toDomain)
                .toList();
    }

    @Override
    public Page<ProductType> findAll(String codStatus, Pageable pageable) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findByCodStatus(codStatus, pageable)
                    .map(productsTypeMapper::toDomain);
        }
        return repository.findAll(pageable)
                .map(productsTypeMapper::toDomain);
    }

    @Override
    public ProductType save(ProductType type) {
        var entity = productsTypeMapper.toEntity(type);
        var saved = repository.save(entity);
        return productsTypeMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codTipoProduto, String codStatus, Long idUsuario) {
        repository.findById(codTipoProduto).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            if (idUsuario != null) {
                UserEntityJpa userRef = new UserEntityJpa();
                userRef.setIdUsuario(idUsuario);
                entity.setUsuarioManutencao(userRef);
            }
            repository.save(entity);
        });
    }

    @Override
    public void deleteById(String codTipoProduto) {
        repository.deleteById(codTipoProduto);
    }

    @Override
    public Long findMaxNumericCode() {
        return repository.findMaxNumericCode();
    }
}



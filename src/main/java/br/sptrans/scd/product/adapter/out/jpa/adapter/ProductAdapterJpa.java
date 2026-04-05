package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ProductJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.ProductRepository;
import br.sptrans.scd.product.domain.Product;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductAdapterJpa implements ProductRepository {

    private final ProductJpaRepository repository;

    @Override
    public Optional<Product> findById(String codProduto) {
        return repository.findById(codProduto)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(ProductMapper::toDomain)
                    .filter(p -> codStatus.equals(p.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public Product save(Product product) {
        var entity = ProductMapper.toEntity(product);
        var saved = repository.save(entity);
        return ProductMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codProduto, String codStatus, Long idUsuario) {
        repository.findById(codProduto).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            if (idUsuario != null) {
                entity.setIdUsuarioManutencao(idUsuario);
            }
            repository.save(entity);
        });
    }

    @Override
    public boolean existsByProduct(String codProduto) {
        return repository.existsById(codProduto);
    }
}
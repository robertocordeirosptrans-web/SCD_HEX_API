package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductVersionMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ProductVersionJpaRepository;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductVersionId;
import br.sptrans.scd.product.application.port.out.repository.ProductVersionPort;
import br.sptrans.scd.product.domain.ProductVersion;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductVersionAdapterJpa implements ProductVersionPort {

    private final ProductVersionJpaRepository repository;
    private final ProductVersionMapper productVersionMapper;

    @Override
    public Optional<ProductVersion> findById(String codVersao) {
        // Busca por codVersao como String - mantém compatibilidade
        // Nota: Se houver múltiplas versões com o mesmo código para diferentes produtos,
        // este método retornará uma delas arbitrariamente. Idealmente, use findByVersionAndProduct.
        return repository.findAll().stream()
                .filter(e -> codVersao.equals(e.getCodVersao()))
                .findFirst()
                .map(productVersionMapper::toDomain);
    }

    public Optional<ProductVersion> findByVersionAndProduct(String codVersao, String codProduto) {
        ProductVersionId id = new ProductVersionId(codVersao, codProduto);
        return repository.findById(id)
                .map(productVersionMapper::toDomain);
    }

    public boolean existsById(String codVersao) {
        return repository.findAll().stream()
                .anyMatch(e -> codVersao.equals(e.getCodVersao()));
    }

    public List<ProductVersion> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(productVersionMapper::toDomain)
                    .filter(v -> codStatus.equals(v.getFlgBloqFabricacao()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(productVersionMapper::toDomain)
                .toList();
    }

    @Override
    public ProductVersion save(ProductVersion version) {
        var entity = productVersionMapper.toEntity(version);
        // Cria a chave composta se não existir
        if (entity.getId() == null) {
            entity.setId(new ProductVersionId(version.getCodVersao(), version.getCodProduto()));
        }
        var saved = repository.save(entity);
        return productVersionMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codVersao, String codStatus, Long idUsuario) {
        repository.findAll().stream()
                .filter(e -> codVersao.equals(e.getCodVersao()))
                .findFirst()
                .ifPresent(entity -> {
                    entity.setFlgBloqFabricacao(codStatus);
                    if (idUsuario != null) {
                        UserEntityJpa userRef = new UserEntityJpa();
                        userRef.setIdUsuario(idUsuario);
                        entity.setUsuarioManutencao(userRef);
                    }
                    repository.save(entity);
                });
    }

    public void deleteById(String codVersao, String codProduto) {
        ProductVersionId id = new ProductVersionId(codVersao, codProduto);
        repository.deleteById(id);
    }

    @Override
    public Optional<ProductVersion> findByProduct(String codProduto) {
        return repository.findAll().stream()
                .filter(e -> codProduto.equals(e.getCodProduto()))
                .findFirst()
                .map(productVersionMapper::toDomain);
    }

    @Override
    public boolean existsByProduct(String codProduto) {
        return repository.findAll().stream()
                .anyMatch(e -> codProduto.equals(e.getCodProduto()));
    }

    @Override
    public Optional<ProductVersion> findLastVersion(String codProduto) {
        return repository.findAll().stream()
                .filter(e -> codProduto.equals(e.getCodProduto()))
                .reduce((first, second) -> second)
                .map(productVersionMapper::toDomain);
    }

    @Override
    public List<ProductVersion> findAllByProduct(String codProduto) {
        return repository.findAll().stream()
                .filter(e -> codProduto.equals(e.getCodProduto()))
                .map(productVersionMapper::toDomain)
                .toList();
    }

    @Override
    public Page<ProductVersion> findAllByProduct(String codProduto, Pageable pageable) {
        List<ProductVersion> all = findAllByProduct(codProduto);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<ProductVersion> slice = all.subList(start, end);
        return new PageImpl<>(slice, pageable, all.size());
    }
}

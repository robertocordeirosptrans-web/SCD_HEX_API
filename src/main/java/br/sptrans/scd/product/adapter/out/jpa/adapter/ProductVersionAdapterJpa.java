package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductVersionMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ProductVersionJpaRepository;
import br.sptrans.scd.product.application.port.out.ProductVersionRepository;
import br.sptrans.scd.product.domain.ProductVersion;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class ProductVersionAdapterJpa implements ProductVersionRepository {

    private final ProductVersionJpaRepository repository;

    @Override
    public Optional<ProductVersion> findById(String codVersao) {
        return repository.findById(codVersao)
                .map(ProductVersionMapper::toDomain);
    }

    
    public boolean existsById(String codVersao) {
        return repository.existsById(codVersao);
    }

   
    public List<ProductVersion> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(ProductVersionMapper::toDomain)
                    .filter(v -> codStatus.equals(v.getFlgBloqFabricacao())) // Ajuste conforme campo correto
                    .toList();
        }
        return repository.findAll().stream()
                .map(ProductVersionMapper::toDomain)
                .toList();
    }

    @Override
    public ProductVersion save(ProductVersion version) {
        var entity = ProductVersionMapper.toEntity(version);
        var saved = repository.save(entity);
        return ProductVersionMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codVersao, String codStatus, Long idUsuario) {
        repository.findById(codVersao).ifPresent(entity -> {
            entity.setFlgBloqFabricacao(codStatus); // Ajuste conforme campo correto
            // Se existir campo de usuário, implemente aqui
            repository.save(entity);
        });
    }


    public void deleteById(String codVersao) {
        repository.deleteById(codVersao);
    }

    // @Override
    public Optional<ProductVersion> findByProduct(String codProduto) {
        // Exemplo: buscar a primeira versão pelo código do produto
        return repository.findAll().stream()
                .filter(e -> codProduto.equals(e.getCodProduto()))
                .findFirst()
                .map(ProductVersionMapper::toDomain);
    }

    @Override
    public boolean existsByProduct(String codProduto) {
        return repository.findAll().stream()
                .anyMatch(e -> codProduto.equals(e.getCodProduto()));
    }

    @Override
    public Optional<ProductVersion> findLastVersion(String codProduto) {
        // Exemplo: buscar a última versão pelo código do produto
        return repository.findAll().stream()
                .filter(e -> codProduto.equals(e.getCodProduto()))
                .reduce((first, second) -> second)
                .map(ProductVersionMapper::toDomain);
    }
}

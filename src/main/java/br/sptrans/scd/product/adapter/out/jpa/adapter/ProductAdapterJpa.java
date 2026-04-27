
package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.creditrequest.application.port.out.projection.CardsTypeProjection;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.CardTypeRepository;
import br.sptrans.scd.product.adapter.out.jpa.repository.ProductJpaRepository;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.application.port.out.repository.CardTypePort;
import br.sptrans.scd.product.application.port.out.repository.ProductPort;
import br.sptrans.scd.product.domain.CardType;
import br.sptrans.scd.product.domain.Product;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductAdapterJpa implements ProductPort, CardTypePort {

    private final ProductJpaRepository repository;
    private final ProductMapper productMapper;
    private final CardTypeRepository cardTypeRepository;

    @Override
    public Optional<Product> findById(String codProduto) {
        return repository.findById(codProduto)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(productMapper::toDomain)
                    .filter(p -> codStatus.equals(p.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Product> findAll(Specification<ProductEntityJpa> spec, Pageable pageable) {
        return repository.findAll(spec, pageable)
                .map(productMapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        var entity = productMapper.toEntity(product);
        var saved = repository.save(entity);
        return productMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codProduto, String codStatus, Long idUsuario) {
        repository.findById(codProduto).ifPresent(entity -> {
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
    public boolean existsByProduct(String codProduto) {
        return repository.existsById(codProduto);
    }

    @Override
    public List<CardType> findAllViaDblink() {
        List<CardsTypeProjection> projections = cardTypeRepository.findAllViaDblink();
        return projections.stream()
                .map(p -> new CardType(
                        String.valueOf(p.getCodTipoCartao()),
                        p.getDesTipoCartao()
                ))
                .collect(Collectors.toList());
    }
}
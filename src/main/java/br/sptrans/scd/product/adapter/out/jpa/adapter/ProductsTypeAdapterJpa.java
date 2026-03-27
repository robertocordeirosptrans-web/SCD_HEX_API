package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.adapter.out.jpa.entity.ProductTypesEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ProductsTypeMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ProductsTypeJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.ProductsTypeRepository;
import br.sptrans.scd.product.domain.ProductType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductsTypeAdapterJpa implements ProductsTypeRepository {

    private final ProductsTypeJpaRepository repository;
    private final UserRepository userRepository;

    @Override
    public Optional<ProductType> findById(String codTipoProduto) {
        return repository.findById(codTipoProduto)
                .map(entity -> ProductsTypeMapper.toDomain(entity, userRepository));
    }

    @Override
    public boolean existsById(String codTipoProduto) {
        return repository.existsById(codTipoProduto);
    }

    @Override
    public List<ProductType> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(entity -> ProductsTypeMapper.toDomain(entity, userRepository))
                    .filter(t -> codStatus.equals(t.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(entity -> ProductsTypeMapper.toDomain(entity, userRepository))
                .toList();
    }

    @Override
    public ProductType save(ProductType type) {
        var entity = new ProductTypesEntityJpa();
        entity.setCodTipoProduto(type.getCodTipoProduto());
        entity.setDesTipoProduto(type.getDesTipoProduto());
        entity.setCodStatus(type.getCodStatus());
        entity.setDtCadastro(type.getDtCadastro());
        entity.setDtManutencao(type.getDtManutencao());
        if (type.getIdUsuarioCadastro() != null) {
            entity.setIdUsuarioCadastro(type.getIdUsuarioCadastro().getIdUsuario());
        }
        if (type.getIdUsuarioManutencao() != null) {
            entity.setIdUsuarioManutencao(type.getIdUsuarioManutencao().getIdUsuario());
        }
        var saved = repository.save(entity);
        return ProductsTypeMapper.toDomain(saved, userRepository);
    }

    @Override
    public void updateStatus(String codTipoProduto, String codStatus, Long idUsuario) {
        repository.findById(codTipoProduto).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            // Supondo que existe setIdUsuarioManutencao
            // Se existir campo de usuário, implemente aqui
            repository.save(entity);
        });
    }

    @Override
    public void deleteById(String codTipoProduto) {
        repository.deleteById(codTipoProduto);
    }
}



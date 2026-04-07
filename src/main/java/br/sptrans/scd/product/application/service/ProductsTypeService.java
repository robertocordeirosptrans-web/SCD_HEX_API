package br.sptrans.scd.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.out.repository.ProductsTypePort;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsTypeService extends AbstractCatalogueService<ProductType, String> {
    private final ProductsTypePort productsTypeRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public ProductType create(ProductType entity, Long idUsuario) {
        if (productsTypeRepository.existsById(entity.getId())) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_CODE_ALREADY_EXISTS);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return productsTypeRepository.save(entity);
    }

    @Override
    public ProductType update(String id, ProductType entity, Long idUsuario) {
        ProductType existing = productsTypeRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));
        User usuario = userResolverHelper.resolve(idUsuario);
        existing.setDesTipoProduto(entity.getDesTipoProduto() != null ? entity.getDesTipoProduto() : existing.getDesTipoProduto());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return productsTypeRepository.save(existing);
    }

    @Override
    public Optional<ProductType> findById(String id) {
        return productsTypeRepository.findById(id);
    }

    @Override
    public List<ProductType> findAll(Long idUsuario) {
        userResolverHelper.resolve(idUsuario);
        return productsTypeRepository.findAll(ProductDomainStatus.ACTIVE.getCode(), Pageable.unpaged()).getContent();
    }

    @Override
    public void activate(String id, Long idUsuario) {
        ProductType productType = productsTypeRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));
        if (productType.isActive()) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_ALREADY_ACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        productType.activate(usuario);
        productsTypeRepository.save(productType);
    }

    @Override
    public void inactivate(String id, Long idUsuario) {
        ProductType productType = productsTypeRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));
        if (productType.isInactive()) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_ALREADY_INACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        productType.deactivate(usuario);
        productsTypeRepository.save(productType);
    }
}

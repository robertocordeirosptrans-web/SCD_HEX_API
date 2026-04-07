package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase;
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
public class ProductsTypeService implements ProductsTypeManagementUseCase {

    private final ProductsTypePort productsTypeRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public ProductType createProductsType(CreateProductsTypeCommand command) {
        if (productsTypeRepository.existsById(command.codTipoProduto())) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_CODE_ALREADY_EXISTS);
        }

        User usuario = userResolverHelper.resolve(command.idUsuario());

        ProductType productType = new ProductType(
                command.codTipoProduto(),
                command.desTipoProduto(),
                ProductDomainStatus.INACTIVE.getCode(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                usuario,
                null
        );

        return productsTypeRepository.save(productType);
    }

    @Override
    public ProductType updateProductsType(String codTipoProduto, UpdateProductsTypeCommand command) {
        ProductType existing = productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));

        User usuario = userResolverHelper.resolve(command.idUsuario());

        existing.update(command.desTipoProduto(), usuario);
        return productsTypeRepository.save(existing);
    }

    @Override
    public ProductType findByProductsType(String codTipoProduto) {
        return productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));
    }

    @Override
    public Page<ProductType> findAllProductsTypes(String codStatus, Pageable pageable) {
        return productsTypeRepository.findAll(codStatus, pageable);
    }

    @Override
    public void activateProductsType(String codTipoProduto, Long idUsuario) {
        ProductType productType = productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));

        if (productType.isActive()) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_ALREADY_ACTIVE);
        }

        User usuario = userResolverHelper.resolve(idUsuario);
        productType.activate(usuario);
        productsTypeRepository.save(productType);
    }

    @Override
    public void inactivateProductsType(String codTipoProduto, Long idUsuario) {
        ProductType productType = productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));

        if (productType.isInactive()) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_ALREADY_INACTIVE);
        }

        User usuario = userResolverHelper.resolve(idUsuario);
        productType.deactivate(usuario);
        productsTypeRepository.save(productType);
    }

    @Override
    public void deleteProductsType(String codTipoProduto) {
        if (!productsTypeRepository.existsById(codTipoProduto)) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND);
        }
        productsTypeRepository.deleteById(codTipoProduto);
    }
}

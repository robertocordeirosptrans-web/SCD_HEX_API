package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase;
import br.sptrans.scd.product.application.port.out.ProductsTypeRepository;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsTypeService implements ProductsTypeManagementUseCase {

    private final ProductsTypeRepository productsTypeRepository;
    private final UserRepository userRepository;

    @Override
    public ProductType createProductsType(CreateProductsTypeCommand command) {
        if (productsTypeRepository.existsById(command.codTipoProduto())) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_CODE_ALREADY_EXISTS);
        }

        User usuario = resolveUser(command.idUsuario());

        ProductType productType = new ProductType(
                command.codTipoProduto(),
                command.desTipoProduto(),
                DomainStatus.INACTIVE.getCode(),
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

        User usuario = resolveUser(command.idUsuario());

        ProductType updated = new ProductType(
                existing.getCodTipoProduto(),
                command.desTipoProduto(),
                existing.getCodStatus(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                existing.getIdUsuarioCadastro(),
                usuario
        );

        return productsTypeRepository.save(updated);
    }

    @Override
    public ProductType findByProductsType(String codTipoProduto) {
        return productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));
    }

    @Override
    public List<ProductType> findAllProductsTypes(String codStatus) {
        return productsTypeRepository.findAll(codStatus);
    }

    @Override
    public void activateProductsType(String codTipoProduto, Long idUsuario) {
        ProductType productType = productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));

        if (productType.isActive()) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_ALREADY_ACTIVE);
        }

        productsTypeRepository.updateStatus(codTipoProduto, DomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateProductsType(String codTipoProduto, Long idUsuario) {
        ProductType productType = productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));

        if (productType.isInactive()) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_ALREADY_INACTIVE);
        }

        productsTypeRepository.updateStatus(codTipoProduto, DomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteProductsType(String codTipoProduto) {
        if (!productsTypeRepository.existsById(codTipoProduto)) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND);
        }
        productsTypeRepository.deleteById(codTipoProduto);
    }

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}

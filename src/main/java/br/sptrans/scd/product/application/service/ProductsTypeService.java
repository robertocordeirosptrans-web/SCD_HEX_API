package br.sptrans.scd.product.application.service;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.CreateProductsTypeCommand;
import br.sptrans.scd.product.application.port.in.ProductsTypeManagementUseCase.UpdateProductsTypeCommand;
import br.sptrans.scd.product.application.port.out.repository.ProductsTypePort;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsTypeService extends AbstractCatalogueService<ProductType, String, ProductsTypeManagementUseCase.CreateProductsTypeCommand, ProductsTypeManagementUseCase.UpdateProductsTypeCommand> implements ProductsTypeManagementUseCase {
    private final ProductsTypePort productsTypeRepository;
    private final UserResolverHelper userResolverHelper;

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_PRODUCTSTYPE");

    private ProductType getByIdOrThrow(String codTipoProduto) {
        return productsTypeRepository.findById(codTipoProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCTS_TYPE_NOT_FOUND));
    }

    @Override
    public ProductType create(CreateProductsTypeCommand command) {
        if (productsTypeRepository.existsById(command.codTipoProduto())) {
            throw new ProductException(ProductErrorType.PRODUCTS_TYPE_CODE_ALREADY_EXISTS);
        }
        var usuario = userResolverHelper.resolve(command.idUsuario());
        ProductType entity = new ProductType();
        entity.setId(command.codTipoProduto());
        entity.setDesTipoProduto(command.desTipoProduto());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} criou TipoProduto {} - {}", usuario.getCodLogin(), entity.getId(), entity.getDesTipoProduto());
        return productsTypeRepository.save(entity);
    }

    @Override
    public ProductType update(String codTipoProduto, UpdateProductsTypeCommand command) {
        ProductType existing = getByIdOrThrow(codTipoProduto);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesTipoProduto(command.desTipoProduto());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} atualizou TipoProduto {} - {}", usuario.getCodLogin(), existing.getId(), existing.getDesTipoProduto());
        return productsTypeRepository.save(existing);
    }

    @Override
    public void activate(String codTipoProduto, Long idUsuario) {
        ProductType productType = getByIdOrThrow(codTipoProduto);
        var usuario = userResolverHelper.resolve(idUsuario);
        productType.activate(usuario);
        productsTypeRepository.save(productType);

        auditLogger.info("[AUDIT] Usuário {} ativou TipoProduto {} - {}", usuario.getCodLogin(), productType.getId(), productType.getDesTipoProduto());
    }

    @Override
    public void inactivate(String codTipoProduto, Long idUsuario) {
        ProductType productType = getByIdOrThrow(codTipoProduto);
        var usuario = userResolverHelper.resolve(idUsuario);
        productType.deactivate(usuario);
        productsTypeRepository.save(productType);

        auditLogger.info("[AUDIT] Usuário {} inativou TipoProduto {} - {}", usuario.getCodLogin(), productType.getId(), productType.getDesTipoProduto());
    }

    @Override
    public void delete(String codTipoProduto) {
        productsTypeRepository.deleteById(codTipoProduto);
        auditLogger.info("[AUDIT] TipoProduto deletado: {}", codTipoProduto);
    }

    @Override
    public Optional<ProductType> findById(String codTipoProduto) {
        return productsTypeRepository.findById(codTipoProduto);
    }

    @Override
    public Page<ProductType> findAll(String codStatus, Pageable pageable) {
        return productsTypeRepository.findAll(codStatus, pageable);
    }
}

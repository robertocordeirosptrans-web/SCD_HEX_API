package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.ProductUseCase;
import br.sptrans.scd.product.application.port.out.repository.ProductRepository;
import br.sptrans.scd.product.application.port.out.repository.ProductVersionRepository;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ProductVersion;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.enums.ProductStatus;
import br.sptrans.scd.product.domain.enums.ProductVersionStatus;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final UserRepository userRepository;

    // =========================================================================
    // Gestão de Produto
    // =========================================================================

    @Override
    public void createProduct(CreateProductCommand cmd) {
        if (productRepository.existsByProduct(cmd.codProduto())) {
            throw new ProductException(ProductErrorType.CODE_ALREADY_EXISTS);
        }

        User usuarioCadastro = resolveUser(cmd.idUsuario());

        Product product = new Product(
            cmd.codProduto(),
            cmd.desProduto(),
            cmd.desEmissorResponsavel(),
            ProductStatus.INACTIVE.getCode(),
            cmd.desUtilizacao(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            cmd.flgBloqFabricacao(),
            cmd.flgBloqVenda(),
            cmd.flgBloqDistribuicao(),
            cmd.flgBloqTroca(),
            cmd.flgBloqAquisicao(),
            cmd.flgBloqPedido(),
            cmd.flgBloqDevolucao(),
            cmd.flgInicializado(),
            cmd.flgComercializado(),
            cmd.flgRestManual(),
            cmd.codEntidade(),
            cmd.codTipoCartao(),
            cmd.codClassificacaoPessoa(),
            cmd.codTipoProduto(),
            cmd.codTecnologia(),
            cmd.codModalidade(),
            cmd.codFamilia(),
            cmd.codEspecie(),
            usuarioCadastro.getIdUsuario(),
            null
        );

        productRepository.save(product);

        // Versão 1 criada automaticamente
        ProductVersion initialVersion = new ProductVersion(
                "1",
                cmd.codProduto(),
                null, null, null, null, null, null, null,
                null, null, null, null,
                null, null, null, null, null, null, null,
                LocalDateTime.now(),
                null,
                ProductVersionStatus.INACTIVE.getCode(),
                null,
                usuarioCadastro,
                null
        );

        productVersionRepository.save(initialVersion);
    }

    @Override
    public void activateProduct(String productCode, Long idUsuario) {
        Product product = productRepository.findById(productCode)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        if (product.isActive()) {
            throw new ProductException(ProductErrorType.PRODUCT_ALREADY_ACTIVE);
        }

        if (!productVersionRepository.existsByProduct(productCode)) {
            throw new ProductException(ProductErrorType.PRODUCT_WITHOUT_VERSION);
        }

        productRepository.updateStatus(productCode, ProductStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateProduct(String productCode, Long idUsuario) {
        Product product = productRepository.findById(productCode)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        if (product.isInactive()) {
            throw new ProductException(ProductErrorType.PRODUCT_ALREADY_INACTIVE);
        }

        productRepository.updateStatus(productCode, ProductStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void updateProduct(String productCode, UpdateProductCommand cmd) {
        Product existing = productRepository.findById(productCode)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        User usuarioManutencao = resolveUser(cmd.idUsuario());

        Product updated = new Product(
                existing.getCodProduto(),
                cmd.desProduto(),
                cmd.desEmissorResponsavel(),
                existing.getCodStatus(),
                cmd.desUtilizacao(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                cmd.flgBloqFabricacao(),
                cmd.flgBloqVenda(),
                cmd.flgBloqDistribuicao(),
                cmd.flgBloqTroca(),
                cmd.flgBloqAquisicao(),
                cmd.flgBloqPedido(),
                cmd.flgBloqDevolucao(),
                cmd.flgInicializado(),
                cmd.flgComercializado(),
                cmd.flgRestManual(),
                cmd.codEntidade(),
                cmd.codTipoCartao(),
                cmd.codClassificacaoPessoa(),
                cmd.codTipoProduto(),
                cmd.codTecnologia(),
                cmd.codModalidade(),
                cmd.codFamilia(),
                cmd.codEspecie(),
                existing.getIdUsuarioCadastro(),
                usuarioManutencao.getIdUsuario()
        );

        productRepository.save(updated);
    }

    @Override
    public Product findByProduct(String codProduto) {
        return productRepository.findById(codProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<Product> findAllProducts(String codStatus) {
        return productRepository.findAll(codStatus);
    }

    // =========================================================================
    // Gestão de Versões
    // =========================================================================

    @Override
    public ProductVersion createNewVersion(String codProduto, CreateVersionCommand cmd) {
        if (!productRepository.existsByProduct(codProduto)) {
            throw new ProductException(ProductErrorType.PRODUCT_NOT_FOUND);
        }

        String newVersionCode = generateNextVersionCode(codProduto);
        User usuarioCadastro = resolveUser(cmd.idUsuario());

        ProductVersion version = new ProductVersion(
                newVersionCode,
                codProduto,
                cmd.dtValidade(),
                cmd.dtVidaInicio(),
                cmd.dtVidaFim(),
                cmd.dtLiberacao(),
                cmd.dtLancamento(),
                cmd.dtVendaInicio(),
                cmd.dtVendaFim(),
                cmd.dtUsoIni(),
                cmd.dtUsoFim(),
                cmd.dtTrocaIni(),
                cmd.dtTrocaFim(),
                cmd.flgBloqFabricacao(),
                cmd.flgBloqVenda(),
                cmd.flgBloqDistribuicao(),
                cmd.flgBloqTroca(),
                cmd.flgBloqAquisicao(),
                cmd.flgBloqPedido(),
                cmd.flgBloqDevolucao(),
                LocalDateTime.now(),
                null,
                ProductVersionStatus.INACTIVE.getCode(),
                cmd.desProdutoVersoes(),
                usuarioCadastro,
                null
        );

        return productVersionRepository.save(version);
    }

    @Override
    public ProductVersion findByVersion(String codVersao) {
        return productVersionRepository.findById(codVersao)
                .orElseThrow(() -> new ProductException(ProductErrorType.VERSION_NOT_FOUND));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }

    private String generateNextVersionCode(String codProduto) {
        return productVersionRepository.findLastVersion(codProduto)
                .map(last -> {
                    int num = Integer.parseInt(last.getCodVersao());
                    return String.valueOf(num + 1);
                })
                .orElse("1");
    }
}


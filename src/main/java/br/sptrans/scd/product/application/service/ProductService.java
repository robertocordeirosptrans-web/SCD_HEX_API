package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.ProductUseCase;
import br.sptrans.scd.product.application.port.out.repository.ProductPort;
import br.sptrans.scd.product.application.port.out.repository.ProductVersionPort;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ProductVersion;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.enums.ProductStatus;
import br.sptrans.scd.product.domain.enums.ProductVersionStatus;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductPort productRepository;
    private final ProductVersionPort productVersionRepository;
    private final UserResolverHelper userResolverHelper;

    // =========================================================================
    // Gestão de Produto
    // =========================================================================

    @Override
    public void createProduct(CreateProductCommand cmd) {
        if (productRepository.existsByProduct(cmd.codProduto())) {
            throw new ProductException(ProductErrorType.CODE_ALREADY_EXISTS);
        }

        User usuarioCadastro = userResolverHelper.resolve(cmd.idUsuario());

        Product product = Product.builder()
            .codProduto(cmd.codProduto())
            .desProduto(cmd.desProduto())
            .desEmissorResponsavel(cmd.desEmissorResponsavel())
            .codStatus(ProductStatus.ACTIVE.getCode())
            .desUtilizacao(cmd.desUtilizacao())
            .dtCadastro(LocalDateTime.now())
            .dtManutencao(LocalDateTime.now())
            .flgBloqFabricacao(cmd.flgBloqFabricacao())
            .flgBloqVenda(cmd.flgBloqVenda())
            .flgBloqDistribuicao(cmd.flgBloqDistribuicao())
            .flgBloqTroca(cmd.flgBloqTroca())
            .flgBloqAquisicao(cmd.flgBloqAquisicao())
            .flgBloqPedido(cmd.flgBloqPedido())
            .flgBloqDevolucao(cmd.flgBloqDevolucao())
            .flgInicializado(cmd.flgInicializado())
            .flgComercializado(cmd.flgComercializado())
            .flgRestManual(cmd.flgRestManual())
            .codEntidade(cmd.codEntidade())
            .codTipoCartao(cmd.codTipoCartao())
            .codClassificacaoPessoa(cmd.codClassificacaoPessoa())
            .codTipoProduto(cmd.codTipoProduto())
            .codTecnologia(cmd.codTecnologia())
            .codModalidade(cmd.codModalidade())
            .codFamilia(cmd.codFamilia())
            .codEspecie(cmd.codEspecie())
            .idUsuarioCadastro(usuarioCadastro.getIdUsuario())
            .idUsuarioManutencao(usuarioCadastro.getIdUsuario())
            .build();

        productRepository.save(product);

        // Versão 1 criada automaticamente
        ProductVersion initialVersion = ProductVersion.builder()
            .codVersao("1")
            .codProduto(cmd.codProduto())
            .dtValidade(null)
            .dtVidaInicio(null)
            .dtVidaFim(null)
            .dtLiberacao(null)
            .dtLancamento(null)
            .dtVendaInicio(null)
            .dtVendaFim(null)
            .dtUsoInicio(null)
            .dtUsoFim(null)
            .dtTrocaInicio(null)
            .dtTrocaFim(null)
            .flgBloqFabricacao("N")
            .flgBloqVenda("N")
            .flgBloqDistribuicao("N")
            .flgBloqTroca("N")
            .flgBloqAquisicao("N")
            .flgBloqPedido("N")
            .flgBloqDevolucao("N")
            .dtCadastro(LocalDateTime.now())
            .dtManutencao(LocalDateTime.now())
            .codStatus(ProductVersionStatus.ACTIVE.getCode())
            .desProdutoVersoes("Versão Inicial")
            .idUsuarioCadastro(usuarioCadastro)
            .idUsuarioManutencao(usuarioCadastro)
            .build();

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

        product.activate(idUsuario);
        productRepository.save(product);
    }

    @Override
    public void inactivateProduct(String productCode, Long idUsuario) {
        Product product = productRepository.findById(productCode)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        if (product.isInactive()) {
            throw new ProductException(ProductErrorType.PRODUCT_ALREADY_INACTIVE);
        }

        product.deactivate(idUsuario);
        productRepository.save(product);
    }

    @Override
    public void updateProduct(String productCode, UpdateProductCommand cmd) {
        Product existing = productRepository.findById(productCode)
            .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        User usuarioManutencao = userResolverHelper.resolve(cmd.idUsuario());

        Product updated = Product.builder()
            .codProduto(existing.getCodProduto())
            .desProduto(cmd.desProduto())
            .desEmissorResponsavel(cmd.desEmissorResponsavel())
            .codStatus(existing.getCodStatus())
            .desUtilizacao(cmd.desUtilizacao())
            .dtCadastro(existing.getDtCadastro())
            .dtManutencao(LocalDateTime.now())
            .flgBloqFabricacao(cmd.flgBloqFabricacao())
            .flgBloqVenda(cmd.flgBloqVenda())
            .flgBloqDistribuicao(cmd.flgBloqDistribuicao())
            .flgBloqTroca(cmd.flgBloqTroca())
            .flgBloqAquisicao(cmd.flgBloqAquisicao())
            .flgBloqPedido(cmd.flgBloqPedido())
            .flgBloqDevolucao(cmd.flgBloqDevolucao())
            .flgInicializado(cmd.flgInicializado())
            .flgComercializado(cmd.flgComercializado())
            .flgRestManual(cmd.flgRestManual())
            .codEntidade(cmd.codEntidade())
            .codTipoCartao(cmd.codTipoCartao())
            .codClassificacaoPessoa(cmd.codClassificacaoPessoa())
            .codTipoProduto(cmd.codTipoProduto())
            .codTecnologia(cmd.codTecnologia())
            .codModalidade(cmd.codModalidade())
            .codFamilia(cmd.codFamilia())
            .codEspecie(cmd.codEspecie())
            .idUsuarioCadastro(existing.getIdUsuarioCadastro())
            .idUsuarioManutencao(usuarioManutencao.getIdUsuario())
            .build();

        productRepository.save(updated);
    }

    @Override
    public Product findByProduct(String codProduto) {
        return productRepository.findById(codProduto)
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));
    }

    @Override
    public Page<Product> findAllProducts(String codStatus, Pageable pageable) {
        return productRepository.findAll(codStatus, pageable);
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
        User usuarioCadastro = userResolverHelper.resolve(cmd.idUsuario());

        ProductVersion version = ProductVersion.builder()
            .codVersao(newVersionCode)
            .codProduto(codProduto)
            .dtValidade(cmd.dtValidade())
            .dtVidaInicio(cmd.dtVidaInicio())
            .dtVidaFim(cmd.dtVidaFim())
            .dtLiberacao(cmd.dtLiberacao())
            .dtLancamento(cmd.dtLancamento())
            .dtVendaInicio(cmd.dtVendaInicio())
            .dtVendaFim(cmd.dtVendaFim())
            .dtUsoInicio(cmd.dtUsoIni())
            .dtUsoFim(cmd.dtUsoFim())
            .dtTrocaInicio(cmd.dtTrocaIni())
            .dtTrocaFim(cmd.dtTrocaFim())
            .flgBloqFabricacao(cmd.flgBloqFabricacao())
            .flgBloqVenda(cmd.flgBloqVenda())
            .flgBloqDistribuicao(cmd.flgBloqDistribuicao())
            .flgBloqTroca(cmd.flgBloqTroca())
            .flgBloqAquisicao(cmd.flgBloqAquisicao())
            .flgBloqPedido(cmd.flgBloqPedido())
            .flgBloqDevolucao(cmd.flgBloqDevolucao())
            .dtCadastro(LocalDateTime.now())
            .dtManutencao(null)
            .codStatus(ProductVersionStatus.INACTIVE.getCode())
            .desProdutoVersoes(cmd.desProdutoVersoes())
            .idUsuarioCadastro(usuarioCadastro)
            .idUsuarioManutencao(null)
            .build();

        return productVersionRepository.save(version);
    }

    @Override
    public List<ProductVersion> findAllVersion(String codProduto) {
        if (!productRepository.existsByProduct(codProduto)) {
            throw new ProductException(ProductErrorType.PRODUCT_NOT_FOUND);
        }
        return productVersionRepository.findAllByProduct(codProduto);
    }

    @Override
    public Page<ProductVersion> findAllVersion(String codProduto, Pageable pageable) {
        if (!productRepository.existsByProduct(codProduto)) {
            throw new ProductException(ProductErrorType.PRODUCT_NOT_FOUND);
        }
        return productVersionRepository.findAllByProduct(codProduto, pageable);
    }

    // =========================================================================
    // Operações de status de Versão de Produto
    // =========================================================================

    public void activateProductVersion(String codVersao, Long idUsuario) {
        ProductVersion version = productVersionRepository.findById(codVersao)
                .orElseThrow(() -> new ProductException(ProductErrorType.VERSION_NOT_FOUND));
        if (version.isActive()) {
            throw new ProductException(ProductErrorType.PRODUCT_ALREADY_ACTIVE);
        }
        User usuarioManutencao = userResolverHelper.resolve(idUsuario);
        version.activate(usuarioManutencao);
        productVersionRepository.save(version);
    }

    public void inactivateProductVersion(String codVersao, Long idUsuario) {
        ProductVersion version = productVersionRepository.findById(codVersao)
                .orElseThrow(() -> new ProductException(ProductErrorType.VERSION_NOT_FOUND));
        if (version.isInactive()) {
            throw new ProductException(ProductErrorType.PRODUCT_ALREADY_INACTIVE);
        }
        User usuarioManutencao = userResolverHelper.resolve(idUsuario);
        version.deactivate(usuarioManutencao);
        productVersionRepository.save(version);
    }



    // =========================================================================
    // Helpers
    // =========================================================================

    private String generateNextVersionCode(String codProduto) {
        return productVersionRepository.findLastVersion(codProduto)
                .map(last -> {
                    int num = Integer.parseInt(last.getCodVersao());
                    return String.valueOf(num + 1);
                })
                .orElse("1");
    }
}

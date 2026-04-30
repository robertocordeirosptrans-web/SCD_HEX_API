
package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.out.ProductChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.dto.ProdutoCodigoDescricaoDTO;
import br.sptrans.scd.channel.application.port.out.query.ChannelByProductProjection;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductChannelService implements ProductChannelUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProductChannelService.class);

    private final ProductChannelPersistencePort repository;
    private final SalesChannelPersistencePort salesChannelRepository;

    @Override
    public List<ProdutoCodigoDescricaoDTO> findProdutosCodigoDescricaoByChannel(
            String codCanal, String stCanaisProdutos, String stProdutos) {
        return repository.findProdutosCodigoDescricaoByChannel(codCanal, stCanaisProdutos, stProdutos);
    }



    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'product-projections-' + #codCanal + '-' + (#pageable != null ? #pageable.pageNumber : 'ALL') + '-' + (#pageable != null ? #pageable.pageSize : 'ALL')")
    public Page<ProductChannelProjection> findProjections(String codCanal, Pageable pageable) {
        if (codCanal != null && !codCanal.isEmpty()) {
            var canalOpt = salesChannelRepository.findById(codCanal);
            if (canalOpt.isEmpty()) {
                throw new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND);
            }
            var canal = canalOpt.get();
            var codAtividade = canal.getCodAtividade() != null ? canal.getCodAtividade().getCodAtividade() : null;
            if (codAtividade == null) {
                throw new ChannelException(ChannelErrorType.SALES_CHANNEL_INVALID_STATE);
            }
            Page<ProductChannelProjection> result;
            switch (codAtividade) {
                case "2" -> // Canal de Comercialização
                    result = repository.findCompletoByCanal(codCanal, pageable);
                case "3" -> // Canal de Distribuição
                    result = repository.findCompletoByCanalDistrib(codCanal, pageable);
                default -> throw new ChannelException(ChannelErrorType.SALES_CHANNEL_INVALID_STATE);
            }
            if (result == null || result.isEmpty()) {
                throw new ChannelException(ChannelErrorType.SALES_CHANNEL_INVALID_STATE);
            }
            return result;
        }
        throw new ChannelException(ChannelErrorType.SALES_CHANNEL_INVALID_STATE);
    }

    @Override
    @CacheEvict(value = "canais", allEntries = true)
    public ProductChannel createProductChannel(CreateProductChannelCommand cmd) {
        log.info("Criando produto do canal. Canal: {}, Produto: {}", cmd.codCanal(), cmd.codProduto());
        salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        ProductChannelKey key = new ProductChannelKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_ALREADY_EXISTS);
        }

        User usuCad = cmd.usuarioCadastro();
        ProductChannel entity = ProductChannel.criar(
                key,
                cmd.qtdLimiteComercializacao(),
                cmd.qtdMinimaEstoque(),
                cmd.qtdMaximaEstoque(),
                cmd.qtdMinimaRessuprimento(),
                cmd.qtdMaximaRessuprimento(),
                cmd.codOrgaoEmissor(),
                cmd.vlFace(),
                cmd.codStatus(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                cmd.codConvenio(),
                cmd.codTipoOperHM(),
                cmd.flgCarac(),
                usuCad,
                null);
        ProductChannel saved = repository.save(entity);
        log.info("Produto do canal criado. Canal: {}, Produto: {}", cmd.codCanal(), cmd.codProduto());
        return saved;
    }

    @Override
    @CacheEvict(value = "canais", allEntries = true)
    public ProductChannel updateProductChannel(String codCanal, String codProduto,
            UpdateProductChannelCommand cmd) {
        log.info("Atualizando produto do canal. Canal: {}, Produto: {}", codCanal, codProduto);
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        ProductChannel existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND));

        User usuMan = cmd.usuarioManutencao();
        existing.atualizar(
                cmd.qtdLimiteComercializacao(),
                cmd.qtdMinimaEstoque(),
                cmd.qtdMaximaEstoque(),
                cmd.qtdMinimaRessuprimento(),
                cmd.qtdMaximaRessuprimento(),
                cmd.codOrgaoEmissor(),
                cmd.vlFace(),
                cmd.codStatus(),
                java.time.LocalDateTime.now(),
                cmd.codConvenio(),
                cmd.codTipoOperHM(),
                cmd.flgCarac(),
                usuMan);
        ProductChannel saved = repository.save(existing);
        log.info("Produto do canal atualizado. Canal: {}, Produto: {}", codCanal, codProduto);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'product-' + #codCanal + '-' + #codProduto")
    public ProductChannel findProductChannel(String codCanal, String codProduto) {
        return repository.findById(new ProductChannelKey(codCanal, codProduto))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'product-all-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductChannel> findAllProductChannels(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'product-canal-' + #codCanal + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductChannel> findByCodCanal(String codCanal, Pageable pageable) {
        return repository.findByCodCanal(codCanal, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'product-produto-' + #codProduto + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductChannel> findByCodProduto(String codProduto, Pageable pageable) {
        return repository.findByCodProduto(codProduto, pageable);
    }

    @Override
    @CacheEvict(value = "canais", allEntries = true)
    public void deleteProductChannel(String codCanal, String codProduto) {
        log.info("Removendo produto do canal. Canal: {}, Produto: {}", codCanal, codProduto);
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND);
        }
        repository.deleteById(key);
        log.info("Produto do canal removido. Canal: {}, Produto: {}", codCanal, codProduto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelByProductProjection> findChannelsByProduct(String codProduto) {
        return repository.findChannelsByProduct(codProduto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelByProductProjection> findChannelsByProduct(String codProduto, Pageable pageable) {
        return repository.findChannelsByProduct(codProduto, pageable);
    }

}


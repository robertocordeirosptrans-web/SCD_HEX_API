package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.out.ProductChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
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

    private final ProductChannelPersistencePort repository;
    private final SalesChannelPersistencePort salesChannelRepository;



    
    @Override
    @Transactional(readOnly = true)
    public List<ProductChannelProjection> findProjections(String codCanal, String codProduto) {
        // Exemplo: busca por canal, pode ser adaptado para outros filtros
        if (codCanal != null && !codCanal.isEmpty()) {
            try {
               
                return repository.findCompletoByCanal(codCanal);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("codCanal deve ser um número inteiro", e);
            }
        }
        // Adapte para outros filtros conforme necessário
        throw new UnsupportedOperationException("Filtro de projections não implementado para os parâmetros fornecidos");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductChannelProjection> findProjections(String codCanal, String codProduto, Pageable pageable) {
        if (codCanal != null && !codCanal.isEmpty()) {
            try {
                return repository.findCompletoByCanal(codCanal, pageable);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("codCanal deve ser um número inteiro", e);
            }
        }
        throw new UnsupportedOperationException("Filtro de projections não implementado para os parâmetros fornecidos");
    }

    @Override
    public ProductChannel createProductChannel(CreateProductChannelCommand cmd) {
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
            null
        );
        return repository.save(entity);
    }

    @Override
    public ProductChannel updateProductChannel(String codCanal, String codProduto,
            UpdateProductChannelCommand cmd) {
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
            usuMan
        );
        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductChannel findProductChannel(String codCanal, String codProduto) {
        return repository.findById(new ProductChannelKey(codCanal, codProduto))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductChannel> findAllProductChannels(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductChannel> findByCodCanal(String codCanal, Pageable pageable) {
        return repository.findByCodCanal(codCanal, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductChannel> findByCodProduto(String codProduto, Pageable pageable) {
        return repository.findByCodProduto(codProduto, pageable);
    }

    @Override
    public void deleteProductChannel(String codCanal, String codProduto) {
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND);
        }
        repository.deleteById(key);
    }


}

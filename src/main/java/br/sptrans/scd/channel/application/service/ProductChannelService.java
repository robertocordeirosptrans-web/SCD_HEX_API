package br.sptrans.scd.channel.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.out.ProductChannelRepository;
import br.sptrans.scd.channel.application.port.out.SalesChannelRepository;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelperImpl;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductChannelService implements ProductChannelUseCase {

    private final ProductChannelRepository repository;
    private final UserResolverHelperImpl userResolverHelper;
    private final SalesChannelRepository salesChannelRepository;
    private final ProductChannelRepository productChannelJpaRepository;
    // private final ProductChannelJpaRepository productChannelJpaRepository;

    
    @Override
    @Transactional(readOnly = true)
    public List<ProductChannelProjection> findProjections(String codCanal, String codProduto) {
        // Exemplo: busca por canal, pode ser adaptado para outros filtros
        if (codCanal != null && !codCanal.isEmpty()) {
            try {
               
                return productChannelJpaRepository.findCompletoByCanal(codCanal);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("codCanal deve ser um número inteiro", e);
            }
        }
        // Adapte para outros filtros conforme necessário
        throw new UnsupportedOperationException("Filtro de projections não implementado para os parâmetros fornecidos");
    }

    @Override
    public ProductChannel createProductChannel(CreateProductChannelCommand cmd) {
        ProductChannelKey key = new ProductChannelKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_ALREADY_EXISTS);
        }

        User usuCad = userResolverHelper.resolve(cmd.idUsuarioCadastro());
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
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
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

        User usuMan = userResolverHelper.resolve(cmd.idUsuarioManutencao());
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
    public List<ProductChannel> findAllProductChannels() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductChannel> findByCodCanal(String codCanal) {
        return repository.findByCodCanal(codCanal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductChannel> findByCodProduto(String codProduto) {
        return repository.findByCodProduto(codProduto);
    }

    @Override
    public void deleteProductChannel(String codCanal, String codProduto) {
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND);
        }
        repository.deleteById(key);
    }

    private SalesChannel resolveChannel(String codCanal) {
        if (codCanal == null) {
            return null;
        }
        return salesChannelRepository.findById(codCanal).orElse(null);
    }
}

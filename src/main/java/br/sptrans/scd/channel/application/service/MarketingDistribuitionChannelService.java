
package br.sptrans.scd.channel.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MarketingDistribuitionChannelService implements MarketingDistribuitionChannelUseCase {

    private static final Logger log = LoggerFactory.getLogger(MarketingDistribuitionChannelService.class);

    private final MarketingDistribuitionChannelPersistencePort repository;
    private final SalesChannelPersistencePort salesChannelRepository;

    @Override
    public MarketingDistribuitionChannel createMarketingDistribuitionChannel(
            CreateMarketingDistribuitionChannelCommand cmd) {
        log.info("Criando canal de comercialização/distribuição. Comercialização: {}, Distribuição: {}",
            cmd.codCanalComercializacao(), cmd.codCanalDistribuicao());
        salesChannelRepository.findById(cmd.codCanalComercializacao())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        salesChannelRepository.findById(cmd.codCanalDistribuicao())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(cmd.codCanalComercializacao(), cmd.codCanalDistribuicao());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.MARKETING_CHANNEL_ALREADY_EXISTS);
        }

        User usuCad = cmd.usuarioCadastro();

        MarketingDistribuitionChannel entity = new MarketingDistribuitionChannel(
                key,
                ChannelDomainStatus.fromCode(cmd.codStatus()),
                null,
                null,
                usuCad,
                null);

        MarketingDistribuitionChannel saved = repository.save(entity);
        log.info("Canal de comercialização/distribuição criado. Comercialização: {}, Distribuição: {}",
            cmd.codCanalComercializacao(), cmd.codCanalDistribuicao());
        return saved;
    }

    @Override
    public MarketingDistribuitionChannel updateMarketingDistribuitionChannel(
            String codCanalComercializacao, String codCanalDistribuicao,
            UpdateMarketingDistribuitionChannelCommand cmd) {
        log.info("Atualizando canal de comercialização/distribuição. Comercialização: {}, Distribuição: {}",
            codCanalComercializacao, codCanalDistribuicao);
        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(codCanalComercializacao, codCanalDistribuicao);
        MarketingDistribuitionChannel existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.MARKETING_CHANNEL_NOT_FOUND));

        User usuMan = cmd.usuarioManutencao();

        MarketingDistribuitionChannel updated = new MarketingDistribuitionChannel(
                existing.getId(),
                ChannelDomainStatus.fromCode(cmd.codStatus()),
                existing.getDtCadastro(),
                null,
                existing.getIdUsuarioCadastro(),
                usuMan);

        MarketingDistribuitionChannel saved = repository.save(updated);
        log.info("Canal de comercialização/distribuição atualizado. Comercialização: {}, Distribuição: {}",
            codCanalComercializacao, codCanalDistribuicao);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'marketing-' + #codCanalComercializacao + '-' + #codCanalDistribuicao")
    public MarketingDistribuitionChannel findMarketingDistribuitionChannel(
            String codCanalComercializacao, String codCanalDistribuicao) {
        return repository.findById(new MarketingDistribuitionChannelKey(codCanalComercializacao, codCanalDistribuicao))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.MARKETING_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
        @Cacheable(value = "canais", key = "'marketing-all-' + #pageable.pageNumber + '-' + #pageable.pageSize")
        public Page<MarketingDistribuitionChannel> findAllMarketingDistribuitionChannels(Pageable pageable) {
        return repository.findAll(pageable);
    }




    @Override
        @CacheEvict(value = "canais", key = "'marketing-' + #codCanalComercializacao + '-' + #codCanalDistribuicao")
        public void deleteMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao) {
        log.info("Removendo canal de comercialização/distribuição. Comercialização: {}, Distribuição: {}",
            codCanalComercializacao, codCanalDistribuicao);
        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(codCanalComercializacao, codCanalDistribuicao);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.MARKETING_CHANNEL_NOT_FOUND);
        }
        repository.deleteById(key);
        log.info("Canal de comercialização/distribuição removido. Comercialização: {}, Distribuição: {}",
            codCanalComercializacao, codCanalDistribuicao);
    }

}

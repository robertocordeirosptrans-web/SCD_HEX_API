package br.sptrans.scd.channel.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelPersistencePort;

import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MarketingDistribuitionChannelService implements MarketingDistribuitionChannelUseCase {

    private final MarketingDistribuitionChannelPersistencePort repository;

    @Override
    public MarketingDistribuitionChannel createMarketingDistribuitionChannel(
            CreateMarketingDistribuitionChannelCommand cmd) {
        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(cmd.codCanalComercializacao(), cmd.codCanalDistribuicao());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.MARKETING_CHANNEL_ALREADY_EXISTS);
        }

        User usuCad = cmd.usuarioCadastro();

        MarketingDistribuitionChannel entity = new MarketingDistribuitionChannel(
                key,
                cmd.codStatus(),
                null,
                null,
                usuCad,
                null,
                cmd.codCanalComercializacao(),
                cmd.codCanalDistribuicao());

        return repository.save(entity);
    }

    @Override
    public MarketingDistribuitionChannel updateMarketingDistribuitionChannel(
            String codCanalComercializacao, String codCanalDistribuicao,
            UpdateMarketingDistribuitionChannelCommand cmd) {
        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(codCanalComercializacao, codCanalDistribuicao);
        MarketingDistribuitionChannel existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.MARKETING_CHANNEL_NOT_FOUND));

        User usuMan = cmd.usuarioManutencao();

        MarketingDistribuitionChannel updated = new MarketingDistribuitionChannel(
                existing.getId(),
                cmd.codStatus(),
                existing.getDtCadastro(),
                null,
                existing.getIdUsuarioCadastro(),
                usuMan,
                existing.getCodCanalComercializacao(),
                existing.getCodCanalDistribuicao());

        return repository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MarketingDistribuitionChannel findMarketingDistribuitionChannel(
            String codCanalComercializacao, String codCanalDistribuicao) {
        return repository.findById(new MarketingDistribuitionChannelKey(codCanalComercializacao, codCanalDistribuicao))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.MARKETING_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketingDistribuitionChannel> findAllMarketingDistribuitionChannels() {
        return repository.findAll();
    }




    @Override
    public void deleteMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao) {
        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(codCanalComercializacao, codCanalDistribuicao);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.MARKETING_CHANNEL_NOT_FOUND);
        }
        repository.deleteById(key);
    }

}

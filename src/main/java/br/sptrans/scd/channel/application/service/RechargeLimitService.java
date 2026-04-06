package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.application.port.in.RechargeLimitUseCase;
import br.sptrans.scd.channel.application.port.out.RechargeLimitPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RechargeLimitService implements RechargeLimitUseCase {

    private final RechargeLimitPersistencePort repository;
    private final SalesChannelPersistencePort salesChannelRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public RechargeLimit createRechargeLimit(CreateRechargeLimitCommand cmd) {
        salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        RechargeLimitKey key = new RechargeLimitKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.RECHARGE_LIMIT_ALREADY_EXISTS);
        }

        RechargeLimit entity = new RechargeLimit(
            key,
            cmd.dtInicioValidade(),
            cmd.dtFimValidade(),
            cmd.vlMinimoRecarga(),
            cmd.vlMaximoRecarga(),
            cmd.vlMaximoSaldo(),
            ChannelDomainStatus.fromCode(cmd.codStatus()),
            LocalDateTime.now(),
            cmd.usuario());

        return repository.save(entity);
    }

    @Override
    public RechargeLimit updateRechargeLimit(String codCanal, String codProduto, UpdateRechargeLimitCommand cmd) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        RechargeLimit existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND));

        existing.updateLimits(
                cmd.vlMinimoRecarga(),
                cmd.vlMaximoRecarga(),
                cmd.vlMaximoSaldo(),
                cmd.dtInicioValidade(),
                cmd.dtFimValidade(),
                userResolverHelper.resolve(cmd.idUsuario()));

        existing.setCodStatus(ChannelDomainStatus.fromCode(cmd.codStatus()));

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public RechargeLimit findRechargeLimit(RechargeLimitKey key) {
        RechargeLimit limit = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND));
        if (!limit.isVigente()) {
            throw new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_VIGENTE);
        }
        return limit;
    }



    @Override
    @Transactional(readOnly = true)
    public Page<RechargeLimit> findAllRechargeLimits(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void deleteRechargeLimit(RechargeLimitKey key) {
        RechargeLimit existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND));

        existing.expire(userResolverHelper.getCurrentUser());

        repository.save(existing);
    }
}
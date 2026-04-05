package br.sptrans.scd.channel.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.RechargeLimitUseCase;
import br.sptrans.scd.channel.application.port.out.RechargeLimitPersistencePort;
import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;

import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelperImpl;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RechargeLimitService implements RechargeLimitUseCase {

    private final RechargeLimitPersistencePort repository;
    private final UserResolverHelperImpl userResolverHelper;

    @Override
    public RechargeLimit createRechargeLimit(CreateRechargeLimitCommand cmd) {
        RechargeLimitKey key = new RechargeLimitKey(cmd.codCanal(), cmd.codProduto());
        if (repository.findById(key).isPresent()) {
            throw new ChannelException(ChannelErrorType.RECHARGE_LIMIT_ALREADY_EXISTS);
        }

        User usuario = cmd.usuario();

        RechargeLimit entity = new RechargeLimit();
        entity.setId(key);
        entity.setDtInicioValidade(cmd.dtInicioValidade());
        entity.setDtFimValidade(cmd.dtFimValidade());
        entity.setVlMinimoRecarga(cmd.vlMinimoRecarga());
        entity.setVlMaximoRecarga(cmd.vlMaximoRecarga());
        entity.setVlMaximoSaldo(cmd.vlMaximoSaldo());
        entity.setCodStatus(cmd.codStatus()); // Espera-se que cmd.codStatus() já use ChannelDomainStatus.getCode()
        entity.setIdUsuarioCadastro(usuario);

        return repository.save(entity);
    }

    @Override
    public RechargeLimit updateRechargeLimit(String codCanal, String codProduto,
            UpdateRechargeLimitCommand cmd) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        RechargeLimit existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND));

        User usuario = userResolverHelper.resolve(cmd.idUsuario());

        existing.setDtInicioValidade(cmd.dtInicioValidade());
        existing.setDtFimValidade(cmd.dtFimValidade());
        existing.setVlMinimoRecarga(cmd.vlMinimoRecarga());
        existing.setVlMaximoRecarga(cmd.vlMaximoRecarga());
        existing.setVlMaximoSaldo(cmd.vlMaximoSaldo());
        existing.setCodStatus(cmd.codStatus()); // Espera-se que cmd.codStatus() já use ChannelDomainStatus.getCode()
        existing.setIdUsuarioCadastro(usuario);

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public RechargeLimit findRechargeLimit(String codCanal, String codProduto) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        return repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RechargeLimit> findAllRechargeLimits() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RechargeLimit> findByCodCanal(String codCanal) {
        return repository.findByCodCanal(codCanal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RechargeLimit> findByCodProduto(String codProduto) {
        return repository.findByCodProduto(codProduto);
    }

    @Override
    public void deleteRechargeLimit(String codCanal, String codProduto) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND);
        }
        repository.deleteById(key);
    }

}

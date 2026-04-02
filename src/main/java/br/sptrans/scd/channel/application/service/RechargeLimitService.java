package br.sptrans.scd.channel.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.RechargeLimitUseCase;
import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;

import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;

import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RechargeLimitService implements RechargeLimitUseCase {

    private final RechargeLimitRepository repository;
    private final UserPersistencePort userRepository;

    @Override
    public RechargeLimit createRechargeLimit(CreateRechargeLimitCommand cmd) {
        RechargeLimitKey key = new RechargeLimitKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.RECHARGE_LIMIT_ALREADY_EXISTS);
        }

        User usuario = resolveUser(cmd.idUsuario());

        RechargeLimit entity = new RechargeLimit();
        entity.setId(key);
        entity.setDtInicioValidade(cmd.dtInicioValidade());
        entity.setDtFimValidade(cmd.dtFimValidade());
        entity.setVlMinimoRecarga(cmd.vlMinimoRecarga());
        entity.setVlMaximoRecarga(cmd.vlMaximoRecarga());
        entity.setVlMaximoSaldo(cmd.vlMaximoSaldo());
        entity.setCodStatus(cmd.codStatus());
        entity.setIdUsuarioCadastro(usuario.getIdUsuario());

        return repository.save(entity);
    }

    @Override
    public RechargeLimit updateRechargeLimit(String codCanal, String codProduto,
            UpdateRechargeLimitCommand cmd) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        RechargeLimit existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.RECHARGE_LIMIT_NOT_FOUND));

        User usuario = resolveUser(cmd.idUsuario());

        existing.setDtInicioValidade(cmd.dtInicioValidade());
        existing.setDtFimValidade(cmd.dtFimValidade());
        existing.setVlMinimoRecarga(cmd.vlMinimoRecarga());
        existing.setVlMaximoRecarga(cmd.vlMaximoRecarga());
        existing.setVlMaximoSaldo(cmd.vlMaximoSaldo());
        existing.setCodStatus(cmd.codStatus());
        existing.setIdUsuarioCadastro(usuario.getIdUsuario());

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

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) {
            return null;
        }
        return userRepository.findById(idUsuario).orElse(null);
    }
}

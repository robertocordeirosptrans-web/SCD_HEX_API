package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase;
import br.sptrans.scd.channel.application.port.out.AgreementValidityRepository;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AgreementValidityService implements AgreementValidityUseCase {

    private final AgreementValidityRepository repository;

    @Override
    public AgreementValidity createAgreementValidity(CreateAgreementValidityCommand cmd) {
        AgreementValidityKey key = new AgreementValidityKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_ALREADY_EXISTS);
        }

        AgreementValidity entity = new AgreementValidity(
                key,
                cmd.dtFimValidade(),
                cmd.dtInicioValidade(),
                cmd.codStatus(),
                LocalDateTime.now(),
                cmd.usuario());

        return repository.save(entity);
    }

    @Override
    public AgreementValidity updateAgreementValidity(String codCanal, String codProduto,
            UpdateAgreementValidityCommand cmd) {
        AgreementValidityKey key = new AgreementValidityKey(codCanal, codProduto);
        AgreementValidity existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_FOUND));

        existing.setDtFimValidade(cmd.dtFimValidade());
        existing.setDtInicioValidade(cmd.dtInicioValidade());
        existing.setCodStatus(cmd.codStatus());
        existing.setUsuario(cmd.usuario());

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public AgreementValidity findAgreementValidity(String codCanal, String codProduto) {
        return repository.findById(new AgreementValidityKey(codCanal, codProduto))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementValidity> findAllAgreementValidities() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementValidity> findByCodCanal(String codCanal) {
        return repository.findByCodCanal(codCanal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementValidity> findByCodProduto(String codProduto) {
        return repository.findByCodProduto(codProduto);
    }

    @Override
    public void deleteAgreementValidity(String codCanal, String codProduto) {
        AgreementValidityKey key = new AgreementValidityKey(codCanal, codProduto);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_FOUND);
        }
        repository.deleteById(key);
    }
}

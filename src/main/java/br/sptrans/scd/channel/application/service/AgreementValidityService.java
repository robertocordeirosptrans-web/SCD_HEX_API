package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase;
import br.sptrans.scd.channel.application.port.out.AgreementValidityPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AgreementValidityService implements AgreementValidityUseCase {

    private final AgreementValidityPersistencePort repository;
    private final SalesChannelPersistencePort salesChannelRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    @CacheEvict(value = "canais", allEntries = true)
    public AgreementValidity createAgreementValidity(CreateAgreementValidityCommand cmd) {
        salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        AgreementValidityKey key = new AgreementValidityKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_ALREADY_EXISTS);
        }

        AgreementValidity entity = new AgreementValidity(
            key,
            cmd.dtFimValidade(),
            cmd.dtInicioValidade(),
            ChannelDomainStatus.fromCode(cmd.codStatus()),
            LocalDateTime.now(),
            cmd.usuario());

        return repository.save(entity);
    }

    @Override
    @CacheEvict(value = "canais", allEntries = true)
    public AgreementValidity updateAgreementValidity(String codCanal, String codProduto,
            UpdateAgreementValidityCommand cmd) {
        AgreementValidityKey key = new AgreementValidityKey(codCanal, codProduto);
        AgreementValidity existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_FOUND));

        existing.updateValidity(cmd.dtFimValidade(), cmd.usuario());
        existing.setCodStatus(ChannelDomainStatus.fromCode(cmd.codStatus()));

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'agreement-' + #codCanal + '-' + #codProduto")
    public AgreementValidity findAgreementValidity(String codCanal, String codProduto) {
        AgreementValidity validity = repository.findById(new AgreementValidityKey(codCanal, codProduto))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_FOUND));
        if (!validity.isVigente()) {
            throw new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_VIGENTE);
        }
        return validity;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'agreement-all-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<AgreementValidity> findAllAgreementValidities(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'agreement-canal-' + #codCanal + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<AgreementValidity> findByCodCanal(String codCanal, Pageable pageable) {
        return repository.findByCodCanal(codCanal, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'agreement-produto-' + #codProduto + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<AgreementValidity> findByCodProduto(String codProduto, Pageable pageable) {
        return repository.findByCodProduto(codProduto, pageable);
    }

    @Override
    @CacheEvict(value = "canais", allEntries = true)
    public void deleteAgreementValidity(String codCanal, String codProduto) {
        AgreementValidityKey key = new AgreementValidityKey(codCanal, codProduto);
        AgreementValidity existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.AGREEMENT_VALIDITY_NOT_FOUND));

        existing.expire(userResolverHelper.getCurrentUser());

        repository.save(existing);
    }
}

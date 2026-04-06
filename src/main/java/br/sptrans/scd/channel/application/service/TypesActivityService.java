
package br.sptrans.scd.channel.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase;
import br.sptrans.scd.channel.application.port.out.TypesActivityPersistencePort;
import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TypesActivityService implements TypesActivityUseCase {

    private static final Logger log = LoggerFactory.getLogger(TypesActivityService.class);

    private final TypesActivityPersistencePort typesActivityRepository;

    @Override
    public TypesActivity createTypesActivity(CreateTypesActivityCommand command) {
        log.info("Criando tipo de atividade. Código: {}", command.codAtividade());
        if (typesActivityRepository.existsById(command.codAtividade())) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_CODE_ALREADY_EXISTS);
        }
        TypesActivity typesActivity = new TypesActivity(
            command.codAtividade(),
            command.desAtividade(),
            ChannelDomainStatus.INACTIVE,
            null,
            null
        );
        TypesActivity saved = typesActivityRepository.save(typesActivity);
        log.info("Tipo de atividade criado. Código: {}", saved.getCodAtividade());
        return saved;
    }

    @Override
    public TypesActivity updateTypesActivity(String codAtividade, UpdateTypesActivityCommand command) {
        log.info("Atualizando tipo de atividade. Código: {}", codAtividade);
        TypesActivity existing = typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));

        TypesActivity updated = new TypesActivity(
            existing.getCodAtividade(),
            command.desAtividade(),
            existing.getCodStatus(),
            existing.getDtCadastro(),
            existing.getDtManutencao()
        );
        TypesActivity saved = typesActivityRepository.save(updated);
        log.info("Tipo de atividade atualizado. Código: {}", codAtividade);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'types-' + #codAtividade")
    public TypesActivity findByTypesActivity(String codAtividade) {
        return typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'types-all-' + (#codStatus != null ? #codStatus.name() : 'ALL') + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TypesActivity> findAllTypesActivities(ChannelDomainStatus codStatus, Pageable pageable) {
        return typesActivityRepository.findAll(codStatus != null ? codStatus.getCode() : null, pageable);
    }

    @Override
    @CacheEvict(value = "canais", key = "'types-' + #codAtividade")
    public void activateTypesActivity(String codAtividade) {
        log.info("Ativando tipo de atividade. Código: {}", codAtividade);
        TypesActivity existing = typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
        if (ChannelDomainStatus.ACTIVE.equals(existing.getCodStatus())) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_ALREADY_ACTIVE);
        }
        typesActivityRepository.updateStatus(codAtividade, ChannelDomainStatus.ACTIVE.getCode());
        log.info("Tipo de atividade ativado. Código: {}", codAtividade);
    }

    @Override
    @CacheEvict(value = "canais", key = "'types-' + #codAtividade")
    public void inactivateTypesActivity(String codAtividade) {
        log.info("Inativando tipo de atividade. Código: {}", codAtividade);
        TypesActivity existing = typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
        if (ChannelDomainStatus.INACTIVE.equals(existing.getCodStatus())) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_ALREADY_INACTIVE);
        }
        typesActivityRepository.updateStatus(codAtividade, ChannelDomainStatus.INACTIVE.getCode());
        log.info("Tipo de atividade inativado. Código: {}", codAtividade);
    }

    @Override
    @CacheEvict(value = "canais", key = "'types-' + #codAtividade")
    public void deleteTypesActivity(String codAtividade) {
        log.info("Removendo tipo de atividade. Código: {}", codAtividade);
        if (!typesActivityRepository.existsById(codAtividade)) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND);
        }
        typesActivityRepository.deleteById(codAtividade);
    }
}

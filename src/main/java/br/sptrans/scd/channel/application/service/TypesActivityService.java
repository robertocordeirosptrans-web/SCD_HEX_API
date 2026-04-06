package br.sptrans.scd.channel.application.service;

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

    private final TypesActivityPersistencePort typesActivityRepository;

    @Override
    public TypesActivity createTypesActivity(CreateTypesActivityCommand command) {
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
        return typesActivityRepository.save(typesActivity);
    }

    @Override
    public TypesActivity updateTypesActivity(String codAtividade, UpdateTypesActivityCommand command) {
        TypesActivity existing = typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));

        TypesActivity updated = new TypesActivity(
            existing.getCodAtividade(),
            command.desAtividade(),
            existing.getCodStatus(),
            existing.getDtCadastro(),
            existing.getDtManutencao()
        );
        return typesActivityRepository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TypesActivity findByTypesActivity(String codAtividade) {
        return typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypesActivity> findAllTypesActivities(ChannelDomainStatus codStatus, Pageable pageable) {
        return typesActivityRepository.findAll(codStatus != null ? codStatus.getCode() : null, pageable);
    }

    @Override
    public void activateTypesActivity(String codAtividade) {
        TypesActivity existing = typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
        if (ChannelDomainStatus.ACTIVE.equals(existing.getCodStatus())) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_ALREADY_ACTIVE);
        }
        typesActivityRepository.updateStatus(codAtividade, ChannelDomainStatus.ACTIVE.getCode());
    }

    @Override
    public void inactivateTypesActivity(String codAtividade) {
        TypesActivity existing = typesActivityRepository.findById(codAtividade)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
        if (ChannelDomainStatus.INACTIVE.equals(existing.getCodStatus())) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_ALREADY_INACTIVE);
        }
        typesActivityRepository.updateStatus(codAtividade, ChannelDomainStatus.INACTIVE.getCode());
    }

    @Override
    public void deleteTypesActivity(String codAtividade) {
        if (!typesActivityRepository.existsById(codAtividade)) {
            throw new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND);
        }
        typesActivityRepository.deleteById(codAtividade);
    }
}

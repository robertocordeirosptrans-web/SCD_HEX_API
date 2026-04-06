package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.ClassificationPort;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.TypesActivityPersistencePort;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SalesChannelService implements SalesChannelUseCase {

    private final SalesChannelPersistencePort salesChannelRepository;
    private final TypesActivityPersistencePort typesActivityRepository;
    private final ClassificationPort classificationPort;
    private final UserResolverHelper userResolverHelper;

    @Override
    public SalesChannel createSalesChannel(CreateSalesChannelCommand cmd) {
        if (salesChannelRepository.existsById(cmd.codCanal())) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_CODE_ALREADY_EXISTS);
        }

        ClassificationPerson classificationPerson = null;
        if (cmd.codClassificacaoPessoa() != null) {
            classificationPerson = classificationPort.findById(cmd.codClassificacaoPessoa())
                    .orElseThrow(() -> new ChannelException(ChannelErrorType.CLASSIFICATION_PERSON_NOT_FOUND));
        }

        TypesActivity typesActivity = null;
        if (cmd.codAtividade() != null) {
            typesActivity = typesActivityRepository.findById(cmd.codAtividade())
                    .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
        }

        if(cmd.usuario() != null) {
            var user = userResolverHelper.resolve(cmd.usuario().getIdUsuario());
            if (user == null) {
                throw new ChannelException(ChannelErrorType.USER_NOT_FOUND);
            }
        }

        SalesChannel salesChannel = new SalesChannel(
                cmd.codCanal(),
                cmd.codDocumento(),
                cmd.codCanalSuperior(),
                cmd.desCanal(),
                cmd.codTipoDocumento(),
                LocalDateTime.now(),
                cmd.desRazaoSocial(),
                ChannelDomainStatus.INACTIVE,
                cmd.desNomeFantasia(),
                LocalDateTime.now(),
                cmd.vlCaucao(),
                cmd.dtInicioCaucao(),
                cmd.dtFimCaucao(),
                cmd.seqNivel(),
                cmd.flgCriticaNumlote(),
                cmd.flgLimiteDias(),
                cmd.flgProcessamentoAutomatico(),
                cmd.flgProcessamentoParcial(),
                cmd.flgSaldoDevedor(),
                cmd.numMinutoIniLibRecarga(),
                cmd.numMinutoFimLibRecarga(),
                cmd.flgEmiteReciboPedido(),
                cmd.flgSupercanal(),
                cmd.flgPagtoFuturo(),
                classificationPerson,
                typesActivity,
                cmd.usuario(),
                null);
        return salesChannelRepository.save(salesChannel);
    }

    @Override
    public SalesChannel updateSalesChannel(String codCanal, UpdateSalesChannelCommand cmd) {
        SalesChannel existing = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));

        ClassificationPerson classificationPerson = null;
        if (cmd.codClassificacaoPessoa() != null) {
            classificationPerson = classificationPort.findById(cmd.codClassificacaoPessoa())
                    .orElseThrow(() -> new ChannelException(ChannelErrorType.CLASSIFICATION_PERSON_NOT_FOUND));
        }

        TypesActivity typesActivity = null;
        if (cmd.codAtividade() != null) {
            typesActivity = typesActivityRepository.findById(cmd.codAtividade())
                    .orElseThrow(() -> new ChannelException(ChannelErrorType.TYPES_ACTIVITY_NOT_FOUND));
        }

        SalesChannel updated = new SalesChannel(
                existing.getCodCanal(),
                existing.getCodDocumento(),
                cmd.codCanalSuperior(),
                cmd.desCanal(),
                existing.getCodTipoDocumento(),
                LocalDateTime.now(),
                cmd.desRazaoSocial(),
                existing.getStCanais(),
                cmd.desNomeFantasia(),
                existing.getDtCadastro(),
                cmd.vlCaucao(),
                cmd.dtInicioCaucao(),
                cmd.dtFimCaucao(),
                cmd.seqNivel(),
                cmd.flgCriticaNumlote(),
                cmd.flgLimiteDias(),
                cmd.flgProcessamentoAutomatico(),
                cmd.flgProcessamentoParcial(),
                cmd.flgSaldoDevedor(),
                cmd.numMinutoIniLibRecarga(),
                cmd.numMinutoFimLibRecarga(),
                cmd.flgEmiteReciboPedido(),
                cmd.flgSupercanal(),
                cmd.flgPagtoFuturo(),
                classificationPerson,
                typesActivity,
                existing.getIdUsuarioCadastro(),
                cmd.usuario());
        return salesChannelRepository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SalesChannel findBySalesChannel(String codCanal) {
        return salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesChannel> findAllSalesChannels(ChannelDomainStatus stCanais, Pageable pageable) {
        return salesChannelRepository.findAll(stCanais != null ? stCanais.getCode() : null, pageable);
    }

    @Override
    public void activateSalesChannel(String codCanal, User usuario) {
        SalesChannel existing = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        if (ChannelDomainStatus.ACTIVE.equals(existing.getStCanais())) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_ALREADY_ACTIVE);
        }
        salesChannelRepository.updateStatus(codCanal, ChannelDomainStatus.ACTIVE.getCode(), usuario);
    }

    @Override
    public void inactivateSalesChannel(String codCanal, User usuario) {
        SalesChannel existing = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        if (ChannelDomainStatus.INACTIVE.equals(existing.getStCanais())) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_ALREADY_INACTIVE);
        }
        salesChannelRepository.updateStatus(codCanal, ChannelDomainStatus.INACTIVE.getCode(), usuario);
    }

    @Override
    public void deleteSalesChannel(String codCanal) {
        if (!salesChannelRepository.existsById(codCanal)) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND);
        }
        salesChannelRepository.deleteById(codCanal);
    }

}

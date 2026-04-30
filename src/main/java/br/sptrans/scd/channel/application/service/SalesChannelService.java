
package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.ClassificationPort;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.SubSalesChannelProjection;
import br.sptrans.scd.channel.adapter.out.persistence.entity.SalesChannelEntityJpa;
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

    private static final Logger log = LoggerFactory.getLogger(SalesChannelService.class);

    private final SalesChannelPersistencePort salesChannelRepository;
    private final TypesActivityPersistencePort typesActivityRepository;
    private final ClassificationPort classificationPort;
    private final UserResolverHelper userResolverHelper;

    @Override
    public SalesChannel createSalesChannel(CreateSalesChannelCommand cmd) {
        log.info("Criando canal de venda. Código: {}", cmd.codCanal());
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

        if (cmd.usuario() != null) {
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
                cmd.usuario());
        SalesChannel saved = salesChannelRepository.save(salesChannel);
        log.info("Canal de venda criado com sucesso. Código: {}", saved.getCodCanal());
        return saved;
    }

    public List<SalesChannel> findByCodClassificacaoPessoaAndStCanais(String codClassificacaoPessoa, String stCanais) {
        return salesChannelRepository.findByCodClassificacaoPessoaAndStCanais(codClassificacaoPessoa, stCanais);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubSalesChannelProjection> findSubChannelsByCodCanalSuperior(
            String codCanalSuperior, Pageable pageable) {
        return salesChannelRepository.findSubChannelsByCodCanalSuperior(codCanalSuperior, pageable);
    }

    @Override
    public SalesChannel updateSalesChannel(String codCanal, UpdateSalesChannelCommand cmd) {
        log.info("Atualizando canal de venda. Código: {}", codCanal);
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
        SalesChannel saved = salesChannelRepository.save(updated);
        log.info("Canal de venda atualizado com sucesso. Código: {}", codCanal);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "#codCanal")
    public SalesChannel findBySalesChannel(String codCanal) {
        return salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesChannel> findAllSalesChannels(Specification<SalesChannelEntityJpa> spec, Pageable pageable) {
        return salesChannelRepository.findAll(spec, pageable);
    }

    @Override
    @CacheEvict(value = "canais", key = "#codCanal")
    public void activateSalesChannel(String codCanal, User usuario) {
        log.info("Ativando canal de venda. Código: {}", codCanal);
        SalesChannel existing = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        if (ChannelDomainStatus.ACTIVE.equals(existing.getStCanais())) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_ALREADY_ACTIVE);
        }
        salesChannelRepository.updateStatus(codCanal, ChannelDomainStatus.ACTIVE.getCode(), usuario);
        log.info("Canal de venda ativado. Código: {}", codCanal);
    }

    @Override
    @CacheEvict(value = "canais", key = "#codCanal")
    public void inactivateSalesChannel(String codCanal, User usuario) {
        log.info("Inativando canal de venda. Código: {}", codCanal);
        SalesChannel existing = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
        if (ChannelDomainStatus.INACTIVE.equals(existing.getStCanais())) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_ALREADY_INACTIVE);
        }
        salesChannelRepository.updateStatus(codCanal, ChannelDomainStatus.INACTIVE.getCode(), usuario);
        log.info("Canal de venda inativado. Código: {}", codCanal);
    }

    @Override
    @CacheEvict(value = "canais", key = "#codCanal")
    public void deleteSalesChannel(String codCanal) {
        log.info("Removendo canal de venda. Código: {}", codCanal);
        if (!salesChannelRepository.existsById(codCanal)) {
            throw new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND);
        }
        salesChannelRepository.deleteById(codCanal);
        log.info("Canal de venda removido. Código: {}", codCanal);
    }

}

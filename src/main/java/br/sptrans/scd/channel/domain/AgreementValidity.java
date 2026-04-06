package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AgreementValidity {

    private final AgreementValidityKey id;

    @Setter private LocalDateTime dtFimValidade;

    @Setter private LocalDateTime dtInicioValidade;

    @Setter private ChannelDomainStatus codStatus;

    @Setter private LocalDateTime dtManutencao;

    @Setter private User usuario;

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isAtivo() {
        return ChannelDomainStatus.ACTIVE.equals(codStatus);
    }

    public boolean isInativo() {
        return ChannelDomainStatus.INACTIVE.equals(codStatus);
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    /**
     * Ativa a vigência do convênio.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.codStatus = ChannelDomainStatus.ACTIVE;
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa a vigência do convênio.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Vigência
    // -------------------------------------------------------------------------

    /**
     * Verifica se a vigência de convênio está vigente:
     * status ativo E dentro do período de validade.
     */
    public boolean isVigente() {
        if (!isAtivo()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (dtInicioValidade != null && now.isBefore(dtInicioValidade)) {
            return false;
        }
        if (dtFimValidade != null && now.isAfter(dtFimValidade)) {
            return false;
        }
        return true;
    }

    /**
     * Encerra a vigência do convênio, definindo {@code dtFimValidade}
     * para o momento atual e inativando o registro.
     *
     * @param operador usuário responsável pela operação
     */
    public void expire(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.dtFimValidade = LocalDateTime.now();
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Atualiza o período de vigência do convênio.
     *
     * <p>Regra de negócio: {@code dtFim} não pode ser anterior a {@code dtInicioValidade} existente.</p>
     *
     * @param dtFim    nova data de fim de vigência
     * @param operador usuário responsável pela operação
     */
    public void updateValidity(LocalDateTime dtFim, User operador) {
        if (this.dtInicioValidade != null && dtFim != null && this.dtInicioValidade.isAfter(dtFim)) {
            throw new IllegalArgumentException(
                "Data de início de vigência não pode ser posterior à data de fim");
        }
        this.dtFimValidade = dtFim;
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }
}

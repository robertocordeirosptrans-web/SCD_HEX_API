package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AgreementValidity {

    private final AgreementValidityKey id;

    @Setter private LocalDateTime dtFimValidade;

    @Setter private LocalDateTime dtInicioValidade;

    @Setter private String codStatus;

    @Setter private LocalDateTime dtManutencao;

    @Setter private User usuario;

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isAtivo() {
        try {
            return ChannelDomainStatus.ACTIVE.equals(ChannelDomainStatus.fromCode(codStatus));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInativo() {
        try {
            return ChannelDomainStatus.INACTIVE.equals(ChannelDomainStatus.fromCode(codStatus));
        } catch (Exception e) {
            return false;
        }
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
        this.codStatus = ChannelDomainStatus.ACTIVE.getCode();
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa a vigência do convênio.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE.getCode();
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Atualiza o período de vigência do convênio.
     *
     * <p>Regra de negócio: {@code dtInicio} não pode ser posterior a {@code dtFim}.</p>
     *
     * @param dtInicio nova data de início de vigência
     * @param dtFim    nova data de fim de vigência
     * @param operador usuário responsável pela operação
     */
    public void updateValidity(LocalDateTime dtInicio, LocalDateTime dtFim, User operador) {
        if (dtInicio != null && dtFim != null && dtInicio.isAfter(dtFim)) {
            throw new IllegalArgumentException(
                "Data de início de vigência não pode ser posterior à data de fim");
        }
        this.dtInicioValidade = dtInicio;
        this.dtFimValidade = dtFim;
        this.usuario = operador;
        this.dtManutencao = LocalDateTime.now();
    }
}

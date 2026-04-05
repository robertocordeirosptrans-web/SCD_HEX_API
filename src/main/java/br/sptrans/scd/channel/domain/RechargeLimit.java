package br.sptrans.scd.channel.domain;

import java.math.BigDecimal;
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
public class RechargeLimit {

    private final RechargeLimitKey id;

    @Setter private LocalDateTime dtInicioValidade;
    @Setter private LocalDateTime dtFimValidade;
    @Setter private BigDecimal vlMinimoRecarga;
    @Setter private BigDecimal vlMaximoRecarga;
    @Setter private BigDecimal vlMaximoSaldo;
    @Setter private String codStatus;
    @Setter private LocalDateTime dtManutencao;

    private final User idUsuarioCadastro;

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
     * Ativa o limite de recarga.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.codStatus = ChannelDomainStatus.ACTIVE.getCode();
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o limite de recarga.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE.getCode();
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Atualiza os valores e vigência do limite de recarga.
     *
     * <p>Regras de negócio:</p>
     * <ul>
     *   <li>{@code vlMinimoRecarga} não pode ser maior que {@code vlMaximoRecarga}</li>
     *   <li>{@code dtInicioValidade} não pode ser posterior a {@code dtFimValidade}</li>
     * </ul>
     *
     * @param vlMinimo       novo valor mínimo de recarga
     * @param vlMaximo       novo valor máximo de recarga
     * @param vlMaximoSaldo  novo valor máximo de saldo
     * @param dtInicio       nova data de início de vigência
     * @param dtFim          nova data de fim de vigência
     * @param operador       usuário responsável pela operação
     */
    public void updateLimits(
            BigDecimal vlMinimo,
            BigDecimal vlMaximo,
            BigDecimal vlMaximoSaldo,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            User operador) {
        if (vlMinimo != null && vlMaximo != null && vlMinimo.compareTo(vlMaximo) > 0) {
            throw new IllegalArgumentException(
                "Valor mínimo de recarga não pode ser maior que o valor máximo");
        }
        if (dtInicio != null && dtFim != null && dtInicio.isAfter(dtFim)) {
            throw new IllegalArgumentException(
                "Data de início de vigência não pode ser posterior à data de fim");
        }
        this.vlMinimoRecarga = vlMinimo;
        this.vlMaximoRecarga = vlMaximo;
        this.vlMaximoSaldo = vlMaximoSaldo;
        this.dtInicioValidade = dtInicio;
        this.dtFimValidade = dtFim;
        this.dtManutencao = LocalDateTime.now();
    }
}

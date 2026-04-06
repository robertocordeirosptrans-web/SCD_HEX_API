
package br.sptrans.scd.channel.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.shared.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class RechargeLimit {

    private final RechargeLimitKey id;

    @Setter
    private LocalDateTime dtInicioValidade;
    @Setter
    private LocalDateTime dtFimValidade;
    @Setter
    private BigDecimal vlMinimoRecarga;
    @Setter
    private BigDecimal vlMaximoRecarga;
    @Setter
    private BigDecimal vlMaximoSaldo;
    @Setter
    private ChannelDomainStatus codStatus;
    @Setter
    private LocalDateTime dtManutencao;

    @Setter
    private User idUsuarioCadastro;

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
     * Ativa o limite de recarga.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.codStatus = ChannelDomainStatus.ACTIVE;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o limite de recarga.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Atualiza os valores e vigência do limite de recarga.
     *
     * <p>
     * Regras de negócio:
     * </p>
     * <ul>
     * <li>{@code vlMinimoRecarga} não pode ser maior que
     * {@code vlMaximoRecarga}</li>
     * <li>{@code dtInicioValidade} não pode ser posterior a
     * {@code dtFimValidade}</li>
     * </ul>
     *
     * @param vlMinimo      novo valor mínimo de recarga
     * @param vlMaximo      novo valor máximo de recarga
     * @param vlMaximoSaldo novo valor máximo de saldo
     * @param dtInicio      nova data de início de vigência
     * @param dtFim         nova data de fim de vigência
     * @param operador      usuário responsável pela operação
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

    // -------------------------------------------------------------------------
    // Vigência
    // -------------------------------------------------------------------------

    /**
     * Verifica se o limite de recarga está vigente:
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
     * Encerra a vigência do limite de recarga, definindo {@code dtFimValidade}
     * para o momento atual e inativando o registro.
     *
     * @param operador usuário responsável pela operação
     */
    public void expire(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.dtFimValidade = LocalDateTime.now();
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Valida os limites de recarga para um valor informado.
     * Lança IllegalStateException ou ValidationException em caso de violação.
     * Retorna null se estiver tudo ok.
     */
    public void validarLimites(BigDecimal valorTotal) {
        LocalDateTime agora = LocalDateTime.now();
        if (this.dtFimValidade != null && this.dtFimValidade.isBefore(agora)) {
            throw new ValidationException(
                    "Limite de recarga expirado para o canal " + (id != null ? id.getCodCanal() : "") +
                            " e produto " + (id != null ? id.getCodProduto() : ""));
        }
        if (this.vlMinimoRecarga != null && valorTotal.compareTo(this.vlMinimoRecarga) < 0) {
            throw new ValidationException(
                    "Valor " + valorTotal + " abaixo do limite mínimo de recarga " + this.vlMinimoRecarga);
        }
        if (this.vlMaximoRecarga != null && valorTotal.compareTo(this.vlMaximoRecarga) > 0) {
            throw new ValidationException(
                    "Valor " + valorTotal + " acima do limite máximo de recarga " + this.vlMaximoRecarga);
        }
    }
}

package br.sptrans.scd.channel.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesChannel {

    private String codCanal;

    private String codDocumento;

    private String codCanalSuperior;

    private String desCanal;

    private String codTipoDocumento;

    private LocalDateTime dtManutencao;

    private String desRazaoSocial;

    private String stCanais;

    private String desNomeFantasia;

    private LocalDateTime dtCadastro;

    private BigDecimal vlCaucao;

    private LocalDate dtInicioCaucao;

    private LocalDate dtFimCaucao;

    private Integer seqNivel;

    private String flgCriticaNumlote;

    private Integer flgLimiteDias;

    private String flgProcessamentoAutomatico;

    private String flgProcessamentoParcial;

    private String flgSaldoDevedor;

    private Integer numMinutoIniLibRecarga;

    private Integer numMinutoFimLibRecarga;

    private String flgEmiteReciboPedido;

    private String flgSupercanal;

    private String flgPagtoFuturo;

    private ClassificationPerson codClassificacaoPessoa;

    private TypesActivity codAtividade;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;
    // --- Métodos de negócio (DDD) ---

    /**
     * Verifica se o canal está habilitado para processamento automático.
     */
    public boolean isProcessamentoAutomaticoHabilitado() {
        return "S".equalsIgnoreCase(flgProcessamentoAutomatico);
    }

    /**
     * Verifica se o canal está habilitado para processamento parcial.
     */
    public boolean isProcessamentoParcialHabilitado() {
        return "S".equalsIgnoreCase(flgProcessamentoParcial);
    }

    /**
     * Verifica se o canal possui saldo devedor.
     */
    public boolean isSaldoDevedorHabilitado() {
        return "S".equalsIgnoreCase(flgSaldoDevedor);
    }

    /**
     * Verifica se o canal faz crítica de número de lote.
     */
    public boolean isCriticaNumloteHabilitada() {
        return "S".equalsIgnoreCase(flgCriticaNumlote);
    }

    /**
     * Verifica se o canal possui limite de dias.
     */
    public boolean isLimiteDiasHabilitado() {
        return flgLimiteDias != null && flgLimiteDias > 0;
    }

    /**
     * Verifica se o canal emite recibo de pedido.
     */
    public boolean isEmiteReciboPedido() {
        return "S".equalsIgnoreCase(flgEmiteReciboPedido);
    }

    /**
     * Verifica se o canal é supercanal.
     */
    public boolean isSupercanal() {
        return "S".equalsIgnoreCase(flgSupercanal);
    }

    /**
     * Verifica se o canal permite pagamento futuro.
     */
    public boolean isPagamentoFuturoHabilitado() {
        return "S".equalsIgnoreCase(flgPagtoFuturo);
    }

    /**
     * Verifica se o canal está ativo usando o enum ChannelDomainStatus.
     */
    public boolean isAtivo() {
        try {
            return ChannelDomainStatus.ACTIVE.equals(
                ChannelDomainStatus.fromCode(stCanais)
            );
        } catch (Exception e) {
            return false;
        }
    }
}

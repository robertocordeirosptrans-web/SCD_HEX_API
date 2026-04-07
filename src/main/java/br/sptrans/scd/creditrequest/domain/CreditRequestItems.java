package br.sptrans.scd.creditrequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditRequestItems {

    private CreditRequestItemsKey id;

    private CreditRequest solicitacao;

    private String codCanal;

    private Long idUsuarioCadastro;

    private String codVersao;

    private String numLogicoCartao;

    private String codProduto;

    private String codTipoDocumento;

    private String codSituacao;

    private Integer qtdItem;

    private BigDecimal vlUnitario;

    private BigDecimal vlItem;

    private LocalDateTime dtRecarga;

    private BigDecimal vlCarregado;

    private BigDecimal vlAjuste;

    private String flgAjuste;

    private String idFuncionario;

    private String codAssinaturaHsm;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private Integer seqRecarga;

    private LocalDateTime dtEnvioHm;

    private LocalDateTime dtRetornoHm;

    private Long idUsuarioManutencao;

    private LocalDateTime dtAssinatura;

    private LocalDateTime dtPagtoEconomica;

    private Long sqPid;

    private LocalDateTime dtInicProcesso;

    private Long idUsuarioCartao = 0L;

    private Integer sqRecarga = 0;

    private BigDecimal vlTxadm = BigDecimal.ZERO;

    private BigDecimal vlTxserv = BigDecimal.ZERO;

    private BigDecimal vlTxtotal;

    private String flgEvento;

    private BigDecimal vlEvento;

    private String flgOutrasVias;

    private String codAssdigRecarga;

    private BigDecimal vlAutorizacaoHm;

    private Integer flgLiminarLoja;

    private String codProdutoHm;

    private Integer qtdDiasUtilizados;

    /**
     * Regra 4.3 — indica se este item deve ser marcado como RECARREGADO com
     * valor zero.
     *
     * <p>
     * Quando o valor efetivo do item ({@code vlItem + valorEvento}) for menor
     * ou igual a zero, o item é considerado "recarregado por evento" sem valor
     * monetário, e deve receber o status RECARREGADO.</p>
     *
     * @param valorEvento valor do evento financeiro associado ao item (pode ser
     * null)
     * @return {@code true} se o valor efetivo for ≤ 0
     */

    public boolean mustIndicateReloadedByEvent(BigDecimal valorEvento) {
        BigDecimal valorEfetivo = (this.vlItem != null ? this.vlItem : BigDecimal.ZERO)
                .add(valorEvento != null ? valorEvento : BigDecimal.ZERO);
        return valorEfetivo.compareTo(BigDecimal.ZERO) <= 0;
    }

    // --- Métodos de consulta de estado ---

    public boolean isRecarregado() {
        return SituationCreditRequestItems.RECARREGADO.getCode().equals(codSituacao);
    }

    public boolean isCancelado() {
        return SituationCreditRequestItems.CANCELADO.getCode().equals(codSituacao);
    }

    public boolean isBloqueado() {
        return SituationCreditRequestItems.BLOQUEADO.getCode().equals(codSituacao);
    }

    public boolean isPendenteLiquidacao() {
        return SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode().equals(codSituacao);
    }

    public boolean isLiberadoParaRecarga() {
        return SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode().equals(codSituacao);
    }

    public boolean isTerminal() {
        return isRecarregado() || isCancelado();
    }

    // --- Métodos de transição de estado ---

    /**
     * Solicita o cancelamento do item de recarga.
     * Não é permitido cancelar um item já recarregado.
     */
    public void cancelar(Long idUsuario) {
        if (isRecarregado()) {
            throw new IllegalStateException("Não é possível cancelar um item já recarregado");
        }
        this.codSituacao = SituationCreditRequestItems.CANCELAMENTO_SOLICITADO.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Solicita o bloqueio do item de recarga.
     * Não é permitido bloquear um item cancelado.
     */
    public void bloquear(Long idUsuario) {
        if (isCancelado()) {
            throw new IllegalStateException("Não é possível bloquear um item cancelado");
        }
        this.codSituacao = SituationCreditRequestItems.BLOQUEIO_SOLICITADO.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Solicita o desbloqueio do item de recarga.
     */
    public void desbloquear(Long idUsuario) {
        this.codSituacao = SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    
}

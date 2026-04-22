package br.sptrans.scd.creditrequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreditRequestItems {



    private CreditRequestItemsKey id;

    private CreditRequest solicitacao;

    private String codCanal;

    private Long idUsuarioCadastro;

    private String codVersao;

    private String numLogicoCartao;

    private String codProduto;

    private String codTipoDocumento;

    private SituationCreditRequestItems codSituacao;

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

    private Long idUsuarioCartao;

    private Integer sqRecarga;

    private BigDecimal vlTxadm;

    private BigDecimal vlTxserv;

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
        return SituationCreditRequestItems.RECARREGADO.equals(codSituacao);
    }

    public boolean isCancelado() {
        return SituationCreditRequestItems.CANCELADO.equals(codSituacao);
    }

    public boolean isBloqueado() {
        return SituationCreditRequestItems.BLOQUEADO.equals(codSituacao);
    }

    public boolean isPendenteLiquidacao() {
        return SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.equals(codSituacao);
    }

    public boolean isLiberadoParaRecarga() {
        return SituationCreditRequestItems.LIBERADO_PARA_RECARGA.equals(codSituacao);
    }

    public boolean isTerminal() {
        return isRecarregado() || isCancelado();
    }

    // --- Métodos de transição de estado ---

    /**
     * Valida se a transição de status é permitida para o item.
     * Pode ser expandido para regras mais complexas.
     */
    public void validarTransicao(String novoStatus) {
        // Exemplo simples: não permite voltar para CRIADO se já avançou
        if (SituationCreditRequestItems.CRIADO.equals(codSituacao) && !SituationCreditRequestItems.CRIADO.getCode().equals(novoStatus)) {
            // ok
        } else if (isTerminal()) {
            throw new IllegalStateException("Não é possível transitar de um estado terminal");
        }
        // Outras regras podem ser adicionadas aqui
    }

    /**
     * Cria um histórico do item a partir do estado atual.
     */
    public HistCreditRequestItems criarHistorico(Long seqHistSdis, String origemTransicao, User usuario) {
        HistCreditRequestItems hist = new HistCreditRequestItems();
        var histId = new HistCreditRequestItemsKey();
        histId.setNumSolicitacao(this.id.getNumSolicitacao());
        histId.setNumSolicitacaoItem(this.id.getNumSolicitacaoItem());
        histId.setCodCanal(this.id.getCodCanal());
        histId.setSeqHistSdis(seqHistSdis);
        hist.setId(histId);
        hist.setCodTipoDocumento(this.codTipoDocumento);
        hist.setCodSituacao(this.codSituacao.getCode());
        hist.setDtTransicao(LocalDateTime.now());
        hist.setIdOrigemTransicao(origemTransicao);
        hist.setDtCadastro(this.dtCadastro);
        hist.setDtManutencao(this.dtManutencao);
        hist.setDtPgtoEconomica(this.dtPagtoEconomica);
        hist.setSqPID(this.sqPid);
        hist.setDtInicProcesso(this.dtInicProcesso);
        hist.setDtFimProcesso(this.dtRetornoHm); // ou outro campo adequado
        hist.setDesOcorrencia(null); // preencher se necessário
        hist.setIdUsuarioTransicao(usuario);
        return hist;
    }

    /**
     * Solicita o cancelamento do item de recarga.
     * Não é permitido cancelar um item já recarregado.
     */
    public void cancelar(Long idUsuario) {
        if (isRecarregado()) {
            throw new IllegalStateException("Não é possível cancelar um item já recarregado");
        }
        this.codSituacao = SituationCreditRequestItems.CANCELAMENTO_SOLICITADO;
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
        this.codSituacao = SituationCreditRequestItems.BLOQUEIO_SOLICITADO;
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Solicita o desbloqueio do item de recarga.
     */
    public void desbloquear(Long idUsuario) {
        this.codSituacao = SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO;
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    
}

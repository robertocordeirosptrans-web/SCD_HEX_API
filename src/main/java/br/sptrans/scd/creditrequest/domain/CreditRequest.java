package br.sptrans.scd.creditrequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreditRequest {



    private Long numSolicitacao;

    private String codCanal;

    private Long idUsuarioCadastro;

    private String codTipoDocumento;

    private String codSituacao;

    private String codFormaPagto;

    private LocalDateTime dtSolicitacao;

    private LocalDateTime dtPrevLiberacao;

    private LocalDateTime dtAceite;

    private LocalDateTime dtConfirmaPagto;

    private LocalDateTime dtPagtoEconomica;

    private String codUsuarioPortador;

    private LocalDateTime dtLiberacaoEfetiva;

    private String codEnderecoEntrega;

    private String numLote;

    private LocalDateTime dtFinanceira;

    private BigDecimal vlTotal;

    private LocalDateTime dtCadastro;

    private String flgCanc;

    private LocalDateTime dtManutencao;

    private LocalDateTime dtEnvioHm;

    private Long idUsuarioManutencao;

    private String flgBloq;

    private BigDecimal vlPago;

    private Long sqPid;

    private LocalDateTime dtInicProcesso;

    private BigDecimal vlServicoRecarga;

    private BigDecimal vlServicoAdm;

    private String flgEvento;

    private BigDecimal vlEvento;

    private List<CreditRequestItems> itens = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Consultas de estado
    // -------------------------------------------------------------------------

    public boolean isCancelado() {
        return "S".equalsIgnoreCase(flgCanc)
                || SituationCreditRequest.CANCELADO.getCode().equals(codSituacao);
    }

    public boolean isBloqueado() {
        return "S".equalsIgnoreCase(flgBloq)
                || SituationCreditRequest.BLOQUEADO.getCode().equals(codSituacao);
    }

    public boolean isAtendido() {
        return SituationCreditRequest.ATENDIDO_TOTALMENTE.getCode().equals(codSituacao)
                || SituationCreditRequest.ATENDIDO_PARCIALMENTE.getCode().equals(codSituacao);
    }

    public boolean isPendenteLiquidacao() {
        return SituationCreditRequest.ACEITO_PENDENTE_LIQUIDACAO.getCode().equals(codSituacao);
    }

    public boolean isLiberadoParaRecarga() {
        return SituationCreditRequest.LIBERADO_PARA_RECARGA.getCode().equals(codSituacao);
    }

    public boolean isTerminal() {
        return SituationCreditRequest.ATENDIDO_TOTALMENTE.getCode().equals(codSituacao)
                || SituationCreditRequest.CANCELADO.getCode().equals(codSituacao);
    }

    public BigDecimal calcularVlTotal() {
        return itens.stream()
                .map(CreditRequestItems::getVlItem)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // -------------------------------------------------------------------------
    // Transições de estado
    // -------------------------------------------------------------------------

    /**
     * Valida se a transição de status é permitida para a solicitação.
     * Pode ser expandido para regras mais complexas.
     */
    public void validarTransicao(String novoStatus) {
        if (isTerminal()) {
            throw new IllegalStateException("Não é possível transitar de um estado terminal");
        }
        // Outras regras podem ser adicionadas aqui
    }

    /**
     * Cria um histórico da solicitação a partir do estado atual.
     */
    public HistCreditRequest criarHistorico(Long seqHistSdis, String origemTransicao, User usuario) {
        HistCreditRequest hist = new HistCreditRequest();
        var histId = new HistCreditRequestKey();
        histId.setNumSolicitacao(this.numSolicitacao);
        histId.setNumSolicitacaoItem(null); // ou preencher se aplicável
        histId.setCodCanal(this.codCanal);
        histId.setSeqHistSdis(seqHistSdis);
        hist.setId(histId);
        hist.setCodTipoDocumento(this.codTipoDocumento);
        hist.setCodSituacao(this.codSituacao);
        hist.setDtTransicao(LocalDateTime.now());
        hist.setIdOrigemTransicao(origemTransicao);
        hist.setDtCadastro(this.dtCadastro);
        hist.setDtManutencao(this.dtManutencao);
        hist.setDtPgtoEconomica(this.dtPagtoEconomica);
        hist.setSqPID(this.sqPid);
        hist.setDtInicProcesso(this.dtInicProcesso);
        hist.setDtFimProcesso(this.dtFinanceira); // ou outro campo adequado
        hist.setDesOcorrencia(null); // preencher se necessário
        hist.setIdUsuarioTransicao(usuario);
        return hist;
    }

    /**
     * Solicita o cancelamento da solicitação de crédito.
     *
     * <p>
     * Regra de negócio: não é permitido cancelar uma solicitação já atendida.
     * </p>
     *
     * @param idUsuario identificador do usuário que solicitou o cancelamento
     */
    public void cancelar(Long idUsuario) {
        if (isAtendido()) {
            throw new IllegalStateException(
                    "Não é possível cancelar uma solicitação já atendida");
        }
        this.flgCanc = "S";
        this.codSituacao = SituationCreditRequest.CANCELAMENTO_SOLICITADO.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Solicita o bloqueio da solicitação de crédito.
     *
     * <p>
     * Regra de negócio: não é permitido bloquear uma solicitação cancelada.
     * </p>
     *
     * @param idUsuario identificador do usuário que solicitou o bloqueio
     */
    public void bloquear(Long idUsuario) {
        if (isCancelado()) {
            throw new IllegalStateException(
                    "Não é possível bloquear uma solicitação cancelada");
        }
        this.flgBloq = "S";
        this.codSituacao = SituationCreditRequest.BLOQUEIO_SOLICITADO.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Solicita o desbloqueio da solicitação de crédito.
     *
     * @param idUsuario identificador do usuário que solicitou o desbloqueio
     */
    public void desbloquear(Long idUsuario) {
        this.flgBloq = "N";
        this.codSituacao = SituationCreditRequest.DESBLOQUEIO_SOLICITADO.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Gerenciamento de itens
    // -------------------------------------------------------------------------

    /**
     * Adiciona um item à solicitação de crédito.
     *
     * <p>
     * Regra de negócio: não é permitido adicionar itens a uma solicitação cancelada
     * ou bloqueada.
     * </p>
     *
     * @param item item a ser adicionado
     */
    public void addItem(CreditRequestItems item) {
        if (isCancelado()) {
            throw new IllegalStateException(
                    "Não é possível adicionar itens a uma solicitação cancelada");
        }
        if (isBloqueado()) {
            throw new IllegalStateException(
                    "Não é possível adicionar itens a uma solicitação bloqueada");
        }
        this.itens.add(item);
    }
}

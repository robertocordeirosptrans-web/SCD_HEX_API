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
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SalesChannel {

    private final String codCanal;

    @Setter private String codDocumento;

    @Setter private String codCanalSuperior;

    @Setter private String desCanal;

    @Setter private String codTipoDocumento;

    @Setter private LocalDateTime dtManutencao;

    @Setter private String desRazaoSocial;

    @Setter private ChannelDomainStatus stCanais;

    @Setter private String desNomeFantasia;

    private final LocalDateTime dtCadastro;

    @Setter private BigDecimal vlCaucao;

    @Setter private LocalDate dtInicioCaucao;

    @Setter private LocalDate dtFimCaucao;

    @Setter private Integer seqNivel;

    @Setter private String flgCriticaNumlote;

    @Setter private Integer flgLimiteDias;

    @Setter private String flgProcessamentoAutomatico;

    @Setter private String flgProcessamentoParcial;

    @Setter private String flgSaldoDevedor;

    @Setter private Integer numMinutoIniLibRecarga;

    @Setter private Integer numMinutoFimLibRecarga;

    @Setter private String flgEmiteReciboPedido;

    @Setter private String flgSupercanal;

    @Setter private String flgPagtoFuturo;

    @Setter private ClassificationPerson codClassificacaoPessoa;

    @Setter private TypesActivity codAtividade;

    private final User idUsuarioCadastro;

    @Setter private User idUsuarioManutencao;
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
        return ChannelDomainStatus.ACTIVE.equals(stCanais);
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    /**
     * Ativa o canal de vendas.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.stCanais = ChannelDomainStatus.ACTIVE;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o canal de vendas.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.stCanais = ChannelDomainStatus.INACTIVE;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Atualiza os dados descritivos e operacionais do canal.
     *
     * @param desCanal                    nova descrição do canal
     * @param desRazaoSocial              nova razão social
     * @param desNomeFantasia             novo nome fantasia
     * @param codDocumento                novo código de documento
     * @param codTipoDocumento            novo tipo de documento
     * @param codCanalSuperior            novo canal superior
     * @param flgProcessamentoAutomatico  flag de processamento automático
     * @param flgProcessamentoParcial     flag de processamento parcial
     * @param operador                    usuário responsável pela operação
     */
    public void updateInfo(
            String desCanal,
            String desRazaoSocial,
            String desNomeFantasia,
            String codDocumento,
            String codTipoDocumento,
            String codCanalSuperior,
            String flgProcessamentoAutomatico,
            String flgProcessamentoParcial,
            User operador) {
        this.desCanal = desCanal;
        this.desRazaoSocial = desRazaoSocial;
        this.desNomeFantasia = desNomeFantasia;
        this.codDocumento = codDocumento;
        this.codTipoDocumento = codTipoDocumento;
        this.codCanalSuperior = codCanalSuperior;
        this.flgProcessamentoAutomatico = flgProcessamentoAutomatico;
        this.flgProcessamentoParcial = flgProcessamentoParcial;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }
}

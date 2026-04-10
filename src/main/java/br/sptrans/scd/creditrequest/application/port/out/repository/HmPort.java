package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Porta de saída (output port) para integração com o sistema HM (Hardware
 * Manager) via DBLink.
 *
 * <p>
 * Encapsula as interações com as tabelas {@code LNK_HM_TB_AUTORIZACAORECARGA} e
 * {@code LNK_HM_TB_REDE} mantidas via DBLink Oracle, conforme a fase 4 do plano
 * de migração.</p>
 */
public interface HmPort {

    /**
     * Verifica se um item foi confirmado pelo HM (NI_STATUSHM = 3 e
     * NI_STATUSSCD = 15).
     *
     * @param numSolicitacao número da solicitação
     * @param codCanal código do canal
     * @param numLogicoCartao número lógico do cartão
     * @return {@code true} se o item foi confirmado pelo HM
     */
    boolean itemConfirmadoPeloHm(Long numSolicitacao, String codCanal, String numLogicoCartao);

    /**
     * Registra a autorização de recarga no HM criando o registro em
     * {@code LNK_HM_TB_REDE} se ainda não existir.
     *
     * @param numSolicitacao número da solicitação
     * @param codCanal código do canal
     * @param codCanalDistribuicao código do canal de distribuição
     */
    void registrarAutorizacaoRecarga(Long numSolicitacao, String codCanal, String codCanalDistribuicao);

    /**
     * Envia a autorização de recarga ao HM inserindo o registro em
     * {@code LNK_HM_TB_AUTORIZACAORECARGA} com {@code NI_STATUSHM = 0} e
     * {@code NI_STATUSSCD = 6}.
     *
     * @param numSolicitacao número da solicitação
     * @param numSolicitacaoItem número do item da solicitação
     * @param codCanal código do canal
     * @param numLogicoCartao número lógico do cartão
     * @param codAssinaturaHsm código de assinatura HSM do item
     * @param dtPagtoEconomica data/hora do pagamento econômico
     * @param seqRecarga sequencial de recarga (SEQ_RECARGA) — usado como
     * idAutorizacao
     * @param valor valor do evento financeiro
     * @param liminar indica se há liminar ativa para o item
     */
    void enviarAutorizacaoRecarga(Long numSolicitacao, Long numSolicitacaoItem, String codCanal,
            String numLogicoCartao, String codAssinaturaHsm,
            LocalDateTime dtPagtoEconomica, Integer seqRecarga,
            BigDecimal valor, int liminar);

}

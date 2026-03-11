package br.sptrans.scd.creditrequest.application.service;

import java.util.Set;

import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;

/**
 * Serviço de domínio responsável por validar se uma transição de situação é
 * permitida para itens e solicitações de pedido de crédito.
 *
 * <p>
 * Encapsula as regras hardcoded derivadas da package Oracle
 * {@code PCK_MVE_SITUACAOPEDIDO}, equivalente à verificação da tabela
 * {@code TRANS_SITUACOES} e às validações inline nas rotinas de alteração.</p>
 */
public class TransitionSituationValidator {

    private static final Set<String> ALLOWED_BLOQUEAR = Set.of(
            SituationCreditRequestItems.CRIADO.getCode(),
            SituationCreditRequestItems.CONSISTIDO_OK.getCode(),
            SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode(),
            SituationCreditRequestItems.PAGO.getCode(),
            SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode(),
            SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode()
    );

    private static final Set<String> ALLOWED_CANCELAR = Set.of(
            SituationCreditRequestItems.CRIADO.getCode(),
            SituationCreditRequestItems.CONSISTIDO_OK.getCode(),
            SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode(),
            SituationCreditRequestItems.PAGO.getCode(),
            SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode()
    );

    /**
     * Valida se a ação é permitida para a situação atual do
     * <strong>item</strong>.
     *
     * @param acao ação a ser executada
     * @param status código da situação atual do item
     * @throws IllegalArgumentException se a transição não for permitida
     */
    public void validarTransicaoItem(ActionStatus acao, String status) {
        validar(acao, status, true);
    }

    /**
     * Valida se a ação é permitida para a situação atual da
     * <strong>solicitação</strong>.
     *
     * @param acao ação a ser executada
     * @param status código da situação atual da solicitação
     * @throws IllegalArgumentException se a transição não for permitida
     */
    public void validarTransicaoSolicitacao(ActionStatus acao, String status) {
        validar(acao, status, false);
    }

    private void validar(ActionStatus acao, String status, boolean isItem) {
        // Situações terminais — nenhuma ação é permitida
        boolean terminal
                = SituationCreditRequestItems.RECARREGADO.getCode().equals(status)
                || (isItem && SituationCreditRequestItems.CANCELADO.getCode().equals(status))
                || (!isItem && SituationCreditRequest.CANCELADO.getCode().equals(status))
                || SituationCreditRequestItems.REJEITADO.getCode().equals(status)
                || (!isItem && SituationCreditRequest.ATENDIDO_TOTALMENTE.getCode().equals(status));

        if (terminal) {
            throw new IllegalArgumentException(
                    "Alteração de Status não permitida para estes itens/solicitação - Status final alcançado");
        }

        switch (acao) {
            case BLOQUEAR -> {
                if (!ALLOWED_BLOQUEAR.contains(status)) {
                    throw new IllegalArgumentException(
                            "Alteração de Status não permitida para estes itens/solicitação");
                }
            }
            case DESBLOQUEAR -> {
                if (!SituationCreditRequestItems.BLOQUEADO.getCode().equals(status)) {
                    throw new IllegalArgumentException(
                            "Alteração de Status não permitida para estes itens/solicitação");
                }
            }
            case CANCELAR -> {
                if (!ALLOWED_CANCELAR.contains(status)) {
                    throw new IllegalArgumentException(
                            "Alteração de Status não permitida para estes itens/solicitação");
                }
            }
            case PAGO -> {
                if (!SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode().equals(status)) {
                    throw new IllegalArgumentException(
                            "Alteração de Status não permitida para estes itens/solicitação - PAGO requer status 03");
                }
            }
            case ACEITO_PENDENTE_LIQUIDACAO -> {
                if (!SituationCreditRequestItems.CRIADO.getCode().equals(status)
                        && !SituationCreditRequestItems.CONSISTIDO_OK.getCode().equals(status)) {
                    throw new IllegalArgumentException(
                            "Alteração de Status não permitida para estes itens/solicitação - ACEITO_PENDENTE_LIQUIDACAO requer status 01 ou 02");
                }
            }
            case LIBERAR_RECARGA -> {
                if (!SituationCreditRequestItems.PAGO.getCode().equals(status)) {
                    throw new IllegalArgumentException(
                            "Alteração de Status não permitida para estes itens/solicitação - LIBERAR_RECARGA requer status 04");
                }
            }
            default ->
                throw new IllegalArgumentException("Ação desconhecida: " + acao);
        }
    }
}

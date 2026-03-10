package br.sptrans.scd.creditrequest.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SituationCreditRequest {
    CRIADO("01", "Criado"),
    CONSISTIDO_OK("02", "Consistido OK"),
    ACEITO_PENDENTE_LIQUIDACAO("03", "Aceito - Pendente de Liquidação"),
    PAGO("04", "Pago"),
    LIBERADO_PARA_RECARGA("05", "Liberado para Recarga"),
    EM_PROCESSO_DE_RECARGA("06", "Em Processo de Recarga"),
    ATENDIDO_PARCIALMENTE("07", "Atendido Parcialmente"),
    ATENDIDO_TOTALMENTE("10", "Atendido Totalmente"),
    REJEITADO("11", "Rejeitado"),
    CANCELAMENTO_SOLICITADO("12", "Cancelamento Solicitado"),
    CANCELADO("13", "Cancelado"),
    BLOQUEIO_SOLICITADO("14", "Bloqueio Solicitado"),
    BLOQUEADO("15", "Bloqueado"),
    DESBLOQUEIO_SOLICITADO("16", "Desbloqueio Solicitado");

    private final String code;
    private final String description;

    public static SituationCreditRequest fromCode(String codigo) {
        for (SituationCreditRequest situacao : values()) {
            if (situacao.code.equals(codigo)) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Código de situação de solicitação inválido: " + codigo);
    }
}

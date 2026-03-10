package br.sptrans.scd.creditrequest.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SituationCreditRequestItems {
    CRIADO("01", "Criado"),
    CONSISTIDO_OK("02", "Consistido OK"),
    ACEITO_PENDENTE_LIQUIDACAO("03", "Aceito - Pendente de Liquidação"),
    PAGO("04", "Pago"),
    LIBERADO_PARA_RECARGA("05", "Liberado para Recarga"),
    EM_PROCESSO_DE_RECARGA("06", "Em Processo de Recarga"),
    RECARREGADO("07", "Recarregado"),
    REJEITADO("11", "Rejeitado"),
    CANCELAMENTO_SOLICITADO("12", "Cancelamento Solicitado"),
    CANCELADO("13", "Cancelado"),
    BLOQUEIO_SOLICITADO("14", "Bloqueio Solicitado"),
    BLOQUEADO("15", "Bloqueado"),
    DESBLOQUEIO_SOLICITADO("16", "Desbloqueio Solicitado"),
    ASSINATURA_INVALIDA("90", "Assinatura Inválida"),
    EM_ASSINATURA("91", "Em Assinatura");

    private final String code;
    private final String description;

    public static SituationCreditRequestItems fromCode(String codigo) {
        for (SituationCreditRequestItems situacao : values()) {
            if (situacao.code.equals(codigo)) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Código de situação de solicitação inválido: " + codigo);
    }
}

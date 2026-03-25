package br.sptrans.scd.creditrequest.application.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;

/**
 * Serviço de domínio puro que apura a situação consolidada de um pedido com
 * base nas situações de seus itens.
 *
 * <p>
 * Equivalente à função {@code SituacaoApuradaPedido} da package Oracle
 * {@code PCK_MVE_SITUACAOPEDIDO}, implementando todas as regras de negócio de
 * consolidação de status definidas no legado.</p>
 */

@Component
public class SituationAscertainedService {

    /**
     * Apura a situação consolidada do pedido a partir dos códigos de situação
     * de seus itens.
     *
     * @param codigosSituacaoItens lista de códigos de situação dos itens do
     * pedido
     * @return código da situação apurada do pedido, ou {@code null} se nenhuma
     * regra se aplicar
     */

    public String apurarSituacaoPedido(List<String> codigosSituacaoItens) {
        if (codigosSituacaoItens == null || codigosSituacaoItens.isEmpty()) {
            return null;
        }

        Map<String, Long> contadores = codigosSituacaoItens.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        long total = codigosSituacaoItens.size();

        long criados = contadores.getOrDefault(SituationCreditRequestItems.CRIADO.getCode(), 0L);
        long consistidosOk = contadores.getOrDefault(SituationCreditRequestItems.CONSISTIDO_OK.getCode(), 0L);
        long aceitosPendLiq = contadores.getOrDefault(SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode(), 0L);
        long pagos = contadores.getOrDefault(SituationCreditRequestItems.PAGO.getCode(), 0L);
        long liberados = contadores.getOrDefault(SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode(), 0L);
        long emProcesso = contadores.getOrDefault(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode(), 0L);
        long recarregados = contadores.getOrDefault(SituationCreditRequestItems.RECARREGADO.getCode(), 0L);
        long cancelados = contadores.getOrDefault(SituationCreditRequestItems.CANCELADO.getCode(), 0L);
        long bloqueados = contadores.getOrDefault(SituationCreditRequestItems.BLOQUEADO.getCode(), 0L);

        // Regras de situação única (todos os itens na mesma situação)
        if (criados == total) {
            return SituationCreditRequest.CRIADO.getCode();
        }
        if (consistidosOk == total) {
            return SituationCreditRequest.CONSISTIDO_OK.getCode();
        }
        if (aceitosPendLiq == total) {
            return SituationCreditRequest.ACEITO_PENDENTE_LIQUIDACAO.getCode();
        }
        if (pagos == total) {
            return SituationCreditRequest.PAGO.getCode();
        }
        if (liberados == total) {
            return SituationCreditRequest.LIBERADO_PARA_RECARGA.getCode();
        }
        if (emProcesso == total) {
            return SituationCreditRequest.EM_PROCESSO_DE_RECARGA.getCode();
        }
        if (recarregados == total) {
            return SituationCreditRequest.ATENDIDO_TOTALMENTE.getCode();
        }
        if (cancelados == total) {
            return SituationCreditRequest.CANCELADO.getCode();
        }
        if (bloqueados == total) {
            return SituationCreditRequest.BLOQUEADO.getCode();
        }

        // Cancelado + Bloqueado = total e há pelo menos um bloqueado → BLOQUEADO
        if ((cancelados + bloqueados) == total && bloqueados > 0) {
            return SituationCreditRequest.BLOQUEADO.getCode();
        }

        // Situação dominante + cancelados/bloqueados
        if (criados > 0 && (criados + cancelados + bloqueados) == total) {
            return SituationCreditRequest.CRIADO.getCode();
        }

        if (consistidosOk > 0 && (consistidosOk + cancelados + bloqueados) == total) {
            return SituationCreditRequest.CONSISTIDO_OK.getCode();
        }

        if (aceitosPendLiq > 0 && (aceitosPendLiq + cancelados + bloqueados) == total) {
            return SituationCreditRequest.ACEITO_PENDENTE_LIQUIDACAO.getCode();
        }

        if (pagos > 0 && (pagos + cancelados + bloqueados) == total) {
            return SituationCreditRequest.PAGO.getCode();
        }

        if (liberados > 0 && (liberados + cancelados + bloqueados) == total) {
            return SituationCreditRequest.LIBERADO_PARA_RECARGA.getCode();
        }

        // Em processo com liberados/recarregados/cancelados/bloqueados → EM_PROCESSO
        if (emProcesso > 0
                && (emProcesso + liberados + recarregados + cancelados + bloqueados) == total) {
            return SituationCreditRequest.EM_PROCESSO_DE_RECARGA.getCode();
        }

        // Recarregados + cancelados = total (sem bloqueados) → ATENDIDO_PARCIALMENTE
        if (recarregados > 0 && cancelados > 0
                && (recarregados + cancelados) == total) {
            return SituationCreditRequest.ATENDIDO_PARCIALMENTE.getCode();
        }

        return null;
    }
}

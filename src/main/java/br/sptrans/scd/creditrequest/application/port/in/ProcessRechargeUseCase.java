package br.sptrans.scd.creditrequest.application.port.in;

import java.util.List;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;

public interface ProcessRechargeUseCase {

    /**
     * Inicia o processamento de recarga para um pedido. Processo
     * semiautomático: opera item a item.
     *
     * Transição pedido: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06)
     * Transição itens: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06) →
     * RECARREGADO(07) Pedido final: ATENDIDO_PARCIALMENTE(07) ou
     * ATENDIDO_TOTALMENTE(10)
     */
    void processarRecarga(ProcessRechargeCommand comando);

    /**
     * Processa um item específico do pedido (granularidade fina).
     */
    void processarItemRecarga(ProcessItemCommand comando);

    record ProcessRechargeCommand(
            String codTipoDocumento,
            Long idUsuarioCadastro,
            Long sqPid
            ) {

    }

    record ProcessItemCommand(
            String codTipoDocumento,
            Long idUsuarioCadastro,
            Integer seqItem,
            java.math.BigDecimal vlCarregado,
            String codAssinaturaHsm
            ) {

    }

}

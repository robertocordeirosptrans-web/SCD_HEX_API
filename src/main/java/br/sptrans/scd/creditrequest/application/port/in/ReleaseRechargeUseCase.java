package br.sptrans.scd.creditrequest.application.port.in;

public interface ReleaseRechargeUseCase {
    
    /**
     * Libera o pedido para recarga.
     * Pode ser invocado por:
     *   - Usuário via REST (adapter/in/controller)
     *   - Rotina do sistema via Scheduler (adapter/in/scheduler)
     *
     * Transição: PAGO(04) → LIBERADO_PARA_RECARGA(05)
     */
    void liberarRecarga(ReleaseRechargeCommand comando);

    /**
     * Libera em lote — usado pela rotina automática.
     * Busca todos os pedidos PAGO e libera para recarga.
     */
    int liberarRecargaEmLote(BatchReleaseCommand comando);

    /**
     * Libera uma solicitação específica para recarga.
     * Usado pelo scheduler após validação de elegibilidade.
     *
     * Transição itens PAGO(04) → LIBERADO_PARA_RECARGA(05)
     *
     * @param numSolicitacao número da solicitação
     * @param codCanal       código do canal
     */
    void liberarRecargaPorSolicitacao(Long numSolicitacao, String codCanal);

    record ReleaseRechargeCommand(
        String codTipoDocumento,
        Long idUsuarioCadastro,
        Long idUsuarioTransicao,       // null quando é rotina do sistema
        String idOrigemTransicao        // "USUARIO" ou "ROTINA_SISTEMA"
    ) {}

    record BatchReleaseCommand(
        String idOrigemTransicao        // "ROTINA_SISTEMA"
    ) {}
}

package br.sptrans.scd.initializedcards.application.port.out;

import br.sptrans.scd.initializedcards.domain.RequestLotSCP;

public interface RequestLotSCPRepository {

    RequestLotSCP save(RequestLotSCP entity);

    RequestLotSCP findById(String codCanal, Long nrSolicitacao);

    RequestLotSCP delete(String codCanal, Long nrSolicitacao, Long numLote);

    RequestLotSCP findAll(String codCanal, Long nrSolicitacao, String codAdquirente);
}

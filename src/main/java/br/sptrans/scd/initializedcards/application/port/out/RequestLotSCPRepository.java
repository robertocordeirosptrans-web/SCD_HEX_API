package br.sptrans.scd.initializedcards.application.port.out;

import java.util.List;

import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.RequestLotSCPKey;

public interface RequestLotSCPRepository {

    RequestLotSCP save(RequestLotSCP entity);

    RequestLotSCP findById(RequestLotSCPKey id);

    List<RequestLotSCP> findAllBySolicitacao(String codCanal, Long nrSolicitacao);

    void deleteAllBySolicitacao(String codCanal, Long nrSolicitacao);
}

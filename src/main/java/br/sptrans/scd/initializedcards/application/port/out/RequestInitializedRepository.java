package br.sptrans.scd.initializedcards.application.port.out;

import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;

public interface RequestInitializedRepository {

    RequestInitializedCards save(RequestInitializedCards entity);

    RequestInitializedCards findById(String codCanal, Long nrSolicitacao);

    RequestInitializedCards findAll(String codCanal, Long nrSolicitacao, String codAdquirente);

}

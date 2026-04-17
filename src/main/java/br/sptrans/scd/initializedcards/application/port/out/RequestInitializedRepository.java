package br.sptrans.scd.initializedcards.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;

public interface RequestInitializedRepository {

    RequestInitializedCards save(RequestInitializedCards entity);

    RequestInitializedCards findById(String codCanal, Long nrSolicitacao);

    Page<RequestInitializedCards> findAll(String codCanal, String codAdquirente, Pageable pageable);

    Long nextNrSolicitacao(String codTipoCanal, String codCanal);
}

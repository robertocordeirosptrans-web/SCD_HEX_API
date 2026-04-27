package br.sptrans.scd.initializedcards.application.port.out;

import java.util.List;

import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;

public interface HistRequestInitializedRepository {

    HistRequestInitializedCards save(HistRequestInitializedCards entity);

    List<HistRequestInitializedCards> findAllHist(String codCanal, Long nrSolicitacao);

    Long nextSeqHist(String codCanal, Long nrSolicitacao);
}

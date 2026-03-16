package br.sptrans.scd.initializedcards.application.port.out;

import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;

public interface HistRequestInitializedRepository {

    HistRequestInitializedCards save(HistRequestInitializedCards entity);

    HistRequestInitializedCards findByHistId(String codCanal, Long nrSolicitacao, String seqHistSolicCartaoIni);

    HistRequestInitializedCards findAllHist(String codCanal, Long nrSolicitacao, String codAdquirente);
}

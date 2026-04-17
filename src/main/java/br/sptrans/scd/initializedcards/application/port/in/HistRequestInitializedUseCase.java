package br.sptrans.scd.initializedcards.application.port.in;

import java.util.List;

import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;

public interface HistRequestInitializedUseCase {

    List<HistRequestInitializedCards> listarHistorico(String codCanal, Long nrSolicitacao);


  
}

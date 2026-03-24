package br.sptrans.scd.initializedcards.application.port.out;

import br.sptrans.scd.initializedcards.domain.TbLotSCD;

public interface TbLotRepository {

    TbLotSCD findAll();

    TbLotSCD findById(Long numLote);
}

package br.sptrans.scd.initializedcards.application.port.out;

import java.util.List;

import br.sptrans.scd.initializedcards.domain.TbLotSCD;

public interface TbLotRepository {

    List<TbLotSCD> findAll();

    TbLotSCD findById(Long idLote);

    List<TbLotSCD> findAllByIds(List<Long> ids);

    List<TbLotSCD> findDisponiveis(String sortBy);
}

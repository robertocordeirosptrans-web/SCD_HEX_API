package br.sptrans.scd.initializedcards.application.port.out;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.initializedcards.domain.TbLotSCD;

public interface TbLotRepository {

    List<TbLotSCD> findAll();

    TbLotSCD findById(Long idLote);

    List<TbLotSCD> findAllByIds(List<Long> ids);

    Page<TbLotSCD> findDisponiveis(Long codTipoCartao, Pageable pageable);

    void updateStatusByIds(List<Long> ids, String status);
}

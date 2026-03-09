package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Modality;

public interface ModalityRepository {

    Optional<Modality> findById(String codModalidade);

    List<Modality> findAll();

    Modality save(Modality modalidade);
}

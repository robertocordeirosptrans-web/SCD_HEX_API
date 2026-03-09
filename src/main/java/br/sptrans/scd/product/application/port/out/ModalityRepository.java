package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Modality;

public interface ModalityRepository {

    Optional<Modality> findById(String codModalidade);

    boolean existsById(String codModalidade);

    List<Modality> findAll(String codStatus);

    Modality save(Modality modalidade);

    void updateStatus(String codModalidade, String codStatus, Long idUsuario);

    void deleteById(String codModalidade);
}

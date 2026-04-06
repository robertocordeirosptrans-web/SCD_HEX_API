package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Modality;

public interface ModalityRepository {

    Optional<Modality> findById(String codModalidade);

    boolean existsById(String codModalidade);

    List<Modality> findAll(String codStatus);

    Page<Modality> findAll(String codStatus, Pageable pageable);

    Modality save(Modality modalidade);

    void updateStatus(String codModalidade, String codStatus, Long idUsuario);

    void deleteById(String codModalidade);
}

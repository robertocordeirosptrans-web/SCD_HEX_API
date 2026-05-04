package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Modality;

// Adapte os métodos conforme necessário
public interface ModalityPort {
    Optional<Modality> findById(String codModalidade);

    boolean existsById(String codModalidade);

    List<Modality> findAll(String codStatus);

    Page<Modality> findAll(String codStatus, Pageable pageable);

    Modality save(Modality modalidade);

    void updateStatus(String codModalidade, String codStatus, Long idUsuario);

    void deleteById(String codModalidade);

    /**
     * Encontra o máximo código modalidade numérico para auto-incremento.
     * Retorna 0 se nenhum código numérico existir.
     */
    Long findMaxNumericCode();
}

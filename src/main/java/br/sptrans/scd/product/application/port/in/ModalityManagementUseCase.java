package br.sptrans.scd.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Modality;

/**
 * Porta de entrada para gestão de Modalidades de produto.
 */
public interface ModalityManagementUseCase {

    Modality createModality(CreateModalityCommand command);

    Modality updateModality(String codModalidade, UpdateModalityCommand command);

    Modality findByModality(String codModalidade);

    Page<Modality> findAllModalities(String codStatus, Pageable pageable);

    void activateModality(String codModalidade, Long idUsuario);

    void inactivateModality(String codModalidade, Long idUsuario);

    void deleteModality(String codModalidade);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateModalityCommand(
            String codModalidade,
            String desModalidade,
            Long idUsuario) {}

    record UpdateModalityCommand(
            String desModalidade,
            Long idUsuario) {}
}

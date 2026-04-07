package br.sptrans.scd.product.application.port.in;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Modality;

public interface ModalityManagementUseCase {
    Modality create(CreateModalityCommand command);
    Modality update(String codModalidade, UpdateModalityCommand command);
    void activate(String codModalidade, Long idUsuario);
    void inactivate(String codModalidade, Long idUsuario);
    void delete(String codModalidade);
    Optional<Modality> findById(String codModalidade);
    Page<Modality> findAll(String codStatus, Pageable pageable);

    // ── Commands ──────────────────────────────────────────────────────────────
    record CreateModalityCommand(
            String codModalidade,
            String desModalidade,
            Long idUsuario) {}

    record UpdateModalityCommand(
            String desModalidade,
            Long idUsuario) {}
}

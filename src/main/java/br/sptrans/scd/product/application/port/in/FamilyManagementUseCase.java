package br.sptrans.scd.product.application.port.in;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Family;

public interface FamilyManagementUseCase {
    Family create(CreateFamilyCommand command);
    Family update(String codFamilia, UpdateFamilyCommand command);
    void activate(String codFamilia, Long idUsuario);
    void inactivate(String codFamilia, Long idUsuario);
    void delete(String codFamilia);
    Optional<Family> findById(String codFamilia);
    Page<Family> findAll(String codStatus, Pageable pageable);

    // ── Commands ──────────────────────────────────────────────────────────────
    record CreateFamilyCommand(
            String codFamilia,
            String desFamilia,
            Long idUsuario) {}

    record UpdateFamilyCommand(
            String desFamilia,
            Long idUsuario) {}
}

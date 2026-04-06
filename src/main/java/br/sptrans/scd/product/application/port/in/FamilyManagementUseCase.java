package br.sptrans.scd.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Family;

/**
 * Porta de entrada para gestão de Famílias de produto.
 */
public interface FamilyManagementUseCase {

    Family createFamily(CreateFamilyCommand command);

    Family updateFamily(String codFamilia, UpdateFamilyCommand command);

    Family findByFamily(String codFamilia);

    Page<Family> findAllFamilies(String codStatus, Pageable pageable);

    void activateFamily(String codFamilia, Long idUsuario);

    void inactivateFamily(String codFamilia, Long idUsuario);

    void deleteFamily(String codFamilia);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateFamilyCommand(
            String codFamilia,
            String desFamilia,
            Long idUsuario) {}

    record UpdateFamilyCommand(
            String desFamilia,
            Long idUsuario) {}
}

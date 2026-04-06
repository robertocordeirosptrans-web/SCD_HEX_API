package br.sptrans.scd.channel.application.port.in;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.AgreementValidity;

public interface AgreementValidityUseCase {

    AgreementValidity createAgreementValidity(CreateAgreementValidityCommand command);

    AgreementValidity updateAgreementValidity(String codCanal, String codProduto, UpdateAgreementValidityCommand command);

    AgreementValidity findAgreementValidity(String codCanal, String codProduto);

    Page<AgreementValidity> findAllAgreementValidities(Pageable pageable);

    Page<AgreementValidity> findByCodCanal(String codCanal, Pageable pageable);

    Page<AgreementValidity> findByCodProduto(String codProduto, Pageable pageable);

    void deleteAgreementValidity(String codCanal, String codProduto);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateAgreementValidityCommand(
            String codCanal,
            String codProduto,
            LocalDateTime dtFimValidade,
            LocalDateTime dtInicioValidade,
            String codStatus,
            User usuario) {}

    record UpdateAgreementValidityCommand(
            LocalDateTime dtFimValidade,
            String codStatus,
            User usuario) {}
}

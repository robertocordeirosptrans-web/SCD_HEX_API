package br.sptrans.scd.channel.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import br.sptrans.scd.channel.domain.AgreementValidity;

public interface AgreementValidityUseCase {

    AgreementValidity createAgreementValidity(CreateAgreementValidityCommand command);

    AgreementValidity updateAgreementValidity(String codCanal, String codProduto, UpdateAgreementValidityCommand command);

    AgreementValidity findAgreementValidity(String codCanal, String codProduto);

    List<AgreementValidity> findAllAgreementValidities();

    List<AgreementValidity> findByCodCanal(String codCanal);

    List<AgreementValidity> findByCodProduto(String codProduto);

    void deleteAgreementValidity(String codCanal, String codProduto);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateAgreementValidityCommand(
            String codCanal,
            String codProduto,
            LocalDateTime dataFimValidade,
            LocalDateTime dataInicioValidade,
            String status,
            Long idUsuario) {}

    record UpdateAgreementValidityCommand(
            LocalDateTime dataFimValidade,
            LocalDateTime dataInicioValidade,
            String status,
            Long idUsuario) {}
}

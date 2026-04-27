package br.sptrans.scd.channel.adapter.in.rest;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.AgreementValidityResponseDTO;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.AgreementValidityMapper;
import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase;
import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase.CreateAgreementValidityCommand;
import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase.UpdateAgreementValidityCommand;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CadPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/agreement-validities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vigências de Convênio v1", description = "Endpoints para gerenciamento de vigências de convênio")
public class AgreementValidityController {

    private static final Logger log = LoggerFactory.getLogger(AgreementValidityController.class);

    private final AgreementValidityUseCase agreementValidityUseCase;
    private final UserResolverHelper userResolverHelper;
    private final AgreementValidityMapper agreementValidityMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.VIG_CADVIG + "')")
    @Operation(summary = "Cadastra uma nova vigência de convênio")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vigência de convênio cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<AgreementValidityResponseDTO> createAgreementValidity(
            @RequestBody CreateAgreementValidityRequest request) {
        log.info("REST POST /agreement-validities — Canal: {}, Produto: {}", request.codCanal(), request.codProduto());
        User usuario = userResolverHelper.getCurrentUser();
        AgreementValidity result = agreementValidityUseCase.createAgreementValidity(
                new CreateAgreementValidityCommand(
                        request.codCanal(),
                        request.codProduto(),
                        request.dtFimValidade(),
                        request.dtInicioValidade(),
                        request.codStatus(),
                        usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(agreementValidityMapper.toResponseDTO(result));
    }

    @PutMapping("/{codCanal}/{codProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.VIG_ATUVIG + "')")
    @Operation(summary = "Atualiza uma vigência de convênio")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vigência de convênio atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<AgreementValidityResponseDTO> updateAgreementValidity(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            @RequestBody UpdateAgreementValidityRequest request) {
        log.info("REST PUT /agreement-validities/{}/{} — Atualizando", codCanal, codProduto);
        User usuario = userResolverHelper.getCurrentUser();
        AgreementValidity result = agreementValidityUseCase.updateAgreementValidity(codCanal, codProduto,
                new UpdateAgreementValidityCommand(
                        request.dtFimValidade(),
                        request.codStatus(),
                        usuario));
        return ResponseEntity.ok(agreementValidityMapper.toResponseDTO(result));
    }

    @GetMapping("/{codCanal}/{codProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.VIG_BUSVIGPORCOD + "')")
    @Operation(summary = "Busca vigência de convênio por canal e produto")
    public ResponseEntity<AgreementValidityResponseDTO> findAgreementValidity(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        return ResponseEntity.ok(agreementValidityMapper.toResponseDTO(agreementValidityUseCase.findAgreementValidity(codCanal, codProduto)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.VIG_LISVIG + "')")
    @Operation(summary = "Lista vigências de convênio com filtro opcional por canal ou produto")
    public ResponseEntity<PageResponse<AgreementValidityResponseDTO>> findAgreementValidities(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) String codProduto,
            Pageable pageable) {
        Page<AgreementValidity> page;
        if (codCanal != null) {
            page = agreementValidityUseCase.findByCodCanal(codCanal, pageable);
        } else if (codProduto != null) {
            page = agreementValidityUseCase.findByCodProduto(codProduto, pageable);
        } else {
            page = agreementValidityUseCase.findAllAgreementValidities(pageable);
        }
        return ResponseEntity.ok(PageResponse.fromPage(page.map(agreementValidityMapper::toResponseDTO)));
    }

    @DeleteMapping("/{codCanal}/{codProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.VIG_REMVIG + "')")
    @Operation(summary = "Remove uma vigência de convênio")
    public ResponseEntity<Void> deleteAgreementValidity(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        log.info("REST DELETE /agreement-validities/{}/{}", codCanal, codProduto);
        agreementValidityUseCase.deleteAgreementValidity(codCanal, codProduto);
        return ResponseEntity.noContent().build();
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────

    public record CreateAgreementValidityRequest(
            String codCanal,
            String codProduto,
            LocalDateTime dtFimValidade,
            LocalDateTime dtInicioValidade,
            String codStatus) {}

    public record UpdateAgreementValidityRequest(
            LocalDateTime dtFimValidade,
            String codStatus) {}
}

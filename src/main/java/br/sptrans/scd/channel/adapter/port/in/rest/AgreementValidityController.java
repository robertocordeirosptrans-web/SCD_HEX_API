package br.sptrans.scd.channel.adapter.port.in.rest;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase;
import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase.CreateAgreementValidityCommand;
import br.sptrans.scd.channel.application.port.in.AgreementValidityUseCase.UpdateAgreementValidityCommand;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.shared.dto.PageResponse;
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
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vigências de Convênio v1", description = "Endpoints para gerenciamento de vigências de convênio")
public class AgreementValidityController {

    private final AgreementValidityUseCase agreementValidityUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra uma nova vigência de convênio")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vigência de convênio cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<AgreementValidity> createAgreementValidity(
            @RequestBody CreateAgreementValidityRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        AgreementValidity result = agreementValidityUseCase.createAgreementValidity(
                new CreateAgreementValidityCommand(
                        request.codCanal(),
                        request.codProduto(),
                        request.dtFimValidade(),
                        request.dtInicioValidade(),
                        request.codStatus(),
                        idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Atualiza uma vigência de convênio")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vigência de convênio atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<AgreementValidity> updateAgreementValidity(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            @RequestBody UpdateAgreementValidityRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        AgreementValidity result = agreementValidityUseCase.updateAgreementValidity(codCanal, codProduto,
                new UpdateAgreementValidityCommand(
                        request.dtFimValidade(),
                        request.dtInicioValidade(),
                        request.codStatus(),
                        idUsuario));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Busca vigência de convênio por canal e produto")
    public ResponseEntity<AgreementValidity> findAgreementValidity(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        return ResponseEntity.ok(agreementValidityUseCase.findAgreementValidity(codCanal, codProduto));
    }

    @GetMapping
    @Operation(summary = "Lista vigências de convênio com filtro opcional por canal ou produto")
    public ResponseEntity<PageResponse<AgreementValidity>> findAgreementValidities(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) String codProduto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AgreementValidity> all;
        if (codCanal != null) {
            all = agreementValidityUseCase.findByCodCanal(codCanal);
        } else if (codProduto != null) {
            all = agreementValidityUseCase.findByCodProduto(codProduto);
        } else {
            all = agreementValidityUseCase.findAllAgreementValidities();
        }
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @DeleteMapping("/{codCanal}/{codProduto}")
    @Operation(summary = "Remove uma vigência de convênio")
    public ResponseEntity<Void> deleteAgreementValidity(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        agreementValidityUseCase.deleteAgreementValidity(codCanal, codProduto);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
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
            LocalDateTime dtInicioValidade,
            String codStatus) {}
}

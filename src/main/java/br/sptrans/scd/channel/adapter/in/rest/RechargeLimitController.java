package br.sptrans.scd.channel.adapter.in.rest;

import java.math.BigDecimal;
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
import br.sptrans.scd.channel.adapter.in.rest.dto.RechargeLimitResponseDTO;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.RechargeLimitMapper;
import br.sptrans.scd.channel.application.port.in.RechargeLimitUseCase;
import br.sptrans.scd.channel.application.port.in.RechargeLimitUseCase.CreateRechargeLimitCommand;
import br.sptrans.scd.channel.application.port.in.RechargeLimitUseCase.UpdateRechargeLimitCommand;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/recharge-limits")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Limites de Recarga v1", description = "Endpoints para gerenciamento de limites de recarga")

public class RechargeLimitController {

    private static final Logger log = LoggerFactory.getLogger(RechargeLimitController.class);

    private final RechargeLimitUseCase rechargeLimitUseCase;
    private final UserResolverHelper userResolverHelper;
    private final RechargeLimitMapper rechargeLimitMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.REC_CADLIMDEREC + "')")
    @Operation(summary = "Cadastra um novo limite de recarga")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limite de recarga cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })

    public ResponseEntity<RechargeLimitResponseDTO> createRechargeLimit(
            @RequestBody CreateRechargeLimitRequest request) {
        log.info("REST POST /recharge-limits — Canal: {}, Produto: {}", request.codCanal(), request.codProduto());
        User usuario = userResolverHelper.getCurrentUser();
        RechargeLimit result = rechargeLimitUseCase.createRechargeLimit(
                new CreateRechargeLimitCommand(
                        request.codCanal(),
                        request.codProduto(),
                        request.dtInicioValidade(),
                        request.dtFimValidade(),
                        request.vlMinimoRecarga(),
                        request.vlMaximoRecarga(),
                        request.vlMaximoSaldo(),
                        request.codStatus(),
                        usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(rechargeLimitMapper.toResponseDTO(result));
    }

    @PutMapping("/{codCanal}/{codProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.REC_ATULIMDEREC + "')")
    @Operation(summary = "Atualiza um limite de recarga")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limite de recarga atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<RechargeLimitResponseDTO> updateRechargeLimit(
            @PathVariable String codCanal,
            @PathVariable String codProduto,
            @RequestBody UpdateRechargeLimitRequest request) {
        log.info("REST PUT /recharge-limits/{}/{} — Atualizando", codCanal, codProduto);
        Long idUsuario = userResolverHelper.getCurrentUserId();
        RechargeLimit result = rechargeLimitUseCase.updateRechargeLimit(codCanal, codProduto,
                new UpdateRechargeLimitCommand(
                        request.dtInicioValidade(),
                        request.dtFimValidade(),
                        request.vlMinimoRecarga(),
                        request.vlMaximoRecarga(),
                        request.vlMaximoSaldo(),
                        request.codStatus(),
                        idUsuario));
        return ResponseEntity.ok(rechargeLimitMapper.toResponseDTO(result));
    }

    @GetMapping("/{codCanal}/{codProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.REC_BUSLIMDERECPORCOD + "')")
    @Operation(summary = "Busca limite de recarga por canal e produto")
    public ResponseEntity<RechargeLimitResponseDTO> findRechargeLimit(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);

        return ResponseEntity.ok(rechargeLimitMapper.toResponseDTO(rechargeLimitUseCase.findRechargeLimit(key)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.REC_LISLIMDEREC + "')")
    @Operation(summary = "Lista limites de recarga com filtro opcional por canal ou produto")
    public ResponseEntity<PageResponse<RechargeLimitResponseDTO>> findRechargeLimits(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) String codProduto,
            Pageable pageable) {
        Page<RechargeLimit> page = rechargeLimitUseCase.findAllRechargeLimits(pageable);
        return ResponseEntity.ok(PageResponse.fromPage(page.map(rechargeLimitMapper::toResponseDTO)));
    }

    @DeleteMapping("/{codCanal}/{codProduto}")
    @PreAuthorize("hasAuthority('" + CadPermissions.REC_REMLIMDEREC + "')")
    @Operation(summary = "Remove um limite de recarga")
    public ResponseEntity<Void> deleteRechargeLimit(
            @PathVariable String codCanal,
            @PathVariable String codProduto) {
        log.info("REST DELETE /recharge-limits/{}/{}", codCanal, codProduto);
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        rechargeLimitUseCase.deleteRechargeLimit(key);
        return ResponseEntity.noContent().build();
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────

    public record CreateRechargeLimitRequest(
            String codCanal,
            String codProduto,
            LocalDateTime dtInicioValidade,
            LocalDateTime dtFimValidade,
            BigDecimal vlMinimoRecarga,
            BigDecimal vlMaximoRecarga,
            BigDecimal vlMaximoSaldo,
            String codStatus) {}

    public record UpdateRechargeLimitRequest(
            LocalDateTime dtInicioValidade,
            LocalDateTime dtFimValidade,
            BigDecimal vlMinimoRecarga,
            BigDecimal vlMaximoRecarga,
            BigDecimal vlMaximoSaldo,
            String codStatus) {}
}

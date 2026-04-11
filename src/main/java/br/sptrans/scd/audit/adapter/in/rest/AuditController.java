package br.sptrans.scd.audit.adapter.in.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.audit.application.port.out.AuditLogRepository;
import br.sptrans.scd.audit.domain.AuditLog;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de consulta de auditoria.
 *
 * <p>Acesso restrito à permissão {@code AUDIT_VIEW}.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/audit")
@Tag(name = "Auditoria v1", description = "Consulta de registros de auditoria — requer AUDIT_VIEW")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private static final String PERM_AUDIT_VIEW = "hasAuthority('AUDIT_VIEW')";

    private final AuditLogRepository auditLogRepository;

    @GetMapping("/user/{userId}")
    @PreAuthorize(PERM_AUDIT_VIEW)
    @Operation(summary = "Histórico de auditoria por usuário",
               description = "Retorna todos os eventos auditados para o userId informado, ordem decrescente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissão AUDIT_VIEW")
    })
    public ResponseEntity<List<AuditLog>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogRepository.findByUserId(userId));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize(PERM_AUDIT_VIEW)
    @Operation(summary = "Histórico de auditoria por sessão",
               description = "Retorna todos os eventos auditados para o sessionId informado, ordem decrescente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissão AUDIT_VIEW")
    })
    public ResponseEntity<List<AuditLog>> bySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(auditLogRepository.findBySessionId(sessionId));
    }
}

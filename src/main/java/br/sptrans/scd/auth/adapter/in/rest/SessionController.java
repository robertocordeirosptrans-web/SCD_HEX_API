package br.sptrans.scd.auth.adapter.in.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.application.port.out.UserSessionRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.session.UserSession;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CacPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gerenciamento de sessões.
 *
 * <p>Todos os endpoints exigem a permissão {@code SESSION_REVOKE}.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/sessions")
@Tag(name = "Sessões v1", description = "Endpoints de gerenciamento e revogação de sessões de usuário")
public class SessionController {

    private static final String PERM_REVOKE = "hasAuthority('" + CacPermissions.REVSSESS + "')";

    private final SessionManagementUseCase sessionUseCase;
    private final UserSessionRepository sessionRepository;
    private final UserQueryPort userQueryPort;
    private final UserResolverHelper userResolverHelper;

    // ── Revogação individual ──────────────────────────────────────────────────

    @PostMapping("/{sessionId}/revoke")
    @PreAuthorize(PERM_REVOKE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Revogar sessão específica",
               description = "Revoga uma sessão pelo seu ID. Requer permissão SESSION_REVOKE.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão revogada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissão SESSION_REVOKE"),
        @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
        public ResponseEntity<RevokeResponse> revokeSession(@PathVariable String sessionId,
                                Authentication authentication) {
        // Valida que a sessão existe
        sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão", "id", sessionId));

        Long revokedBy = userResolverHelper.getCurrentUserId();
        sessionUseCase.revokeSession(sessionId, revokedBy, "ADMIN_REVOKE");

        return ResponseEntity.ok(new RevokeResponse("Sessão " + sessionId + " revogada com sucesso."));
        }

    // ── Revogação em massa ────────────────────────────────────────────────────

    @PostMapping("/users/{userId}/revoke")
    @PreAuthorize(PERM_REVOKE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Revogar todas as sessões de um usuário",
               description = "Revoga todas as sessões ativas de um usuário. Requer permissão SESSION_REVOKE.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessões revogadas com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissão SESSION_REVOKE"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<RevokeResponse> revokeAllUserSessions(@PathVariable Long userId,
                                                                Authentication authentication) {
        // Valida que o usuário existe
        userQueryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        sessionUseCase.revokeAllUserSessions(userId, "ADMIN_REVOKE");

        return ResponseEntity.ok(
                new RevokeResponse("Todas as sessões do usuário " + userId + " foram revogadas."));
    }

    // ── Consulta de sessões ativas ────────────────────────────────────────────

    @GetMapping("/users/sessions")
    @PreAuthorize(PERM_REVOKE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar sessões ativas de usuários",
               description = "Retorna sessões ativas filtrando por nome, codLogin ou email. Paginação suportada. Requer permissão SESSION_REVOKE.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessões retornadas com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissão SESSION_REVOKE")
    })

    public ResponseEntity<PageResponse<SessionSummaryResponse>> listActiveSessions(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String codLogin,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<User> users = new ArrayList<>();
        if (codLogin != null && !codLogin.isBlank()) {
            userQueryPort.findByCodLogin(codLogin).ifPresent(users::add);
        } else if (email != null && !email.isBlank()) {
            userQueryPort.findByNomEmail(email).ifPresent(users::add);
        } else if (nome != null && !nome.isBlank()) {
            users = userQueryPort.findAllPaginated(null, nome, null, null, 0, Integer.MAX_VALUE, null, null);
        }

        if (users.isEmpty()) {
            Page<SessionSummaryResponse> emptyPage = new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(page, size), 0);
            return ResponseEntity.ok(PageResponse.fromPage(emptyPage));
        }

        List<SessionSummaryResponse> allSessions = new ArrayList<>();
        for (User user : users) {
            sessionRepository.findActiveByUserId(user.getIdUsuario())
                .stream()
                .filter(UserSession::isAtiva)
                .map(s -> new SessionSummaryResponse(
                        s.getIdSessao(),
                        s.getIdUsuario(),
                        s.getEnderecoIp(),
                        s.getAgenteUsuario(),
                        s.getDtCriacao().toString(),
                        s.getDtExpiracao().toString(),
                        s.getStatus().name()))
                .forEach(allSessions::add);
        }

        int start = Math.min(page * size, allSessions.size());
        int end = Math.min(start + size, allSessions.size());
        List<SessionSummaryResponse> pagedSessions = allSessions.subList(start, end);
        Page<SessionSummaryResponse> sessionPage = new PageImpl<>(pagedSessions, org.springframework.data.domain.PageRequest.of(page, size), allSessions.size());
        return ResponseEntity.ok(PageResponse.fromPage(sessionPage));
    }


    // ── DTOs ─────────────────────────────────────────────────────────────────

    public record RevokeResponse(String mensagem) {}

    public record SessionSummaryResponse(
            String sessionId,
            Long userId,
            String ip,
            String userAgent,
            String createdAt,
            String expiresAt,
            String status) {}
}

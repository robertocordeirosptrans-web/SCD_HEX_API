package br.sptrans.scd.product.adapter.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.CreateModalityCommand;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.UpdateModalityCommand;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/modalities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Modalidades v1", description = "Endpoints para gerenciamento de modalidades de produto")
public class ModalityController {

    private final ModalityManagementUseCase modalityManagementUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra uma nova modalidade")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modalidade cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<Modality> createModality(
            @RequestBody CreateModalityRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        Modality modality = modalityManagementUseCase.createModality(
                new CreateModalityCommand(request.codModalidade(), request.desModalidade(), idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(modality);
    }

    @PutMapping("/{codModalidade}")
    @Operation(summary = "Atualiza dados de uma modalidade")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modalidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<Modality> updateModality(
            @PathVariable String codModalidade,
            @RequestBody UpdateModalityRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        Modality modality = modalityManagementUseCase.updateModality(codModalidade,
                new UpdateModalityCommand(request.desModalidade(), idUsuario));
        return ResponseEntity.ok(modality);
    }

    @GetMapping("/{codModalidade}")
    @Operation(summary = "Busca modalidade por código")
    public ResponseEntity<Modality> findByModality(@PathVariable String codModalidade) {
        return ResponseEntity.ok(modalityManagementUseCase.findByModality(codModalidade));
    }

    @GetMapping
    @Operation(summary = "Lista todas as modalidades, com filtro opcional de status")
    public ResponseEntity<PageResponse<Modality>> findAllModalities(
            @RequestParam(required = false) String codStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Modality> all = modalityManagementUseCase.findAllModalities(codStatus);
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @PatchMapping("/{codModalidade}/activate")
    @Operation(summary = "Ativa uma modalidade")
    public ResponseEntity<Void> activateModality(
            @PathVariable String codModalidade,
            Authentication authentication) {
        modalityManagementUseCase.activateModality(codModalidade, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codModalidade}/inactivate")
    @Operation(summary = "Inativa uma modalidade")
    public ResponseEntity<Void> inactivateModality(
            @PathVariable String codModalidade,
            Authentication authentication) {
        modalityManagementUseCase.inactivateModality(codModalidade, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codModalidade}")
    @Operation(summary = "Remove uma modalidade")
    public ResponseEntity<Void> deleteModality(@PathVariable String codModalidade) {
        modalityManagementUseCase.deleteModality(codModalidade);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateModalityRequest(String codModalidade, String desModalidade) {}
    public record UpdateModalityRequest(String desModalidade) {}
}

package br.sptrans.scd.auth.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/usuario-perfil")
@Tag(name = "Usuário-Perfil v1", description = "Endpoints para associação de usuário e perfil")

public class UserProfileController {

    private final GroupProfileManagementUseCase groupProfileManagementUseCase;

    public UserProfileController(GroupProfileManagementUseCase groupProfileManagementUseCase) {
        this.groupProfileManagementUseCase = groupProfileManagementUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Associar perfil ao usuário", description = "Associa um perfil ao usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> associate(@RequestBody UserProfileRequest request) {
        try {
      
            groupProfileManagementUseCase.associateProfilesToGroup(
                    new GroupProfileManagementUseCase.AssociateProfilesToGroupCommand(
                            request.idUsuario().toString(),
                            java.util.Set.of(request.codPerfil()),
                            request.idUsuario()
                    )
            );
            return ResponseEntity.ok().build();
        } catch (GroupProfileManagementUseCase.GroupProfileManagementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar associação usuário-perfil", description = "Atualiza dados da associação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@RequestBody UserProfileRequest request) {
        // Implementar lógica de atualização se necessário
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar status da associação", description = "Atualiza o status da associação usuário-perfil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateStatus(@RequestBody UserProfileStatusRequest request) {
        try {
            groupProfileManagementUseCase.disassociateProfileFromGroup(
                    new GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand(
                            request.idUsuario().toString(),
                            request.codPerfil(),
                            request.idUsuario()
                    )
            );
            return ResponseEntity.ok().build();
        } catch (GroupProfileManagementUseCase.GroupProfileManagementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record UserProfileRequest(Long idUsuario, String codPerfil) {

    }

    public record UserProfileStatusRequest(Long idUsuario, String codPerfil, String status) {

    }
}

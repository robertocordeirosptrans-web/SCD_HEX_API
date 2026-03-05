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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/grupo-usuario")
@Tag(name = "Grupo-Usuário v1", description = "Endpoints para associação de grupo e usuários")
public class GroupUserController {

    private final GroupProfileManagementUseCase groupProfileManagementUseCase;

    public GroupUserController(GroupProfileManagementUseCase groupProfileManagementUseCase) {
        this.groupProfileManagementUseCase = groupProfileManagementUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Associar usuário ao grupo", description = "Associa um usuário ao grupo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> associate(@RequestBody GroupUserRequest request) {
        try {
            groupProfileManagementUseCase.associateProfilesToGroup(
                new GroupProfileManagementUseCase.AssociateProfilesToGroupCommand(
                    request.codGrupo(),
                    java.util.Set.of(request.idUsuario().toString()), // Ajuste conforme o caso de uso real
                    request.idUsuario() // idUsuarioLogado
                )
            );
            return ResponseEntity.ok().build();
        } catch (GroupProfileManagementUseCase.GroupProfileManagementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar associação grupo-usuário", description = "Atualiza dados da associação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@RequestBody GroupUserRequest request) {
        // Implementar lógica de atualização se necessário
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar status da associação", description = "Atualiza o status da associação grupo-usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateStatus(@RequestBody GroupUserStatusRequest request) {
        try {
            groupProfileManagementUseCase.disassociateProfileFromGroup(
                new GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand(
                    request.codGrupo(),
                    request.idUsuario().toString(),
                    request.idUsuario()
                )
            );
            return ResponseEntity.ok().build();
        } catch (GroupProfileManagementUseCase.GroupProfileManagementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record GroupUserRequest(String codGrupo, Long idUsuario) {}
    public record GroupUserStatusRequest(String codGrupo, Long idUsuario, String status) {}
}


package br.sptrans.scd.auth.adapter.port.in.rest;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.shared.dto.PageResponse;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar associações grupo-usuário", description = "Retorna uma lista paginada de todas as associações grupo-usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de associações retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<GroupUser>> listGroupUsers(
            Pageable pageable) {
        Page<GroupUser> page = groupProfileManagementUseCase.listGroupUsers(pageable);
        return ResponseEntity.ok(PageResponse.fromPage(page));
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
        groupProfileManagementUseCase.associateProfilesToGroup(
            new GroupProfileManagementUseCase.AssociateProfilesToGroupCommand(
                request.codGrupo(),
                java.util.Set.of(request.idUsuario().toString()),
                request.idUsuario()
            )
        );
        return ResponseEntity.ok().build();
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
        groupProfileManagementUseCase.disassociateProfileFromGroup(
            new GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand(
                request.codGrupo(),
                request.idUsuario().toString(),
                request.idUsuario()
            )
        );
        return ResponseEntity.ok().build();
    }

    public record GroupUserRequest(String codGrupo, Long idUsuario) {}
    public record GroupUserStatusRequest(String codGrupo, Long idUsuario, String status) {}
}

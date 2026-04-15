
package br.sptrans.scd.auth.adapter.in.rest;

import br.sptrans.scd.auth.adapter.in.rest.dto.GroupUserCustomResponseDTO;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupUserCustomProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.GroupUserRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.GroupUserResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.GroupUserStatusRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.mapper.GroupUserRestMapper;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.security.CacPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/grupo-usuario")
@Tag(name = "Grupo-Usuário v1", description = "Endpoints para associação de grupo e usuários")
public class GroupUserController {

        private final GroupProfileManagementUseCase groupProfileManagementUseCase;
        private final GroupUserRestMapper groupUserRestMapper;

        public GroupUserController(GroupProfileManagementUseCase groupProfileManagementUseCase,
                        GroupUserRestMapper groupUserRestMapper) {
                this.groupProfileManagementUseCase = groupProfileManagementUseCase;
                this.groupUserRestMapper = groupUserRestMapper;
        }

        @GetMapping
        @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
        @Operation(summary = "Listar associações grupo-usuário", description = "Retorna uma lista paginada de todas as associações grupo-usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de associações retornada com sucesso")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<PageResponse<GroupUserResponseDTO>> listGroupUsers(
                        Pageable pageable) {
                Page<GroupUserResponseDTO> page = groupProfileManagementUseCase.listGroupUsers(pageable)
                                .map(groupUserRestMapper::toDto);
                return ResponseEntity.ok(PageResponse.fromPage(page));
        }

        @GetMapping("/{codGrupo}/usuarios")
        @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
        @Operation(summary = "Listar usuários do grupo", description = "Retorna uma lista de usuários vinculados ao grupo informado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<PageResponse<GroupUserCustomResponseDTO>> listUsersByGroup(@PathVariable String codGrupo,
                        Pageable pageable) {
                Page<GroupUserCustomProjection> usuarios = groupProfileManagementUseCase.listCustomUsersByGroup(codGrupo, pageable);
                if (usuarios == null || usuarios.isEmpty()) {
                        return ResponseEntity.notFound().build();
                }
                Page<GroupUserCustomResponseDTO> dtos = usuarios.map(u -> new GroupUserCustomResponseDTO(
                        u.getCodLogin(),
                        u.getNomUsuario(),
                        u.getNomDepartamento(),
                        u.getNomEmail(),
                        u.getCodStatus()
                ));
                return ResponseEntity.ok(PageResponse.fromPage(dtos));
        }

        @PostMapping
        @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
        @Operation(summary = "Associar usuário ao grupo", description = "Associa um usuário ao grupo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> associate(@Valid @RequestBody GroupUserRequestDTO request) {
                groupProfileManagementUseCase.associateProfilesToGroup(
                                new GroupProfileManagementUseCase.AssociateProfilesToGroupCommand(
                                                request.codGrupo(),
                                                java.util.Set.of(request.idUsuario().toString()),
                                                request.idUsuario()));
                return ResponseEntity.ok().build();
        }

        @PutMapping
        @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
        @Operation(summary = "Atualizar associação grupo-usuário", description = "Atualiza dados da associação")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> update(@Valid @RequestBody GroupUserRequestDTO request) {
                // Implementar lógica de atualização se necessário
                return ResponseEntity.ok().build();
        }

        @PatchMapping("/status")
        @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
        @Operation(summary = "Atualizar status da associação", description = "Atualiza o status da associação grupo-usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> updateStatus(@Valid @RequestBody GroupUserStatusRequestDTO request) {
                groupProfileManagementUseCase.disassociateProfileFromGroup(
                                new GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand(
                                                request.codGrupo(),
                                                request.idUsuario().toString(),
                                                request.idUsuario()));
                return ResponseEntity.ok().build();
        }

}

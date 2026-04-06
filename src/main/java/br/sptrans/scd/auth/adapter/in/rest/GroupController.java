package br.sptrans.scd.auth.adapter.in.rest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/grupos")
@Tag(name = "Grupos v1", description = "Endpoints para gerenciamento de grupos - Versão 1")
@RequiredArgsConstructor
public class GroupController {

    public record CreateGroupRequest(String codGrupo, String nomGrupo, Long idUsuarioLogado) {

    }

    public record UpdateGroupRequest(String nomGrupo, Long idUsuarioLogado) {

    }

    private final GroupProfileManagementUseCase groupProfileManagementUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar grupo", description = "Cria um novo grupo no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Grupo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest request) {
        GroupProfileManagementUseCase.CreateGroupCommand cmd = new GroupProfileManagementUseCase.CreateGroupCommand(request.codGrupo(), request.nomGrupo(), request.idUsuarioLogado());
        var grupo = groupProfileManagementUseCase.createGroup(cmd);
        return ResponseEntity.ok(grupo);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar grupos", description = "Retorna uma lista de todos os grupos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de grupos retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<GrupoResponseDTO>> getAllGroup(
            Pageable pageable) {
        Page<GrupoResponseDTO> dtoPage = groupProfileManagementUseCase.listGroups(null, pageable)
            .map(GrupoResponseDTO::new);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{codGrupo}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter grupo por código", description = "Retorna um grupo específico pelo código")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Grupo retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getGroupById(@PathVariable String codGrupo) {
        List<Group> grupos = groupProfileManagementUseCase.listGroups(null);
        Optional<Group> grupo = grupos.stream().filter(g -> g.getCodGrupo().equalsIgnoreCase(codGrupo)).findFirst();
        return grupo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{codGrupo}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar grupo", description = "Atualiza um grupo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Grupo atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateGroup(@PathVariable String codGrupo, @RequestBody UpdateGroupRequest request) {
        GroupProfileManagementUseCase.UpdateGroupCommand cmd = new GroupProfileManagementUseCase.UpdateGroupCommand(codGrupo, request.nomGrupo(), request.idUsuarioLogado());
        var grupo = groupProfileManagementUseCase.updateGroup(cmd);
        return ResponseEntity.ok(grupo);
    }

    public record GrupoResponseDTO(
            String codGrupo,
            Long idUsuarioManutencao,
            LocalDateTime dtModi,
            String codStatus,
            String nomGrupo
            ) {

        public GrupoResponseDTO(Group grupo) {
            this(
                    grupo.getCodGrupo(),
                    grupo.getIdUsuarioManutencao(),
                    grupo.getDtModi(),
                    grupo.getCodStatus(),
                    grupo.getNomGrupo()
            );
        }
    }

}

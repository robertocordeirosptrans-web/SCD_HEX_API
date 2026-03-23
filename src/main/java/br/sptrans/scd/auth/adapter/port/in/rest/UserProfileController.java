package br.sptrans.scd.auth.adapter.port.in.rest;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.UserProfile;
import br.sptrans.scd.shared.dto.PageResponse;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar associações usuário-perfil", description = "Retorna uma lista paginada de todas as associações usuário-perfil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de associações retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<UserProfileResponseDTO>> listUserProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserProfile> all = groupProfileManagementUseCase.listUserProfiles();
        List<UserProfileResponseDTO> userDTOs = all.stream()
                .map(UserProfileResponseDTO::new)
                .toList();
        return ResponseEntity.ok(PageResponse.fromList(userDTOs, page, size));
    }

    @GetMapping("/perfil/{codPerfil}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuários associados a um perfil", description = "Retorna os usuários associados ao perfil informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuários associados retornados com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<UserProfileResponseDTO>> getUsersByProfile(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @PathVariable String codPerfil) {
        List<UserProfile> associados = groupProfileManagementUseCase.listUserProfiles();
        List<UserProfileResponseDTO> associadosDTO = associados.stream()
                .filter(up -> up.getId() != null && codPerfil.equals(up.getId().getCodPerfil()))
                .map(UserProfileResponseDTO::new)
                .toList();
        if (associadosDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PageResponse.fromList(associadosDTO, page, size));
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
        groupProfileManagementUseCase.associateProfilesToGroup(
                new GroupProfileManagementUseCase.AssociateProfilesToGroupCommand(
                        request.idUsuario().toString(),
                        java.util.Set.of(request.codPerfil()),
                        request.idUsuario()
                )
        );
        return ResponseEntity.ok().build();
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
        groupProfileManagementUseCase.disassociateProfileFromGroup(
                new GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand(
                        request.idUsuario().toString(),
                        request.codPerfil(),
                        request.idUsuario()
                )
        );
        return ResponseEntity.ok().build();
    }

    public record UserProfileRequest(Long idUsuario, String codPerfil) {

    }

    public record UserProfileStatusRequest(Long idUsuario, String codPerfil, String status) {

    }

    public record UserProfileResponseDTO(
            Long idUsuario,
            String codPerfil,
            String nomPerfil,
            Long idUsuarioManutencao,
            String codStatus,
            LocalDateTime dtModi
            ) {

        public UserProfileResponseDTO(UserProfile usuarioPerfil) {
            this(
                    usuarioPerfil.getId().getIdUsuario() != null ? usuarioPerfil.getId().getIdUsuario() : null,
                    usuarioPerfil.getId().getCodPerfil() != null ? usuarioPerfil.getId().getCodPerfil() : null,
                    usuarioPerfil.getPerfil() != null ? usuarioPerfil.getPerfil().getNomPerfil() : null,
                    usuarioPerfil.getIdUsuarioManutencao(),
                    usuarioPerfil.getCodStatus(),
                    usuarioPerfil.getDtModi()
            );
        }
    }

}

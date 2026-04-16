package br.sptrans.scd.auth.adapter.in.rest;


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

import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileStatusRequestDTO;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/usuario-perfil")
@Tag(name = "Usuário-Perfil v1", description = "Endpoints para associação de usuário e perfil")

public class UserProfileController {

    private final GroupProfileManagementUseCase groupProfileManagementUseCase;

    public UserProfileController(GroupProfileManagementUseCase groupProfileManagementUseCase) {
        this.groupProfileManagementUseCase = groupProfileManagementUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
    @Operation(summary = "Listar associações usuário-perfil", description = "Retorna uma lista paginada de todas as associações usuário-perfil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de associações retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<UserProfileResponseDTO>> listUserProfiles(
            Pageable pageable) {
        Page<UserProfileResponseDTO> dtoPage = groupProfileManagementUseCase.listUserProfiles(pageable)
                .map(UserProfileResponseDTO::new);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/perfil/{codPerfil}")
    @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
    @Operation(summary = "Listar usuários associados a um perfil", description = "Retorna os usuários associados ao perfil informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuários associados retornados com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<UserProfileProjectionDTO>> getUsersByProfile(
            Pageable pageable, @PathVariable String codPerfil) {
        Page<UserProfileProjectionDTO> dtoPage = groupProfileManagementUseCase.listUserProfilesByPerfil(codPerfil, pageable);
        if (dtoPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    @Operation(summary = "Associar perfil ao usuário", description = "Associa um perfil ao usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> associate(@Valid @RequestBody UserProfileRequestDTO request) {
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
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    @Operation(summary = "Atualizar associação usuário-perfil", description = "Atualiza dados da associação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@Valid @RequestBody UserProfileRequestDTO request) {
        // Implementar lógica de atualização se necessário
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    @Operation(summary = "Atualizar status da associação", description = "Atualiza o status da associação usuário-perfil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UserProfileStatusRequestDTO request) {
        groupProfileManagementUseCase.disassociateProfileFromGroup(
                new GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand(
                        request.idUsuario().toString(),
                        request.codPerfil(),
                        request.idUsuario()
                )
        );
        return ResponseEntity.ok().build();
    }


}

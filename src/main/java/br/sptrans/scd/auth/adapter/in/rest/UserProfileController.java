package br.sptrans.scd.auth.adapter.in.rest;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileStatusRequestDTO;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
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
    private final UserResolverHelper userResolverHelper;

    public UserProfileController(GroupProfileManagementUseCase groupProfileManagementUseCase, UserResolverHelper userResolverHelper) {
        this.groupProfileManagementUseCase = groupProfileManagementUseCase;
        this.userResolverHelper = userResolverHelper;
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

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
    @Operation(summary = "Listar perfis associados a um usuário", description = "Retorna os perfis associados ao usuário informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfis associados retornados com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou sem perfis associados")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<UserProfileResponseDTO>> getProfilesByUser(
            Pageable pageable, @PathVariable Long idUsuario) {
        Page<UserProfileResponseDTO> dtoPage = groupProfileManagementUseCase.listProfilesByUsuario(idUsuario, pageable);
        if (dtoPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    @Operation(summary = "Associar perfil ao usuário", description = "Associa um perfil ativo ao usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou perfil/usuário não encontrado ou já existe associação ativa")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> associate(@Valid @RequestBody UserProfileRequestDTO request) {
        Long idUsuario = request.idUsuario();
        String codPerfil = request.codPerfil();
        // Verifica se já existe associação ativa
        Page<UserProfileResponseDTO> existentes = groupProfileManagementUseCase.listProfilesByUsuario(idUsuario, Pageable.unpaged());
        boolean existeAtivo = existentes.stream()
            .anyMatch(dto -> codPerfil.equals(dto.codPerfil()) && "A".equalsIgnoreCase(dto.codStatus()));
        if (existeAtivo) {
            return ResponseEntity.badRequest().body("Já existe associação ativa deste perfil para o usuário.");
        }
        // Cria nova associação
        Long idUsuarioManutencao = userResolverHelper.getCurrentUserId();
        groupProfileManagementUseCase.createUserProfileAssociation(idUsuario, codPerfil, idUsuarioManutencao);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ativar")
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    @Operation(summary = "Ativar associação usuário-perfil", description = "Ativa a associação do perfil com o usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação ativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> activate(@Valid @RequestBody UserProfileStatusRequestDTO request) {
        Long idUsuarioManutencao = userResolverHelper.getCurrentUserId();
  
        // Atualizar validade para 1 ano à frente
        groupProfileManagementUseCase.updateUserProfileValidity(
            request.idUsuario(),
            request.codPerfil(),
            idUsuarioManutencao,
            true // ativar
        );
        return ResponseEntity.ok().build();
        }

    @PatchMapping("/inativar")
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    @Operation(summary = "Inativar associação usuário-perfil", description = "Inativa a associação do perfil com o usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação inativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> deactivate(@Valid @RequestBody UserProfileStatusRequestDTO request) {
        Long idUsuarioManutencao = userResolverHelper.getCurrentUserId();

        // Atualizar validade para data atual (ou lógica de expiração)
        groupProfileManagementUseCase.updateUserProfileValidity(
            request.idUsuario(),
            request.codPerfil(),
            idUsuarioManutencao,
            false // inativar
        );
        return ResponseEntity.ok().build();
        }


}

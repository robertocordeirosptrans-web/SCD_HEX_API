package br.sptrans.scd.auth.adapter.in.rest;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.CreateProfileRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UpdateProfileRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.mapper.ProfileRestMapper;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.Profile;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/perfis")
@Tag(name = "Perfis v1", description = "Endpoints para gerenciamento de perfis - Versão 1")
@RequiredArgsConstructor
public class ProfileController {

    private final GroupProfileManagementUseCase groupProfileManagementUseCase;
    private final ProfileRestMapper profileRestMapper;
    private final UserResolverHelper userResolverHelper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.CRIPER + "')")
    @Operation(summary = "Criar perfil", description = "Cria um novo perfil no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProfileResponseDTO> createProfile(@Valid @RequestBody CreateProfileRequestDTO request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        GroupProfileManagementUseCase.CreateProfileCommand cmd = new GroupProfileManagementUseCase.CreateProfileCommand(
                request.codPerfil(), request.nomPerfil(), idUsuario);
        Profile perfil = groupProfileManagementUseCase.createProfile(cmd);
        return ResponseEntity.ok(profileRestMapper.toDto(perfil));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.CRIPER + "')")
    @Operation(summary = "Listar perfis", description = "Retorna uma lista de todos os perfis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de perfis retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<ProfileResponseDTO>> getAllProfile(
            @RequestParam(required = false) String nomPerfil,
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        Page<ProfileResponseDTO> dtoPage = groupProfileManagementUseCase.listProfiles(nomPerfil, codStatus, pageable)
                .map(profileRestMapper::toDto);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{codPerfil}")
    @PreAuthorize("hasAuthority('" + CacPermissions.CRIPER + "')")
    @Operation(summary = "Obter perfil por código", description = "Retorna um perfil específico pelo código")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProfileResponseDTO> getProfileById(@PathVariable String codPerfil) {
        List<Profile> perfis = groupProfileManagementUseCase.listProfiles(null);
        Optional<Profile> perfil = perfis.stream().filter(p -> p.getCodPerfil().equalsIgnoreCase(codPerfil))
                .findFirst();
        return perfil.map(profileRestMapper::toDto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{codPerfil}")
    @PreAuthorize("hasAuthority('" + CacPermissions.CRIPER + "')")
    @Operation(summary = "Atualizar perfil", description = "Atualiza um perfil específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProfileResponseDTO> updateProfile(@PathVariable String codPerfil,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        GroupProfileManagementUseCase.UpdateProfileCommand cmd = new GroupProfileManagementUseCase.UpdateProfileCommand(
                codPerfil, request.nomPerfil(), idUsuario);
        Profile perfil = groupProfileManagementUseCase.updateProfile(cmd);
        return ResponseEntity.ok(profileRestMapper.toDto(perfil));
    }

    @PatchMapping("/{codPerfil}/reactivate")
    @PreAuthorize("hasAuthority('" + CacPermissions.CRIPER + "')")
    @Operation(summary = "Ativar perfil", description = "Reativa um perfil inativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado"),
            @ApiResponse(responseCode = "400", description = "Perfil já está ativo")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> reactivateProfile(@PathVariable String codPerfil) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        GroupProfileManagementUseCase.ReactivateCommand cmd = new GroupProfileManagementUseCase.ReactivateCommand(
                codPerfil, idUsuario);
        groupProfileManagementUseCase.reactivateProfile(cmd);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codPerfil}/deactivate")
    @PreAuthorize("hasAuthority('" + CacPermissions.CRIPER + "')")
    @Operation(summary = "Inativar perfil", description = "Inativa um perfil ativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado"),
            @ApiResponse(responseCode = "400", description = "Perfil já está inativo ou há usuários ativos vinculados")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deactivateProfile(@PathVariable String codPerfil) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        GroupProfileManagementUseCase.DeactivateCommand cmd = new GroupProfileManagementUseCase.DeactivateCommand(
                codPerfil, idUsuario);
        groupProfileManagementUseCase.deactivateProfile(cmd);
        return ResponseEntity.noContent().build();
    }

}

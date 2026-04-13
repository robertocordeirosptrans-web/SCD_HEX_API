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

import br.sptrans.scd.auth.adapter.in.rest.dto.CreateProfileRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UpdateProfileRequestDTO;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.shared.dto.PageResponse;
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar perfil", description = "Cria um novo perfil no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateProfileRequestDTO request) {
        GroupProfileManagementUseCase.CreateProfileCommand cmd = new GroupProfileManagementUseCase.CreateProfileCommand(request.codPerfil(), request.nomPerfil(), request.idUsuarioLogado());
        var perfil = groupProfileManagementUseCase.createProfile(cmd);
        return ResponseEntity.ok(perfil);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar perfis", description = "Retorna uma lista de todos os perfis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de perfis retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<ProfileResponseDTO>> getAllProfile(
            Pageable pageable) {
        Page<ProfileResponseDTO> dtoPage = groupProfileManagementUseCase.listProfiles(null, pageable)
                .map(ProfileResponseDTO::new);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{codPerfil}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter perfil por código", description = "Retorna um perfil específico pelo código")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getProfileById(@PathVariable String codPerfil) {
        List<Profile> perfis = groupProfileManagementUseCase.listProfiles(null);
        Optional<Profile> perfil = perfis.stream().filter(p -> p.getCodPerfil().equalsIgnoreCase(codPerfil)).findFirst();
        return perfil.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{codPerfil}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar perfil", description = "Atualiza um perfil específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateProfile(@PathVariable String codPerfil, @Valid @RequestBody UpdateProfileRequestDTO request) {
        GroupProfileManagementUseCase.UpdateProfileCommand cmd = new GroupProfileManagementUseCase.UpdateProfileCommand(codPerfil, request.nomPerfil(), request.idUsuarioLogado());
        var perfil = groupProfileManagementUseCase.updateProfile(cmd);
        return ResponseEntity.ok(perfil);
    }

    public record ProfileResponseDTO(
            String codPerfil,
            String nomPerfil,
            Long idUsuarioManutencao,
            LocalDateTime dtModi,
            String codStatus
            ) {

        public ProfileResponseDTO(Profile perfil) {
            this(
                    perfil.getCodPerfil(),
                    perfil.getNomPerfil(),
                    perfil.getIdUsuarioManutencao(),
                    perfil.getDtModi(),
                    perfil.getCodStatus()
            );
        }

    }
}

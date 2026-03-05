package br.sptrans.scd.auth.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/perfis")
@Tag(name = "Perfis v1", description = "Endpoints para gerenciamento de perfis - Versão 1")
@RequiredArgsConstructor
public class ProfileController {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar perfil", description = "Cria um novo perfil no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createProfile() {

        return null;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar perfis", description = "Retorna uma lista de todos os perfis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de perfis retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllProfile() {
        return null;
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
        return null;
    }

    @PutMapping("/{codPerfil}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar perfil", description = "Atualiza um perfil específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateGroup(@PathVariable String codPerfil) {
        return null;
    }
}

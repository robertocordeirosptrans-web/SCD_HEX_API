package br.sptrans.scd.product.adapter.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.CreateSpeciesCommand;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.UpdateSpeciesCommand;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/species")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Espécies v1", description = "Endpoints para gerenciamento de espécies de produto")
public class SpeciesController {

    private final SpeciesManagementUseCase speciesManagementUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra uma nova espécie")
    public ResponseEntity<Species> createSpecies(
            @RequestBody CreateSpeciesRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        Species species = speciesManagementUseCase.createSpecies(
                new CreateSpeciesCommand(request.codEspecie(), request.desEspecie(), idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(species);
    }

    @PutMapping("/{codEspecie}")
    @Operation(summary = "Atualiza dados de uma espécie")
    public ResponseEntity<Species> updateSpecies(
            @PathVariable String codEspecie,
            @RequestBody UpdateSpeciesRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        Species species = speciesManagementUseCase.updateSpecies(codEspecie,
                new UpdateSpeciesCommand(request.desEspecie(), idUsuario));
        return ResponseEntity.ok(species);
    }

    @GetMapping("/{codEspecie}")
    @Operation(summary = "Busca espécie por código")
    public ResponseEntity<Species> findBySpecies(@PathVariable String codEspecie) {
        return ResponseEntity.ok(speciesManagementUseCase.findBySpecies(codEspecie));
    }

    @GetMapping
    @Operation(summary = "Lista todas as espécies, com filtro opcional de status")
    public ResponseEntity<List<Species>> findAllSpecies(
            @RequestParam(required = false) String codStatus) {
        return ResponseEntity.ok(speciesManagementUseCase.findAllSpecies(codStatus));
    }

    @PatchMapping("/{codEspecie}/activate")
    @Operation(summary = "Ativa uma espécie")
    public ResponseEntity<Void> activateSpecies(
            @PathVariable String codEspecie,
            Authentication authentication) {
        speciesManagementUseCase.activateSpecies(codEspecie, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codEspecie}/inactivate")
    @Operation(summary = "Inativa uma espécie")
    public ResponseEntity<Void> inactivateSpecies(
            @PathVariable String codEspecie,
            Authentication authentication) {
        speciesManagementUseCase.inactivateSpecies(codEspecie, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codEspecie}")
    @Operation(summary = "Remove uma espécie")
    public ResponseEntity<Void> deleteSpecies(@PathVariable String codEspecie) {
        speciesManagementUseCase.deleteSpecies(codEspecie);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateSpeciesRequest(String codEspecie, String desEspecie) {}
    public record UpdateSpeciesRequest(String desEspecie) {}
}

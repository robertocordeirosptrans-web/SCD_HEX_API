package br.sptrans.scd.product.adapter.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.CreateSpeciesCommand;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.UpdateSpeciesCommand;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/species")
@RequiredArgsConstructor
@Tag(name = "Espécies v1", description = "Endpoints para gerenciamento de espécies de produto")
public class SpeciesController {

    private final SpeciesManagementUseCase speciesManagementUseCase;

    @PostMapping
    @Operation(summary = "Cadastra uma nova espécie")
    public ResponseEntity<Species> createSpecies(@RequestBody CreateSpeciesCommand command) {
        Species species = speciesManagementUseCase.createSpecies(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(species);
    }

    @PutMapping("/{codEspecie}")
    @Operation(summary = "Atualiza dados de uma espécie")
    public ResponseEntity<Species> updateSpecies(
            @PathVariable String codEspecie,
            @RequestBody UpdateSpeciesCommand command) {
        Species species = speciesManagementUseCase.updateSpecies(codEspecie, command);
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
            @RequestParam Long idUsuario) {
        speciesManagementUseCase.activateSpecies(codEspecie, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codEspecie}/inactivate")
    @Operation(summary = "Inativa uma espécie")
    public ResponseEntity<Void> inactivateSpecies(
            @PathVariable String codEspecie,
            @RequestParam Long idUsuario) {
        speciesManagementUseCase.inactivateSpecies(codEspecie, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codEspecie}")
    @Operation(summary = "Remove uma espécie")
    public ResponseEntity<Void> deleteSpecies(@PathVariable String codEspecie) {
        speciesManagementUseCase.deleteSpecies(codEspecie);
        return ResponseEntity.noContent().build();
    }
}

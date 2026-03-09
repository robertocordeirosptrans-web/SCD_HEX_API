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

import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase.CreateTechnologyCommand;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase.UpdateTechnologyCommand;
import br.sptrans.scd.product.domain.Technology;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/technologies")
@RequiredArgsConstructor
@Tag(name = "Tecnologias v1", description = "Endpoints para gerenciamento de tecnologias de produto")
public class TechnologyController {

    private final TechnologyManagementUseCase technologyManagementUseCase;

    @PostMapping
    @Operation(summary = "Cadastra uma nova tecnologia")
    public ResponseEntity<Technology> createTechnology(@RequestBody CreateTechnologyCommand command) {
        Technology technology = technologyManagementUseCase.createTechnology(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(technology);
    }

    @PutMapping("/{codTecnologia}")
    @Operation(summary = "Atualiza dados de uma tecnologia")
    public ResponseEntity<Technology> updateTechnology(
            @PathVariable String codTecnologia,
            @RequestBody UpdateTechnologyCommand command) {
        Technology technology = technologyManagementUseCase.updateTechnology(codTecnologia, command);
        return ResponseEntity.ok(technology);
    }

    @GetMapping("/{codTecnologia}")
    @Operation(summary = "Busca tecnologia por código")
    public ResponseEntity<Technology> findByTechnology(@PathVariable String codTecnologia) {
        return ResponseEntity.ok(technologyManagementUseCase.findByTechnology(codTecnologia));
    }

    @GetMapping
    @Operation(summary = "Lista todas as tecnologias, com filtro opcional de status")
    public ResponseEntity<List<Technology>> findAllTechnologies(
            @RequestParam(required = false) String codStatus) {
        return ResponseEntity.ok(technologyManagementUseCase.findAllTechnologies(codStatus));
    }

    @PatchMapping("/{codTecnologia}/activate")
    @Operation(summary = "Ativa uma tecnologia")
    public ResponseEntity<Void> activateTechnology(
            @PathVariable String codTecnologia,
            @RequestParam Long idUsuario) {
        technologyManagementUseCase.activateTechnology(codTecnologia, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codTecnologia}/inactivate")
    @Operation(summary = "Inativa uma tecnologia")
    public ResponseEntity<Void> inactivateTechnology(
            @PathVariable String codTecnologia,
            @RequestParam Long idUsuario) {
        technologyManagementUseCase.inactivateTechnology(codTecnologia, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codTecnologia}")
    @Operation(summary = "Remove uma tecnologia")
    public ResponseEntity<Void> deleteTechnology(@PathVariable String codTecnologia) {
        technologyManagementUseCase.deleteTechnology(codTecnologia);
        return ResponseEntity.noContent().build();
    }
}

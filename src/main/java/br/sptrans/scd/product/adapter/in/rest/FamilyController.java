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

import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase.CreateFamilyCommand;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase.UpdateFamilyCommand;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/families")
@RequiredArgsConstructor
@Tag(name = "Famílias v1", description = "Endpoints para gerenciamento de famílias de produto")
public class FamilyController {

    private final FamilyManagementUseCase familyManagementUseCase;

    @PostMapping
    @Operation(summary = "Cadastra uma nova família")
    public ResponseEntity<Family> createFamily(@RequestBody CreateFamilyCommand command) {
        Family family = familyManagementUseCase.createFamily(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(family);
    }

    @PutMapping("/{codFamilia}")
    @Operation(summary = "Atualiza dados de uma família")
    public ResponseEntity<Family> updateFamily(
            @PathVariable String codFamilia,
            @RequestBody UpdateFamilyCommand command) {
        Family family = familyManagementUseCase.updateFamily(codFamilia, command);
        return ResponseEntity.ok(family);
    }

    @GetMapping("/{codFamilia}")
    @Operation(summary = "Busca família por código")
    public ResponseEntity<Family> findByFamily(@PathVariable String codFamilia) {
        return ResponseEntity.ok(familyManagementUseCase.findByFamily(codFamilia));
    }

    @GetMapping
    @Operation(summary = "Lista todas as famílias, com filtro opcional de status")
    public ResponseEntity<List<Family>> findAllFamilies(
            @RequestParam(required = false) String codStatus) {
        return ResponseEntity.ok(familyManagementUseCase.findAllFamilies(codStatus));
    }

    @PatchMapping("/{codFamilia}/activate")
    @Operation(summary = "Ativa uma família")
    public ResponseEntity<Void> activateFamily(
            @PathVariable String codFamilia,
            @RequestParam Long idUsuario) {
        familyManagementUseCase.activateFamily(codFamilia, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codFamilia}/inactivate")
    @Operation(summary = "Inativa uma família")
    public ResponseEntity<Void> inactivateFamily(
            @PathVariable String codFamilia,
            @RequestParam Long idUsuario) {
        familyManagementUseCase.inactivateFamily(codFamilia, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codFamilia}")
    @Operation(summary = "Remove uma família")
    public ResponseEntity<Void> deleteFamily(@PathVariable String codFamilia) {
        familyManagementUseCase.deleteFamily(codFamilia);
        return ResponseEntity.noContent().build();
    }
}

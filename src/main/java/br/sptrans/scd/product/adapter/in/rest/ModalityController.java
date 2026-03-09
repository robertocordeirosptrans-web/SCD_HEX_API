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

import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.CreateModalityCommand;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.UpdateModalityCommand;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/modalities")
@RequiredArgsConstructor
@Tag(name = "Modalidades v1", description = "Endpoints para gerenciamento de modalidades de produto")
public class ModalityController {

    private final ModalityManagementUseCase modalityManagementUseCase;

    @PostMapping
    @Operation(summary = "Cadastra uma nova modalidade")
    public ResponseEntity<Modality> createModality(@RequestBody CreateModalityCommand command) {
        Modality modality = modalityManagementUseCase.createModality(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(modality);
    }

    @PutMapping("/{codModalidade}")
    @Operation(summary = "Atualiza dados de uma modalidade")
    public ResponseEntity<Modality> updateModality(
            @PathVariable String codModalidade,
            @RequestBody UpdateModalityCommand command) {
        Modality modality = modalityManagementUseCase.updateModality(codModalidade, command);
        return ResponseEntity.ok(modality);
    }

    @GetMapping("/{codModalidade}")
    @Operation(summary = "Busca modalidade por código")
    public ResponseEntity<Modality> findByModality(@PathVariable String codModalidade) {
        return ResponseEntity.ok(modalityManagementUseCase.findByModality(codModalidade));
    }

    @GetMapping
    @Operation(summary = "Lista todas as modalidades, com filtro opcional de status")
    public ResponseEntity<List<Modality>> findAllModalities(
            @RequestParam(required = false) String codStatus) {
        return ResponseEntity.ok(modalityManagementUseCase.findAllModalities(codStatus));
    }

    @PatchMapping("/{codModalidade}/activate")
    @Operation(summary = "Ativa uma modalidade")
    public ResponseEntity<Void> activateModality(
            @PathVariable String codModalidade,
            @RequestParam Long idUsuario) {
        modalityManagementUseCase.activateModality(codModalidade, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codModalidade}/inactivate")
    @Operation(summary = "Inativa uma modalidade")
    public ResponseEntity<Void> inactivateModality(
            @PathVariable String codModalidade,
            @RequestParam Long idUsuario) {
        modalityManagementUseCase.inactivateModality(codModalidade, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codModalidade}")
    @Operation(summary = "Remove uma modalidade")
    public ResponseEntity<Void> deleteModality(@PathVariable String codModalidade) {
        modalityManagementUseCase.deleteModality(codModalidade);
        return ResponseEntity.noContent().build();
    }
}

package br.sptrans.scd.channel.adapter.port.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase;
import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase.CreateTypesActivityCommand;
import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase.UpdateTypesActivityCommand;
import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/types-activities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tipos de Atividade v1", description = "Endpoints para gerenciamento de tipos de atividade")
public class TypesActivityController {

    private final TypesActivityUseCase typesActivityUseCase;

    @PostMapping
    @Operation(summary = "Cadastra um novo tipo de atividade")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de atividade cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<TypesActivity> createTypesActivity(
            @RequestBody CreateTypesActivityRequest request) {
        TypesActivity result = typesActivityUseCase.createTypesActivity(
                new CreateTypesActivityCommand(request.codAtividade(), request.desAtividade()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codAtividade}")
    @Operation(summary = "Atualiza dados de um tipo de atividade")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de atividade atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<TypesActivity> updateTypesActivity(
            @PathVariable String codAtividade,
            @RequestBody UpdateTypesActivityRequest request) {
        TypesActivity result = typesActivityUseCase.updateTypesActivity(codAtividade,
                new UpdateTypesActivityCommand(request.desAtividade()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codAtividade}")
    @Operation(summary = "Busca tipo de atividade por código")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de atividade retornado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<TypesActivity> findByTypesActivity(@PathVariable String codAtividade) {
        return ResponseEntity.ok(typesActivityUseCase.findByTypesActivity(codAtividade));
    }

    @GetMapping
    @Operation(summary = "Lista todos os tipos de atividade, com filtro opcional de status")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de atividade retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<PageResponse<TypesActivity>> findAllTypesActivities(
            @RequestParam(required = false) String codStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ChannelDomainStatus statusEnum = null;
        if (codStatus != null) {
            try {
                statusEnum = ChannelDomainStatus.fromCode(codStatus);
            } catch (Exception e) {
                // Se valor inválido, retorna lista vazia ou erro, conforme política do sistema
                return ResponseEntity.ok(PageResponse.fromList(List.of(), page, size));
            }
        }
        List<TypesActivity> all = typesActivityUseCase.findAllTypesActivities(statusEnum);
        return ResponseEntity.ok(PageResponse.fromList(all, page, size));
    }

    @PatchMapping("/{codAtividade}/activate")
    @Operation(summary = "Ativa um tipo de atividade")
    public ResponseEntity<Void> activateTypesActivity(@PathVariable String codAtividade) {
        typesActivityUseCase.activateTypesActivity(codAtividade);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codAtividade}/inactivate")
    @Operation(summary = "Inativa um tipo de atividade")
    public ResponseEntity<Void> inactivateTypesActivity(@PathVariable String codAtividade) {
        typesActivityUseCase.inactivateTypesActivity(codAtividade);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codAtividade}")
    @Operation(summary = "Remove um tipo de atividade")
    public ResponseEntity<Void> deleteTypesActivity(@PathVariable String codAtividade) {
        typesActivityUseCase.deleteTypesActivity(codAtividade);
        return ResponseEntity.noContent().build();
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateTypesActivityRequest(String codAtividade, String desAtividade) {}
    public record UpdateTypesActivityRequest(String desAtividade) {}
}

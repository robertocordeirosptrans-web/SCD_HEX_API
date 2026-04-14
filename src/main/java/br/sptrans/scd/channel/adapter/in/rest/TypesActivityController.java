package br.sptrans.scd.channel.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import br.sptrans.scd.channel.adapter.in.rest.dto.TypesActivityResponseDTO;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.TypesActivityMapper;
import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase;
import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase.CreateTypesActivityCommand;
import br.sptrans.scd.channel.application.port.in.TypesActivityUseCase.UpdateTypesActivityCommand;
import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.security.CadPermissions;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tipos de Atividade v1", description = "Endpoints para gerenciamento de tipos de atividade")
public class TypesActivityController {

    private static final Logger log = LoggerFactory.getLogger(TypesActivityController.class);

    private final TypesActivityUseCase typesActivityUseCase;
    private final TypesActivityMapper typesActivityMapper;

    @PostMapping
    @Operation(summary = "Cadastra um novo tipo de atividade")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de atividade cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_CADTIPDEATI + "')")
    public ResponseEntity<TypesActivityResponseDTO> createTypesActivity(
            @RequestBody CreateTypesActivityRequest request) {
        log.info("REST POST /types-activities — Criando atividade: {}", request.codAtividade());
        TypesActivity result = typesActivityUseCase.createTypesActivity(
                new CreateTypesActivityCommand(request.codAtividade(), request.desAtividade()));
        return ResponseEntity.status(HttpStatus.CREATED).body(typesActivityMapper.toResponseDTO(result));
    }

    @PutMapping("/{codAtividade}")
    @Operation(summary = "Atualiza dados de um tipo de atividade")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de atividade atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_ATUTIPDEATI + "')")
    public ResponseEntity<TypesActivityResponseDTO> updateTypesActivity(
            @PathVariable String codAtividade,
            @RequestBody UpdateTypesActivityRequest request) {
        log.info("REST PUT /types-activities/{} — Atualizando", codAtividade);
        TypesActivity result = typesActivityUseCase.updateTypesActivity(codAtividade,
                new UpdateTypesActivityCommand(request.desAtividade()));
        return ResponseEntity.ok(typesActivityMapper.toResponseDTO(result));
    }

    @GetMapping("/{codAtividade}")
    @Operation(summary = "Busca tipo de atividade por código")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de atividade retornado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_BUSTIPDEATIPORCOD + "')")
    public ResponseEntity<TypesActivityResponseDTO> findByTypesActivity(@PathVariable String codAtividade) {
        return ResponseEntity.ok(typesActivityMapper.toResponseDTO(typesActivityUseCase.findByTypesActivity(codAtividade)));
    }

    @GetMapping
    @Operation(summary = "Lista todos os tipos de atividade, com filtro opcional de status")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de atividade retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_LISTIPDEATI + "')")
    public ResponseEntity<PageResponse<TypesActivityResponseDTO>> findAllTypesActivities(
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        ChannelDomainStatus statusEnum = null;
        if (codStatus != null) {
            try {
                statusEnum = ChannelDomainStatus.fromCode(codStatus);
            } catch (Exception e) {
                return ResponseEntity.ok(PageResponse.fromPage(Page.empty(pageable)));
            }
        }
        Page<TypesActivity> page = typesActivityUseCase.findAllTypesActivities(statusEnum, pageable);
        return ResponseEntity.ok(PageResponse.fromPage(page.map(typesActivityMapper::toResponseDTO)));
    }

    @PatchMapping("/{codAtividade}/activate")
    @Operation(summary = "Ativa um tipo de atividade")
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_ATITIPDEATI + "')")
    public ResponseEntity<Void> activateTypesActivity(@PathVariable String codAtividade) {
        typesActivityUseCase.activateTypesActivity(codAtividade);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codAtividade}/inactivate")
    @Operation(summary = "Inativa um tipo de atividade")
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_INATIPDEATI + "')")
    public ResponseEntity<Void> inactivateTypesActivity(@PathVariable String codAtividade) {
        typesActivityUseCase.inactivateTypesActivity(codAtividade);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codAtividade}")
    @Operation(summary = "Remove um tipo de atividade")
    @PreAuthorize("hasAuthority('" + CadPermissions.TYP_REMTIPDEATI + "')")
    public ResponseEntity<Void> deleteTypesActivity(@PathVariable String codAtividade) {
        log.info("REST DELETE /types-activities/{}", codAtividade);
        typesActivityUseCase.deleteTypesActivity(codAtividade);
        return ResponseEntity.noContent().build();
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateTypesActivityRequest(String codAtividade, String desAtividade) {}
    public record UpdateTypesActivityRequest(String desAtividade) {}
}

package br.sptrans.scd.product.adapter.in.rest;

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

import br.sptrans.scd.product.adapter.in.rest.dto.TechnologyResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase.CreateTechnologyCommand;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase.UpdateTechnologyCommand;
import br.sptrans.scd.product.domain.Technology;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CadPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/technologies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tecnologias v1", description = "Endpoints para gerenciamento de tecnologias de produto")
public class TechnologyController {

    private final TechnologyManagementUseCase technologyManagementUseCase;
    private final UserResolverHelper userResolverHelper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_CADTEC + "')")
    @Operation(summary = "Cadastra uma nova tecnologia")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnologia cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
            public ResponseEntity<Technology> createTechnology(
                @RequestBody br.sptrans.scd.product.adapter.in.rest.dto.TechnologyRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Technology technology = technologyManagementUseCase.create(
            new CreateTechnologyCommand(
                request.codTecnologia(),
                request.desTecnologia(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(technology);
        }

    @PutMapping("/{codTecnologia}")
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_ATUTEC + "')")
    @Operation(summary = "Atualiza dados de uma tecnologia")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnologia atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
            public ResponseEntity<Technology> updateTechnology(
                @PathVariable String codTecnologia,
                @RequestBody br.sptrans.scd.product.adapter.in.rest.dto.TechnologyRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Technology technology = technologyManagementUseCase.update(codTecnologia,
            new UpdateTechnologyCommand(request.desTecnologia(), idUsuario));
        return ResponseEntity.ok(technology);
        }

    @GetMapping("/{codTecnologia}")
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_BUSTECPORCOD + "')")
    @Operation(summary = "Busca tecnologia por código")
    public ResponseEntity<TechnologyResponseDTO> findByTechnology(@PathVariable String codTecnologia) {
        Technology technology = technologyManagementUseCase.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));
        TechnologyResponseDTO dto = new TechnologyResponseDTO(
            technology.getCodTecnologia(),
            technology.getDesTecnologia(),
            technology.getCodStatus(),
            technology.getDtCadastro(),
            technology.getDtManutencao(),
            UserSimpleMapper.toDto(technology.getIdUsuarioCadastro()),
            UserSimpleMapper.toDto(technology.getIdUsuarioManutencao())
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_LISTEC + "')")
    @Operation(summary = "Lista todas as tecnologias, com filtro opcional de status")
    public ResponseEntity<PageResponse<TechnologyResponseDTO>> findAllTechnologies(
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        Page<TechnologyResponseDTO> dtoPage = technologyManagementUseCase.findAll(codStatus, pageable)
            .map(technology -> new TechnologyResponseDTO(
                technology.getCodTecnologia(),
                technology.getDesTecnologia(),
                technology.getCodStatus(),
                technology.getDtCadastro(),
                technology.getDtManutencao(),
                UserSimpleMapper.toDto(technology.getIdUsuarioCadastro()),
                UserSimpleMapper.toDto(technology.getIdUsuarioManutencao())
            ));
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PatchMapping("/{codTecnologia}/activate")
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_ATITEC + "')")
    @Operation(summary = "Ativa uma tecnologia")
    public ResponseEntity<Void> activateTechnology(
            @PathVariable String codTecnologia) {
        technologyManagementUseCase.activate(codTecnologia, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codTecnologia}/inactivate")
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_INATEC + "')")
    @Operation(summary = "Inativa uma tecnologia")
    public ResponseEntity<Void> inactivateTechnology(
            @PathVariable String codTecnologia) {
        technologyManagementUseCase.inactivate(codTecnologia, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codTecnologia}")
    @PreAuthorize("hasAuthority('" + CadPermissions.TEC_REMTEC + "')")
    @Operation(summary = "Remove uma tecnologia")
    public ResponseEntity<Void> deleteTechnology(@PathVariable String codTecnologia) {
        technologyManagementUseCase.delete(codTecnologia);
        return ResponseEntity.noContent().build();
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    // Request DTOs removidos, usar TechnologyRequest
}

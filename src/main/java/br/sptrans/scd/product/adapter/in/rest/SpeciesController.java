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

import br.sptrans.scd.product.adapter.in.rest.dto.SpeciesRequest;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.CreateSpeciesCommand;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.UpdateSpeciesCommand;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.dto.CatalogueDTO;
import br.sptrans.scd.shared.dto.CatalogueMapper;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CadPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/species")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Espécies v1", description = "Endpoints para gerenciamento de espécies de produto")
public class SpeciesController {

    private final SpeciesManagementUseCase speciesManagementUseCase;
    private final UserResolverHelper userResolverHelper;
    private final CatalogueMapper catalogueMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_CADESP + "')")
    @Operation(summary = "Cadastra uma nova espécie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Espécie cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CatalogueDTO> createSpecies(
            @Valid @RequestBody SpeciesRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Species species = speciesManagementUseCase.create(
                new CreateSpeciesCommand(
                        request.codEspecie(),
                        request.desEspecie(),
                        idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueMapper.toDto(species));
    }

    @PutMapping("/{codEspecie}")
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_ATUESP + "')")
    @Operation(summary = "Atualiza dados de uma espécie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Espécie atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CatalogueDTO> updateSpecies(
            @PathVariable String codEspecie,
            @Valid @RequestBody SpeciesRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Species species = speciesManagementUseCase.update(codEspecie,
                                new UpdateSpeciesCommand(request.desEspecie(), idUsuario));
        return ResponseEntity.ok(catalogueMapper.toDto(species));
    }

    @GetMapping("/{codEspecie}")
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_BUSESPPORCOD + "')")
    @Operation(summary = "Busca espécie por código")
    public ResponseEntity<CatalogueDTO> findBySpecies(@PathVariable String codEspecie) {
        Species species = speciesManagementUseCase.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));
        return ResponseEntity.ok(catalogueMapper.toDto(species));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_LISESP + "')")
    @Operation(summary = "Lista todas as espécies, com filtro opcional de status")
    public ResponseEntity<PageResponse<CatalogueDTO>> findAllSpecies(
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        Page<CatalogueDTO> dtoPage = speciesManagementUseCase.findAll(codStatus, pageable)
                .map(catalogueMapper::toDto);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PatchMapping("/{codEspecie}/activate")
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_ATIESP + "')")
    @Operation(summary = "Ativa uma espécie")
    public ResponseEntity<Void> activateSpecies(
            @PathVariable String codEspecie) {
        speciesManagementUseCase.activate(codEspecie, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codEspecie}/inactivate")
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_INAESP + "')")
    @Operation(summary = "Inativa uma espécie")
    public ResponseEntity<Void> inactivateSpecies(
            @PathVariable String codEspecie) {
        speciesManagementUseCase.inactivate(codEspecie, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codEspecie}")
    @PreAuthorize("hasAuthority('" + CadPermissions.SPE_REMESP + "')")
    @Operation(summary = "Remove uma espécie")
    public ResponseEntity<Void> deleteSpecies(@PathVariable String codEspecie) {
        speciesManagementUseCase.delete(codEspecie);
        return ResponseEntity.noContent().build();
    }
}

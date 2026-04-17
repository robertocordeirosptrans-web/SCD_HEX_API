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

import br.sptrans.scd.product.adapter.in.rest.dto.ModalityRequest;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.CreateModalityCommand;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.UpdateModalityCommand;
import br.sptrans.scd.product.domain.Modality;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/modalities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Modalidades v1", description = "Endpoints para gerenciamento de modalidades de produto")
public class ModalityController {

    private final ModalityManagementUseCase modalityManagementUseCase;
    private final UserResolverHelper userResolverHelper;
    private final CatalogueMapper catalogueMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_CADMOD + "')")
    @Operation(summary = "Cadastra uma nova modalidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Modalidade cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CatalogueDTO> createModality(
            @Valid @RequestBody ModalityRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Modality modality = modalityManagementUseCase.create(
            new CreateModalityCommand(
                request.codModalidade(),
                request.desModalidade(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueMapper.toDto(modality));
    }

    @PutMapping("/{codModalidade}")
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_ATUMOD + "')")
    @Operation(summary = "Atualiza dados de uma modalidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Modalidade atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CatalogueDTO> updateModality(
            @PathVariable String codModalidade,
            @Valid @RequestBody ModalityRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Modality modality = modalityManagementUseCase.update(codModalidade,
            new UpdateModalityCommand(request.desModalidade(), idUsuario));
        return ResponseEntity.ok(catalogueMapper.toDto(modality));
    }

    @GetMapping("/{codModalidade}")
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_BUSMODPORCOD + "')")
    @Operation(summary = "Busca modalidade por código")
    public ResponseEntity<CatalogueDTO> findByModality(@PathVariable String codModalidade) {
        Modality modality = modalityManagementUseCase.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
        return ResponseEntity.ok(catalogueMapper.toDto(modality));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_LISMOD + "')")
    @Operation(summary = "Lista todas as modalidades, com filtro opcional de status")
    public ResponseEntity<PageResponse<CatalogueDTO>> findAllModalities(
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        Page<CatalogueDTO> dtoPage = modalityManagementUseCase.findAll(codStatus, pageable)
            .map(catalogueMapper::toDto);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PatchMapping("/{codModalidade}/activate")
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_ATIMOD + "')")
    @Operation(summary = "Ativa uma modalidade")
    public ResponseEntity<Void> activateModality(
            @PathVariable String codModalidade) {
        modalityManagementUseCase.activate(codModalidade, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codModalidade}/inactivate")
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_INAMOD + "')")
    @Operation(summary = "Inativa uma modalidade")
    public ResponseEntity<Void> inactivateModality(
            @PathVariable String codModalidade) {
        modalityManagementUseCase.inactivate(codModalidade, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codModalidade}")
    @PreAuthorize("hasAuthority('" + CadPermissions.MOD_REMMOD + "')")
    @Operation(summary = "Remove uma modalidade")
    public ResponseEntity<Void> deleteModality(@PathVariable String codModalidade) {
        modalityManagementUseCase.delete(codModalidade);
        return ResponseEntity.noContent().build();
    }
}

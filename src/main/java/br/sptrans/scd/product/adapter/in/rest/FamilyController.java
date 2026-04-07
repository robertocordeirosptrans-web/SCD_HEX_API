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

import br.sptrans.scd.product.adapter.in.rest.dto.FamilyRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.FamilyResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase.CreateFamilyCommand;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase.UpdateFamilyCommand;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/families")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Famílias v1", description = "Endpoints para gerenciamento de famílias de produto")
public class FamilyController {

    private final FamilyManagementUseCase familyManagementUseCase;
    private final UserResolverHelper userResolverHelper;

    @PostMapping
    @Operation(summary = "Cadastra uma nova família")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Família cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
            public ResponseEntity<Family> createFamily(
               @Valid  @RequestBody FamilyRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Family family = familyManagementUseCase.create(
            new CreateFamilyCommand(
                request.codFamilia(),
                request.desFamilia(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(family);
        }

    @PutMapping("/{codFamilia}")
    @Operation(summary = "Atualiza dados de uma família")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Família atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
            public ResponseEntity<Family> updateFamily(
                @PathVariable String codFamilia,
                @Valid @RequestBody FamilyRequest request) {
        Long idUsuario = userResolverHelper.getCurrentUserId();
        Family family = familyManagementUseCase.update(codFamilia,
            new UpdateFamilyCommand(request.desFamilia(), idUsuario));
        return ResponseEntity.ok(family);
        }

    @GetMapping("/{codFamilia}")
    @Operation(summary = "Busca família por código")
    public ResponseEntity<FamilyResponseDTO> findByFamily(@PathVariable String codFamilia) {
        Family family = familyManagementUseCase.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));
        FamilyResponseDTO dto = new FamilyResponseDTO(
            family.getCodFamilia(),
            family.getDesFamilia(),
            family.getCodStatus(),
            family.getDtCadastro(),
            family.getDtManutencao(),
            UserSimpleMapper.toDto(family.getIdUsuarioCadastro()),
            UserSimpleMapper.toDto(family.getIdUsuarioManutencao())
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Lista todas as famílias, com filtro opcional de status")
    public ResponseEntity<PageResponse<FamilyResponseDTO>> findAllFamilies(
            @RequestParam(required = false) String codStatus,
            Pageable pageable) {
        Page<FamilyResponseDTO> dtoPage = familyManagementUseCase.findAll(codStatus, pageable)
            .map(family -> new FamilyResponseDTO(
                family.getCodFamilia(),
                family.getDesFamilia(),
                family.getCodStatus(),
                family.getDtCadastro(),
                family.getDtManutencao(),
                UserSimpleMapper.toDto(family.getIdUsuarioCadastro()),
                UserSimpleMapper.toDto(family.getIdUsuarioManutencao())
            ));
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PatchMapping("/{codFamilia}/activate")
    @Operation(summary = "Ativa uma família")
    public ResponseEntity<Void> activateFamily(
            @PathVariable String codFamilia) {
        familyManagementUseCase.activate(codFamilia, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codFamilia}/inactivate")
    @Operation(summary = "Inativa uma família")
    public ResponseEntity<Void> inactivateFamily(
            @PathVariable String codFamilia) {
        familyManagementUseCase.inactivate(codFamilia, userResolverHelper.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codFamilia}")
    @Operation(summary = "Remove uma família")
    public ResponseEntity<Void> deleteFamily(@PathVariable String codFamilia) {
        familyManagementUseCase.delete(codFamilia);
        return ResponseEntity.noContent().build();
    }

}

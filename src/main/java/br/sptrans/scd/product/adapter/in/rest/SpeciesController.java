package br.sptrans.scd.product.adapter.in.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.adapter.port.in.rest.dto.SpeciesResponseDTO;
import br.sptrans.scd.product.adapter.port.in.rest.dto.UserSimpleMapper;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.CreateSpeciesCommand;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.UpdateSpeciesCommand;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/species")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Espécies v1", description = "Endpoints para gerenciamento de espécies de produto")
public class SpeciesController {

    private final SpeciesManagementUseCase speciesManagementUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra uma nova espécie")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Espécie cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<Species> createSpecies(
            @RequestBody CreateSpeciesRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        Species species = speciesManagementUseCase.createSpecies(
                new CreateSpeciesCommand(request.codEspecie(), request.desEspecie(), idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(species);
    }

    @PutMapping("/{codEspecie}")
    @Operation(summary = "Atualiza dados de uma espécie")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Espécie atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<Species> updateSpecies(
            @PathVariable String codEspecie,
            @RequestBody UpdateSpeciesRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        Species species = speciesManagementUseCase.updateSpecies(codEspecie,
                new UpdateSpeciesCommand(request.desEspecie(), idUsuario));
        return ResponseEntity.ok(species);
    }

    @GetMapping("/{codEspecie}")
    @Operation(summary = "Busca espécie por código")
    public ResponseEntity<SpeciesResponseDTO> findBySpecies(@PathVariable String codEspecie) {
        Species species = speciesManagementUseCase.findBySpecies(codEspecie);
        SpeciesResponseDTO dto = new SpeciesResponseDTO(
            species.getCodEspecie(),
            species.getDesEspecie(),
            species.getCodStatus(),
            species.getDtCadastro(),
            species.getDtManutencao(),
            UserSimpleMapper.toDto(species.getIdUsuarioCadastro()),
            UserSimpleMapper.toDto(species.getIdUsuarioManutencao())
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Lista todas as espécies, com filtro opcional de status")
    public ResponseEntity<PageResponse<SpeciesResponseDTO>> findAllSpecies(
            @RequestParam(required = false) String codStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Species> all = speciesManagementUseCase.findAllSpecies(codStatus);
        List<SpeciesResponseDTO> dtos = all.stream()
            .map(species -> new SpeciesResponseDTO(
                species.getCodEspecie(),
                species.getDesEspecie(),
                species.getCodStatus(),
                species.getDtCadastro(),
                species.getDtManutencao(),
                UserSimpleMapper.toDto(species.getIdUsuarioCadastro()),
                UserSimpleMapper.toDto(species.getIdUsuarioManutencao())
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
    }

    @PatchMapping("/{codEspecie}/activate")
    @Operation(summary = "Ativa uma espécie")
    public ResponseEntity<Void> activateSpecies(
            @PathVariable String codEspecie,
            Authentication authentication) {
        speciesManagementUseCase.activateSpecies(codEspecie, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codEspecie}/inactivate")
    @Operation(summary = "Inativa uma espécie")
    public ResponseEntity<Void> inactivateSpecies(
            @PathVariable String codEspecie,
            Authentication authentication) {
        speciesManagementUseCase.inactivateSpecies(codEspecie, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codEspecie}")
    @Operation(summary = "Remove uma espécie")
    public ResponseEntity<Void> deleteSpecies(@PathVariable String codEspecie) {
        speciesManagementUseCase.deleteSpecies(codEspecie);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateSpeciesRequest(String codEspecie, String desEspecie) {}
    public record UpdateSpeciesRequest(String desEspecie) {}
}

package br.sptrans.scd.auth.adapter.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.FunctionalityResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.request.CreateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.DeactivateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.ReactivateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.UpdateFunctionalityRequest;
import br.sptrans.scd.auth.application.port.in.FunctionCase;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.adapter.in.rest.mapper.FunctionalityRestMapper;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/functionality")
@Tag(name = "Funcionalidades v1", description = "Endpoints para gerenciamento de funcionalidades - Versão 1")
@RequiredArgsConstructor

public class FunctionalityController {

        private final FunctionCase functionCase;
        private final UserResolverHelper userResolverHelper;
        private final FunctionalityRestMapper functionRestMapper;

    @GetMapping
    @Operation(summary = "Listar Funcionalidades", description = "Retorna uma lista de todas as funcionalidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funcionalidades retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<FunctionalityResponseDTO>> getAllFunctionality(
            Pageable pageable) {
        Page<FunctionalityResponseDTO> dtoPage = functionCase.listFunctionalities(pageable)
                .map(functionRestMapper::toDto);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{codSistema}/{codModulo}/{codRotina}/{codFuncionalidade}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter funcionalidade por códigos", description = "Retorna uma funcionalidade específica pelos códigos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionalidade retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionalidade não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FunctionalityResponseDTO> getFunctionalityById(@PathVariable String codSistema,
            @PathVariable String codModulo, @PathVariable String codRotina, @PathVariable String codFuncionalidade) {

        FunctionalityKey key = new FunctionalityKey(codSistema, codModulo, codRotina, codFuncionalidade);

        return functionCase.findById(key)
                .map(functionRestMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Functionality> create(@Valid @RequestBody CreateFunctionalityRequest request) {
        Long userId = userResolverHelper.getCurrentUserId();
        var command = new CreateFunctionalityRequest(
                request.codSistema(),
                request.codModulo(),
                request.codRotina(),
                request.codFuncionalidade(),
                request.nomFuncionalidade(),
                userId);
        var result = functionCase.createFunctionality(command);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<Functionality> update(@Valid @RequestBody UpdateFunctionalityRequest request) {
        Long userId = userResolverHelper.getCurrentUserId();
        var command = new UpdateFunctionalityRequest(
                request.codSistema(),
                request.codModulo(),
                request.codRotina(),
                request.codFuncionalidade(),
                request.nomFuncionalidade(),
                userId);
        var result = functionCase.updateFunctionality(command);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<Void> deactivate(@Valid @RequestBody DeactivateFunctionalityRequest request) {
        Long userId = userResolverHelper.getCurrentUserId();
        var command = new DeactivateFunctionalityRequest(
                request.codSistema(),
                request.codModulo(),
                request.codRotina(),
                request.codFuncionalidade(),
                userId);
        functionCase.deactivateFunctionality(command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reactivate")
    public ResponseEntity<Void> reactivate(@Valid @RequestBody ReactivateFunctionalityRequest request) {
        Long userId = userResolverHelper.getCurrentUserId();
        var command = new ReactivateFunctionalityRequest(
                request.codSistema(),
                request.codModulo(),
                request.codRotina(),
                request.codFuncionalidade(),
                userId);
        functionCase.reactivateFunctionality(command);
        return ResponseEntity.noContent().build();
    }
}

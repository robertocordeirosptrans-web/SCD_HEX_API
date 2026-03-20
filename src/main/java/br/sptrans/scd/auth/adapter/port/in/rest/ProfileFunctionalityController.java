package br.sptrans.scd.auth.adapter.port.in.rest;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.port.in.rest.GroupController.GrupoResponseDTO;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/perfil-funcionalidade")
@Tag(name = "Perfil-Funcionalidade v1", description = "Endpoints para associação de perfil e funcionalidades")

public class ProfileFunctionalityController {

    private final GroupProfileManagementUseCase groupProfileManagementUseCase;

    public ProfileFunctionalityController(GroupProfileManagementUseCase groupProfileManagementUseCase) {
        this.groupProfileManagementUseCase = groupProfileManagementUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar associações perfil-funcionalidade", description = "Retorna uma lista paginada de todas as associações perfil-funcionalidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de associações retornada com sucesso")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<ProfileFunctionalityResponseDTO>> listProfileFunctionalities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ProfileFunctionality> all = groupProfileManagementUseCase.listProfileFunctionalities();
        List<ProfileFunctionalityResponseDTO> proDTOs = all.stream()
                .map(ProfileFunctionalityResponseDTO::new)
                .toList();
        return ResponseEntity.ok(PageResponse.fromList(proDTOs, page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Associar funcionalidade ao perfil", description = "Associa uma funcionalidade ao perfil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> associate(@RequestBody ProfileFunctionalityRequest request) {
        FunctionalityKey key = new FunctionalityKey(
                request.codSistema(),
                request.codModulo(),
                request.codRotina(),
                request.codFuncionalidade()
        );

        var functionality = new Functionality();
        groupProfileManagementUseCase.associateFunctionalitiesToProfile(
                new GroupProfileManagementUseCase.AssociateFunctionalitiesToProfileCommand(
                        request.codPerfil(),
                        java.util.Set.of(functionality),
                        request.idUsuarioLogado()
                )
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar associação perfil-funcionalidade", description = "Atualiza dados da associação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@RequestBody ProfileFunctionalityRequest request) {
        // Implementar lógica de atualização se necessário
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar status da associação", description = "Atualiza o status da associação perfil-funcionalidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateStatus(@RequestBody ProfileFunctionalityStatusRequest request) {
        var key = new FunctionalityKey(
                request.codSistema(),
                request.codModulo(),
                request.codRotina(),
                request.codFuncionalidade()
        );
        groupProfileManagementUseCase.disassociateFunctionalityFromProfile(
                new GroupProfileManagementUseCase.DisassociateFunctionalityFromProfileCommand(
                        request.codPerfil(),
                        new GroupProfileManagementUseCase.FunctionalityKey(
                                request.codSistema(),
                                request.codModulo(),
                                request.codRotina(),
                                request.codFuncionalidade()
                        ),
                        request.idUsuarioLogado()
                )
        );
        return ResponseEntity.ok().build();
    }

    public record ProfileFunctionalityRequest(String codSistema, String codModulo, String codRotina, String codFuncionalidade, String codPerfil, Long idUsuarioLogado) {

    }

    public record ProfileFunctionalityStatusRequest(String codSistema, String codModulo, String codRotina, String codFuncionalidade, String codPerfil, String status, Long idUsuarioLogado) {

    }

    public record ProfileFunctionalityResponseDTO(
            String codSistema,
            String codModulo,
            String codRotina,
            String codFuncionalidade,
            String codPerfil,
            Long idUsuarioManutencao,
            LocalDate dtInicioValidade
            ) {

        public ProfileFunctionalityResponseDTO(ProfileFunctionality perfilFuncionalidade) {
            this(
                    perfilFuncionalidade.getId().getCodSistema(),
                    perfilFuncionalidade.getId().getCodModulo(),
                    perfilFuncionalidade.getId().getCodRotina(),
                    perfilFuncionalidade.getId().getCodFuncionalidade(),
                    perfilFuncionalidade.getId().getCodPerfil(),
                    perfilFuncionalidade.getIdUsuarioManutencao(),
                    perfilFuncionalidade.getDtInicioValidade()
            );
        }
    }

}

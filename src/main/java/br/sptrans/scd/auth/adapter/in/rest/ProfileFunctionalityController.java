package br.sptrans.scd.auth.adapter.in.rest;

import java.util.Set;

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

import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityStatusRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.mapper.ProfileFunctionalityRestMapper;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CacPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/profile_functionality")
@Tag(name = "Perfil-Funcionalidade v1", description = "Endpoints para associação de perfil e funcionalidades")
@RequiredArgsConstructor
public class ProfileFunctionalityController {

        private final GroupProfileManagementUseCase groupProfileManagementUseCase;
        private final ProfileFunctionalityRestMapper profileFunctionalityRestMapper;
        private final UserResolverHelper userResolverHelper;

        @GetMapping("/{codPerfil}/functionalities")
        @PreAuthorize("hasAuthority('" + CacPermissions.LISASSPERFUN + "')")
        @Operation(summary = "Listar funcionalidades de um perfil", description = "Retorna as funcionalidades associadas ao perfil informado (projeção customizada)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de funcionalidades retornada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<PageResponse<ProfileFunctionalityProjectionDTO>> listFunctionalitiesByProfile(
                        @PathVariable("codPerfil") String codPerfil,
                        Pageable pageable) {
                Page<ProfileFunctionalityProjectionDTO> dtoPage = groupProfileManagementUseCase
                                .listFunctionalitiesProjectionByProfile(codPerfil, pageable);
                return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
        }

        @GetMapping
        @PreAuthorize("hasAuthority('" + CacPermissions.LISASSPERFUN + "')")
        @Operation(summary = "Listar associações perfil-funcionalidade", description = "Retorna uma lista paginada de todas as associações perfil-funcionalidade")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de associações retornada com sucesso")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<PageResponse<ProfileFunctionalityResponseDTO>> listProfileFunctionalities(
                        Pageable pageable) {
                Page<ProfileFunctionalityResponseDTO> dtoPage = groupProfileManagementUseCase
                                .listProfileFunctionalities(pageable)
                                .map(profileFunctionalityRestMapper::toDto);
                return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
        }

        @PostMapping
        @PreAuthorize("hasAuthority('" + CacPermissions.ASSFUNAOPER + "')")
        @Operation(summary = "Associar funcionalidade ao perfil", description = "Associa uma funcionalidade ao perfil")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Associação criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> associate(@Valid @RequestBody ProfileFunctionalityRequestDTO request) {

                Long idUsuario = userResolverHelper.getCurrentUserId();

                // Criar FunctionalityKey com os dados do request
                var functionalityKey = new FunctionalityKey(
                                request.codSistema(),
                                request.codModulo(),
                                request.codRotina(),
                                request.codFuncionalidade());

                // Criar e popular a Functionality com os dados do request
                var functionality = new Functionality();

                functionality.setId(functionalityKey);
                functionality.setCodStatus("A"); // Status ativo por padrão
                functionality.setIdUsuarioManutencao(idUsuario);
                functionality.setDtModi(java.time.LocalDateTime.now());

                groupProfileManagementUseCase.associateFunctionalitiesToProfile(
                                new GroupProfileManagementUseCase.AssociateFunctionalitiesToProfileCommand(
                                                request.codPerfil(),
                                                Set.of(functionality),
                                                idUsuario));
                return ResponseEntity.ok().build();
        }

        @PutMapping
        @PreAuthorize("hasAuthority('" + CacPermissions.ASSFUNAOPER + "')")
        @Operation(summary = "Atualizar associação perfil-funcionalidade", description = "Atualiza dados da associação")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> update(@Valid @RequestBody ProfileFunctionalityRequestDTO request) {

                Long idUsuario = userResolverHelper.getCurrentUserId();

                // Remover associação anterior
                groupProfileManagementUseCase.disassociateFunctionalityFromProfile(
                                new GroupProfileManagementUseCase.DisassociateFunctionalityFromProfileCommand(
                                                request.codPerfil(),
                                                new FunctionalityKey(
                                                                request.codSistema(),
                                                                request.codModulo(),
                                                                request.codRotina(),
                                                                request.codFuncionalidade()),
                                                idUsuario));

                // Criar e popular a Functionality com os dados atualizados
                var functionality = new Functionality();
                var id = new FunctionalityKey(
                                request.codSistema(),
                                request.codModulo(),
                                request.codRotina(),
                                request.codFuncionalidade());
                functionality.setId(id);
                functionality.setCodStatus("A"); // Status ativo por padrão
                functionality.setIdUsuarioManutencao(idUsuario);
                functionality.setDtModi(java.time.LocalDateTime.now());

                // Recriar com novos dados
                groupProfileManagementUseCase.associateFunctionalitiesToProfile(
                                new GroupProfileManagementUseCase.AssociateFunctionalitiesToProfileCommand(
                                                request.codPerfil(),
                                                Set.of(functionality),
                                                idUsuario));

                return ResponseEntity.ok().build();
        }

        @PatchMapping("/status")
        @PreAuthorize("hasAuthority('" + CacPermissions.ASSFUNAOPER + "')")
        @Operation(summary = "Atualizar status da associação", description = "Atualiza o status da associação perfil-funcionalidade")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Associação não encontrada")
        })
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> updateStatus(@Valid @RequestBody ProfileFunctionalityStatusRequestDTO request) {

                Long idUsuario = request.idUsuarioLogado();
                // Criar FunctionalityKey com os dados do request
                var functionalityKey = new FunctionalityKey(
                                request.codSistema(),
                                request.codModulo(),
                                request.codRotina(),
                                request.codFuncionalidade());
                // Se o status é 'I' (inativo), remover a associação
                if ("I".equals(request.status())) {
                        groupProfileManagementUseCase.disassociateFunctionalityFromProfile(
                                        new GroupProfileManagementUseCase.DisassociateFunctionalityFromProfileCommand(
                                                        request.codPerfil(),
                                                        functionalityKey,
                                                        idUsuario));
                }
                // Se o status é 'A' (ativo), recriar a associação
                else if ("A".equals(request.status())) {
                        var functionality = new Functionality();
                        functionality.setId(functionalityKey);
                        functionality.setCodStatus("A");
                        functionality.setIdUsuarioManutencao(idUsuario);
                        functionality.setDtModi(java.time.LocalDateTime.now());

                        groupProfileManagementUseCase.associateFunctionalitiesToProfile(
                                        new GroupProfileManagementUseCase.AssociateFunctionalitiesToProfileCommand(
                                                        request.codPerfil(),
                                                        Set.of(functionality),
                                                        idUsuario));
                }

                return ResponseEntity.ok().build();
        }

}

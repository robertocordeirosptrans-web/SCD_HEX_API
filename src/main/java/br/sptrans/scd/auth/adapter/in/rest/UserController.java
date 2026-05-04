package br.sptrans.scd.auth.adapter.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.StatusChangeResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UpdateUserDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserFilterRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserResponseDTO;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.adapter.specification.UserSpecification;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.CreateUserCommand;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.StatusChangeCommand;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.UpdateUserCommand;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CacPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/users")
@Tag(name = "Usuario v1", description = "Endpoints Gerenciamento de Usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementUseCase userManagementUseCase;
    private final UserResolverHelper userResolverHelper;

    @GetMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.LISUSU + "')")
    @Operation(summary = "Lista usuários com paginação, filtros e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PageResponse<UserResponseDTO>> listUsers(
            @RequestParam(required = false) String nomUsuario,
            @RequestParam(required = false) String nomEmail,
            @RequestParam(required = false) String codStatus,
            @RequestParam(required = false) String codPerfil,
            Pageable pageable) {
        var filtro = new UserFilterRequestDTO(nomUsuario, nomEmail, codPerfil, codStatus);
        Specification<UserEntityJpa> spec = UserSpecification.filterUsers(filtro);
        Page<User> page = userManagementUseCase.listUsersPaginated(spec, pageable);
        Page<UserResponseDTO> dtoPage = page.map(this::toResponseDTO);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasAuthority('" + CacPermissions.LISUSU + "')")
    @Operation(summary = "Busca de usuarios por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado")
    })
    public UserResponseDTO getUsersById(@PathVariable Long idUsuario) {
        User user = userManagementUseCase.findById(idUsuario);
        // O campo desCanal não está presente, então passamos null ou ajuste conforme
        // necessário
        return new UserResponseDTO(
                user,
                null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar usuario", description = "Cria um novo usuario no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO dto) {
        Long idUsuarioLogado = userResolverHelper.getCurrentUserId();
        User user = userManagementUseCase.createUser(new CreateUserCommand(
                dto.codLogin(),
                dto.nomUsuario(),
                dto.nomEmail(),
                dto.desEndereco(),
                dto.nomDepartamento(),
                dto.nomCargo(),
                dto.nomFuncao(),
                dto.numTelefone(),
                dto.codEmpresa(),
                dto.codClassificacaoPessoa(),
                dto.codCpf(),
                dto.codRg(),
                dto.numDiasSemanasPermitidos(),
                dto.dtJornadaIni(),
                dto.dtJornadaFim(),
                idUsuarioLogado));
        return toResponseDTO(user);
    }

    @PutMapping("/{idUsuario}")
    public UserResponseDTO updateUser(@PathVariable Long idUsuario, @RequestBody UpdateUserDTO dto) {
        User user = userManagementUseCase.updateUser(new UpdateUserCommand(
                idUsuario,
                dto.nomUsuario(),
                dto.nomEmail(),
                dto.codCpf(),
                dto.codRg(),
                null // idUsuarioLogado não existe no UpdateUserDTO, ajuste conforme necessário
        ));
        return toResponseDTO(user);
    }

    // ── Gerenciamento de Status de Usuário ────────────────────────────────────

    @PatchMapping("/{idUsuario}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativa um usuário", description = "Altera o status do usuário para INATIVO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já inativo ou tem sessão ativa"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para desativar usuário")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<StatusChangeResponseDTO> deactivateUser(
            @PathVariable Long idUsuario) {
        Long idUsuarioLogado = userResolverHelper.getCurrentUserId();
        userManagementUseCase.deactivateUser(new StatusChangeCommand(idUsuario, idUsuarioLogado));
        User user = userManagementUseCase.findById(idUsuario);
        StatusChangeResponseDTO response = buildStatusChangeResponse(user, "Usuário desativado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{idUsuario}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativa um usuário", description = "Altera o status do usuário para ATIVO e reseta contador de tentativas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já está ativo"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para ativar usuário")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<StatusChangeResponseDTO> activateUser(
            @PathVariable Long idUsuario) {
        Long idUsuarioLogado = userResolverHelper.getCurrentUserId();
        userManagementUseCase.reactivateUser(new StatusChangeCommand(idUsuario, idUsuarioLogado));
        User user = userManagementUseCase.findById(idUsuario);
        StatusChangeResponseDTO response = buildStatusChangeResponse(user, "Usuário ativado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{idUsuario}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloqueia um usuário", description = "Altera o status do usuário para BLOQUEADO, impedindo seu login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário bloqueado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já está bloqueado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para bloquear usuário")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<StatusChangeResponseDTO> blockUser(
            @PathVariable Long idUsuario) {
        Long idUsuarioLogado = userResolverHelper.getCurrentUserId();
        userManagementUseCase.blockUser(new StatusChangeCommand(idUsuario, idUsuarioLogado));
        User user = userManagementUseCase.findById(idUsuario);
        StatusChangeResponseDTO response = buildStatusChangeResponse(user, "Usuário bloqueado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{idUsuario}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desbloqueia um usuário", description = "Altera o status do usuário bloqueado para ATIVO e reseta contador de tentativas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desbloqueado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário não está bloqueado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para desbloquear usuário")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<StatusChangeResponseDTO> unblockUser(
            @PathVariable Long idUsuario) {
        Long idUsuarioLogado = userResolverHelper.getCurrentUserId();
        userManagementUseCase.unblockUser(new StatusChangeCommand(idUsuario, idUsuarioLogado));
        User user = userManagementUseCase.findById(idUsuario);
        StatusChangeResponseDTO response = buildStatusChangeResponse(user, "Usuário desbloqueado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idUsuario}/reset-password")
    @PreAuthorize("hasAuthority('" + CacPermissions.RESET_PASSWORD + "')")
    @Operation(summary = "Reset administrativo de senha", description = "Permite ao administrador resetar a senha de um usuário. A senha é definida como padrão e o usuário será forçado a trocar no próximo login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha resetada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para resetar senha (requer RESET_PASSWORD)")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AdminResetPasswordResponse> adminResetPassword(
            @PathVariable Long idUsuario) {
        Long idUsuarioLogado = userResolverHelper.getCurrentUserId();
        String tempPassword = userManagementUseCase.adminResetPassword(
            new UserManagementUseCase.AdminResetPasswordCommand(idUsuario, idUsuarioLogado));
        
        User user = userManagementUseCase.findById(idUsuario);
        
        return ResponseEntity.ok(new AdminResetPasswordResponse(
            idUsuario,
            user.getPersonalInfo() != null ? user.getPersonalInfo().getNomUsuario() : null,
            tempPassword,
            "Senha resetada para o valor padrão. Usuário deve alterar no próximo login."));
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user, null);
    }

    private StatusChangeResponseDTO buildStatusChangeResponse(User user, String mensagem) {
        String status = user.getAudit() != null && user.getAudit().getCodStatus() != null 
            ? user.getAudit().getCodStatus().name() 
            : "DESCONHECIDO";
        String statusDescricao = user.getAudit() != null && user.getAudit().getCodStatus() != null 
            ? user.getAudit().getCodStatus().getDescription() 
            : "Status desconhecido";
        
        return new StatusChangeResponseDTO(
            user.getIdUsuario(),
            user.getCredentials() != null ? user.getCredentials().getCodLogin() : null,
            user.getPersonalInfo() != null ? user.getPersonalInfo().getNomUsuario() : null,
            status,
            statusDescricao,
            user.getAudit() != null ? user.getAudit().getDtModi() : null,
            mensagem
        );
    }

    public record UserResponse(String nomUsuario, String nomFuncao, String nomCargo) {

    }

    /**
     * Response DTO para reset administrativo de senha.
     * Retorna a senha temporária que o administrador pode compartilhar com o usuário.
     */
    public record AdminResetPasswordResponse(
            Long idUsuario,
            String nomUsuario,
            String senhaTemporaria,
            String mensagem) {

    }

}

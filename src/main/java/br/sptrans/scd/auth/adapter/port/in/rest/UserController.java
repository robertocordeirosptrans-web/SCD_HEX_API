package br.sptrans.scd.auth.adapter.port.in.rest;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.port.in.rest.dto.UserFilterRequestDTO;
import br.sptrans.scd.auth.adapter.port.in.rest.dto.UserRequestDTO;
import br.sptrans.scd.auth.adapter.port.in.rest.dto.UserResponseDTO;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.CreateUserCommand;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.StatusChangeCommand;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.UpdateUserCommand;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.dto.PageResponse;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista usuários com paginação, filtros e ordenação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public PageResponse<UserResponseDTO> listUsers(
            @ModelAttribute UserFilterRequestDTO filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUsuario") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        String dbSortColumn = mapSortColumn(sortBy);
        List<User> users = userManagementUseCase.listUsersPaginated(filtro, page, size, dbSortColumn, sortDir);
        long totalElements = userManagementUseCase.countUsers(filtro);
        List<UserResponseDTO> content = users.stream().map(this::toResponseDTO).toList();
        return PageResponse.of(content, page, size, totalElements);
    }

    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busca de usuarios por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario não encontrado")
    })
    public UserResponseDTO getUsersById(@PathVariable Long idUsuario) {
        User user = userManagementUseCase.findById(idUsuario);
        // O campo desCanal não está presente, então passamos null ou ajuste conforme necessário
        return new UserResponseDTO(
                user,
                null
        );
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
        User user = userManagementUseCase.createUser(new CreateUserCommand(
                dto.codLogin(),
                dto.nomUsuario(),
                dto.nomEmail(),
                dto.codCpf(),
                dto.codRg(),
                dto.numDiasSemanasPermitidos(),
                dto.dtJornadaIni(),
                dto.dtJornadaFim(),
                dto.idUsuarioLogado()
        ));
        return toResponseDTO(user);
    }

    @PutMapping("/{idUsuario}")
    public UserResponseDTO updateUser(@PathVariable Long idUsuario, @RequestBody UserRequestDTO dto) {
        User user = userManagementUseCase.updateUser(new UpdateUserCommand(
                idUsuario,
                dto.nomUsuario(),
                dto.nomEmail(),
                dto.codCpf(),
                dto.codRg(),
                dto.idUsuarioLogado()
        ));
        return toResponseDTO(user);
    }

    @PatchMapping("/{idUsuario}/deactivate")
    public void deactivateUser(@PathVariable Long idUsuario, @RequestParam Long idUsuarioLogado) {
        userManagementUseCase.deactivateUser(new StatusChangeCommand(idUsuario, idUsuarioLogado));
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user, null);
    }

    private String mapSortColumn(String sortBy) {
        return switch (sortBy) {
            case "codLogin" ->
                "COD_LOGIN";
            case "nomUsuario" ->
                "NOM_USUARIO";
            case "nomEmail" ->
                "NOM_EMAIL";
            case "codStatus" ->
                "COD_STATUS";
            case "dtCriacao" ->
                "DT_CRIACAO";
            case "dtModi" ->
                "DT_MODI";
            case "dtUltimoAcesso" ->
                "DT_ULTIMO_ACESSO";
            default ->
                "ID_USUARIO";
        };
    }

    public record UserResponse(String nomUsuario, String nomFuncao, String nomCargo) {

    }

}

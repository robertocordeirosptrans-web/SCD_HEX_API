package br.sptrans.scd.auth.adapter.port.in.rest;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.port.in.rest.dto.UserRequestDTO;
import br.sptrans.scd.auth.adapter.port.in.rest.dto.UserResponseDTO;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.CreateUserCommand;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.StatusChangeCommand;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.UpdateUserCommand;
import br.sptrans.scd.auth.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserManagementUseCase userManagementUseCase;

	@PostMapping
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
		return new UserResponseDTO(
			user.getIdUsuario(),
			user.getCodLogin(),
			user.getNomUsuario(),
			user.getNomEmail(),
			user.getCodCpf(),
			user.getCodRg(),
			user.getStatus() != null ? user.getStatus().getCode() : null,
			user.getDtCriacao(),
			user.getDtModi(),
			user.getDtExpiraSenha()
		);
	}
}

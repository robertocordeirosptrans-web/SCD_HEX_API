package br.sptrans.scd.auth.application.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.adapter.in.rest.request.CreateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.DeactivateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.ReactivateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.UpdateFunctionalityRequest;
import br.sptrans.scd.auth.application.port.in.FunctionCase;
import br.sptrans.scd.auth.application.port.out.FunctionalityPort;
import br.sptrans.scd.auth.application.usecases.functionality.FunctionalityUseCase;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FunctionalityService implements FunctionCase, FunctionalityUseCase {

	private final FunctionalityPort functionalityPort;

	// ── FunctionCase (porta de entrada) ──────────────────────────────────────

	@Override
	public Functionality createFunctionality(CreateFunctionalityRequest command) {
		Functionality functionality = new Functionality();
		functionality.setId(new FunctionalityKey(
			command.codSistema(), command.codModulo(), command.codRotina(), command.codFuncionalidade()));
		functionality.setNomFuncionalidade(command.nomFuncionalidade());
		functionality.setIdUsuarioManutencao(command.idUsuarioManutencao());
		functionality.setCodStatus("A");
		return functionalityPort.save(functionality);
	}

	@Override
	public Functionality updateFunctionality(UpdateFunctionalityRequest command) {
		Functionality functionality = functionalityPort.findById(new FunctionalityKey(
			command.codSistema(), command.codModulo(), command.codRotina(), command.codFuncionalidade()))
			.orElseThrow(() -> new IllegalArgumentException("Funcionalidade não encontrada"));
		functionality.setNomFuncionalidade(command.nomFuncionalidade());
		functionality.setIdUsuarioManutencao(command.idUsuarioManutencao());
		functionalityPort.update(functionality);
		return functionality;
	}

	@Override
	public Page<Functionality> listFunctionalities(Pageable pageable) {
		return functionalityPort.findAll(pageable.getPageNumber(), pageable.getPageSize());
	}

	@Override
	public Optional<Functionality> findById(FunctionalityKey key) {
		return functionalityPort.findById(key);
	}

	@Override
	public void deactivateFunctionality(DeactivateFunctionalityRequest command) {
		Functionality functionality = functionalityPort.findById(new FunctionalityKey(
			command.codSistema(), command.codModulo(), command.codRotina(), command.codFuncionalidade()))
			.orElseThrow(() -> new IllegalArgumentException("Funcionalidade não encontrada"));
		functionality.setCodStatus("I");
		functionality.setIdUsuarioManutencao(command.idUsuarioManutencao());
		functionalityPort.update(functionality);
	}

	@Override
	public void reactivateFunctionality(ReactivateFunctionalityRequest command) {
		Functionality functionality = functionalityPort.findById(new FunctionalityKey(
			command.codSistema(), command.codModulo(), command.codRotina(), command.codFuncionalidade()))
			.orElseThrow(() -> new IllegalArgumentException("Funcionalidade não encontrada"));
		functionality.setCodStatus("A");
		functionality.setIdUsuarioManutencao(command.idUsuarioManutencao());
		functionalityPort.update(functionality);
	}

	// ── FunctionalityUseCase (porta de saída interna) ─────────────────────────

	@Override
	public Functionality save(Functionality functionality) {
		return functionalityPort.save(functionality);
	}

	@Override
	public void delete(FunctionalityKey id) {
		functionalityPort.delete(id);
	}

	@Override
	public void update(Functionality functionality) {
		functionalityPort.update(functionality);
	}

	@Override
	public Page<Functionality> findAll(int page, int size) {
		return functionalityPort.findAll(page, size);
	}
}

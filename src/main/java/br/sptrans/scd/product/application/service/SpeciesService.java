package br.sptrans.scd.product.application.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.CreateSpeciesCommand;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase.UpdateSpeciesCommand;
import br.sptrans.scd.product.application.port.out.repository.SpeciesPort;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SpeciesService extends AbstractCatalogueService<Species, String, SpeciesManagementUseCase.CreateSpeciesCommand, SpeciesManagementUseCase.UpdateSpeciesCommand> implements SpeciesManagementUseCase {
    private final SpeciesPort speciesRepository;
    private final UserResolverHelper userResolverHelper;

    private Species getByIdOrThrow(String codEspecie) {
        return speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));
    }

    @Override
    public Species create(CreateSpeciesCommand command) {
        if (speciesRepository.existsById(command.codEspecie())) {
            throw new ProductException(ProductErrorType.SPECIES_CODE_ALREADY_EXISTS);
        }
        var usuario = userResolverHelper.resolve(command.idUsuario());
        Species entity = new Species();
        entity.setId(command.codEspecie());
        entity.setDesEspecie(command.desEspecie());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return speciesRepository.save(entity);
    }

    @Override
    public Species update(String codEspecie, UpdateSpeciesCommand command) {
        Species existing = getByIdOrThrow(codEspecie);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesEspecie(command.desEspecie());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return speciesRepository.save(existing);
    }

    @Override
    public void activate(String codEspecie, Long idUsuario) {
        Species species = getByIdOrThrow(codEspecie);
        var usuario = userResolverHelper.resolve(idUsuario);
        species.activate(usuario);
        speciesRepository.save(species);
    }

    @Override
    public void inactivate(String codEspecie, Long idUsuario) {
        Species species = getByIdOrThrow(codEspecie);
        var usuario = userResolverHelper.resolve(idUsuario);
        species.deactivate(usuario);
        speciesRepository.save(species);
    }

    @Override
    public void delete(String codEspecie) {
        speciesRepository.deleteById(codEspecie);
    }

    @Override
    public Optional<Species> findById(String codEspecie) {
        return speciesRepository.findById(codEspecie);
    }

    @Override
    public Page<Species> findAll(String codStatus, Pageable pageable) {
        return speciesRepository.findAll(codStatus, pageable);
    }
}

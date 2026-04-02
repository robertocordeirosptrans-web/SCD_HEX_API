package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.SpeciesRepository;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SpeciesService implements SpeciesManagementUseCase {

    private final SpeciesRepository speciesRepository;
    private final UserPersistencePort userRepository;

    @Override
    public Species createSpecies(CreateSpeciesCommand command) {
        if (speciesRepository.existsById(command.codEspecie())) {
            throw new ProductException(ProductErrorType.SPECIES_CODE_ALREADY_EXISTS);
        }

        User usuario = resolveUser(command.idUsuario());

        Species species = new Species(
                command.codEspecie(),
                command.desEspecie(),
                DomainStatus.INACTIVE.getCode(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                usuario,
                null
        );

        return speciesRepository.save(species);
    }

    @Override
    public Species updateSpecies(String codEspecie, UpdateSpeciesCommand command) {
        Species existing = speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));

        User usuario = resolveUser(command.idUsuario());

        Species updated = new Species(
                existing.getCodEspecie(),
                command.desEspecie(),
                existing.getCodStatus(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                existing.getIdUsuarioCadastro(),
                usuario
        );

        return speciesRepository.save(updated);
    }

    @Override
    public Species findBySpecies(String codEspecie) {
        return speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));
    }

    @Override
    public List<Species> findAllSpecies(String codStatus) {
        return speciesRepository.findAll(codStatus);
    }

    @Override
    public void activateSpecies(String codEspecie, Long idUsuario) {
        Species species = speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));

        if (species.isActive()) {
            throw new ProductException(ProductErrorType.SPECIES_ALREADY_ACTIVE);
        }

        speciesRepository.updateStatus(codEspecie, DomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateSpecies(String codEspecie, Long idUsuario) {
        Species species = speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));

        if (species.isInactive()) {
            throw new ProductException(ProductErrorType.SPECIES_ALREADY_INACTIVE);
        }

        speciesRepository.updateStatus(codEspecie, DomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteSpecies(String codEspecie) {
        if (!speciesRepository.existsById(codEspecie)) {
            throw new ProductException(ProductErrorType.SPECIES_NOT_FOUND);
        }
        speciesRepository.deleteById(codEspecie);
    }

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}

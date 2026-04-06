package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.SpeciesManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.SpeciesRepository;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SpeciesService implements SpeciesManagementUseCase {

    private final SpeciesRepository speciesRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public Species createSpecies(CreateSpeciesCommand command) {
        if (speciesRepository.existsById(command.codEspecie())) {
            throw new ProductException(ProductErrorType.SPECIES_CODE_ALREADY_EXISTS);
        }

        User usuario = userResolverHelper.resolve(command.idUsuario());

        Species species = new Species(
                command.codEspecie(),
                command.desEspecie(),
                ProductDomainStatus.INACTIVE.getCode(),
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

        User usuario = userResolverHelper.resolve(command.idUsuario());

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
    public Page<Species> findAllSpecies(String codStatus, Pageable pageable) {
        return speciesRepository.findAll(codStatus, pageable);
    }

    @Override
    public void activateSpecies(String codEspecie, Long idUsuario) {
        Species species = speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));

        if (species.isActive()) {
            throw new ProductException(ProductErrorType.SPECIES_ALREADY_ACTIVE);
        }

        speciesRepository.updateStatus(codEspecie, ProductDomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateSpecies(String codEspecie, Long idUsuario) {
        Species species = speciesRepository.findById(codEspecie)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));

        if (species.isInactive()) {
            throw new ProductException(ProductErrorType.SPECIES_ALREADY_INACTIVE);
        }

        speciesRepository.updateStatus(codEspecie, ProductDomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteSpecies(String codEspecie) {
        if (!speciesRepository.existsById(codEspecie)) {
            throw new ProductException(ProductErrorType.SPECIES_NOT_FOUND);
        }
        speciesRepository.deleteById(codEspecie);
    }
}

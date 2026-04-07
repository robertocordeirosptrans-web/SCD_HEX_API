package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.TechnologyPort;

import br.sptrans.scd.product.domain.Technology;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyService implements TechnologyManagementUseCase {

    private final TechnologyPort technologyRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public Technology createTechnology(CreateTechnologyCommand command) {
        if (technologyRepository.existsById(command.codTecnologia())) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_CODE_ALREADY_EXISTS);
        }

        User usuario = userResolverHelper.resolve(command.idUsuario());

        Technology technology = new Technology(
                command.codTecnologia(),
                command.desTecnologia(),
                ProductDomainStatus.INACTIVE.getCode(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                usuario,
                null
        );

        return technologyRepository.save(technology);
    }

    @Override
    public Technology updateTechnology(String codTecnologia, UpdateTechnologyCommand command) {
        Technology existing = technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));

        User usuario = userResolverHelper.resolve(command.idUsuario());

        Technology updated = new Technology(
                existing.getCodTecnologia(),
                command.desTecnologia(),
                existing.getCodStatus(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                existing.getIdUsuarioCadastro(),
                usuario
        );

        return technologyRepository.save(updated);
    }

    @Override
    public Technology findByTechnology(String codTecnologia) {
        return technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));
    }

    @Override
    public Page<Technology> findAllTechnologies(String codStatus, Pageable pageable) {
        return technologyRepository.findAll(codStatus, pageable);
    }

    @Override
    public void activateTechnology(String codTecnologia, Long idUsuario) {
        Technology technology = technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));

        if (technology.isActive()) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_ALREADY_ACTIVE);
        }

        technologyRepository.updateStatus(codTecnologia, ProductDomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateTechnology(String codTecnologia, Long idUsuario) {
        Technology technology = technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));

        if (technology.isInactive()) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_ALREADY_INACTIVE);
        }

        technologyRepository.updateStatus(codTecnologia, ProductDomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteTechnology(String codTecnologia) {
        if (!technologyRepository.existsById(codTecnologia)) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND);
        }
        technologyRepository.deleteById(codTecnologia);
    }
}

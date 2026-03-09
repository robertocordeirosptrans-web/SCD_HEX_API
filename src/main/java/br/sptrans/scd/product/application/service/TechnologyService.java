package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase;
import br.sptrans.scd.product.application.port.out.TechnologyRepository;
import br.sptrans.scd.product.domain.Technology;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyService implements TechnologyManagementUseCase {

    private final TechnologyRepository technologyRepository;
    private final UserRepository userRepository;

    @Override
    public Technology createTechnology(CreateTechnologyCommand command) {
        if (technologyRepository.existsById(command.codTecnologia())) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_CODE_ALREADY_EXISTS);
        }

        User usuario = resolveUser(command.idUsuario());

        Technology technology = new Technology(
                command.codTecnologia(),
                command.desTecnologia(),
                DomainStatus.INACTIVE.getCode(),
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

        User usuario = resolveUser(command.idUsuario());

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
    public List<Technology> findAllTechnologies(String codStatus) {
        return technologyRepository.findAll(codStatus);
    }

    @Override
    public void activateTechnology(String codTecnologia, Long idUsuario) {
        Technology technology = technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));

        if (technology.isActive()) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_ALREADY_ACTIVE);
        }

        technologyRepository.updateStatus(codTecnologia, DomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateTechnology(String codTecnologia, Long idUsuario) {
        Technology technology = technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));

        if (technology.isInactive()) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_ALREADY_INACTIVE);
        }

        technologyRepository.updateStatus(codTecnologia, DomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteTechnology(String codTecnologia) {
        if (!technologyRepository.existsById(codTecnologia)) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND);
        }
        technologyRepository.deleteById(codTecnologia);
    }

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}

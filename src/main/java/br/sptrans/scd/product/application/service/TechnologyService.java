package br.sptrans.scd.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
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
public class TechnologyService extends AbstractCatalogueService<Technology, String> {
    private final TechnologyPort technologyRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public Technology create(Technology entity, Long idUsuario) {
        if (technologyRepository.existsById(entity.getId())) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_CODE_ALREADY_EXISTS);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return technologyRepository.save(entity);
    }

    @Override
    public Technology update(String id, Technology entity, Long idUsuario) {
        Technology existing = technologyRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));
        User usuario = userResolverHelper.resolve(idUsuario);
        existing.setDesTecnologia(entity.getDesTecnologia() != null ? entity.getDesTecnologia() : existing.getDesTecnologia());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return technologyRepository.save(existing);
    }

    @Override
    public Optional<Technology> findById(String id) {
        return technologyRepository.findById(id);
    }

    @Override
    public List<Technology> findAll(Long idUsuario) {
        userResolverHelper.resolve(idUsuario);
        return technologyRepository.findAll(ProductDomainStatus.ACTIVE.getCode(), Pageable.unpaged()).getContent();
    }

    @Override
    public void activate(String id, Long idUsuario) {
        Technology technology = technologyRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));
        if (technology.isActive()) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_ALREADY_ACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        technology.activate(usuario);
        technologyRepository.save(technology);
    }

    @Override
    public void inactivate(String id, Long idUsuario) {
        Technology technology = technologyRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));
        if (technology.isInactive()) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_ALREADY_INACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        technology.deactivate(usuario);
        technologyRepository.save(technology);
    }
}

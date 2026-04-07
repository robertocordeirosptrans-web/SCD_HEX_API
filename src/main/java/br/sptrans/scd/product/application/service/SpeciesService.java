package br.sptrans.scd.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.out.repository.SpeciesPort;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SpeciesService extends AbstractCatalogueService<Species, String> {
    private final SpeciesPort speciesRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public Species create(Species entity, Long idUsuario) {
        if (speciesRepository.existsById(entity.getId())) {
            throw new ProductException(ProductErrorType.SPECIES_CODE_ALREADY_EXISTS);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return speciesRepository.save(entity);
    }

    @Override
    public Species update(String id, Species entity, Long idUsuario) {
        Species existing = speciesRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));
        User usuario = userResolverHelper.resolve(idUsuario);
        existing.setDesEspecie(entity.getDesEspecie() != null ? entity.getDesEspecie() : existing.getDesEspecie());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return speciesRepository.save(existing);
    }

    @Override
    public Optional<Species> findById(String id) {
        return speciesRepository.findById(id);
    }

    @Override
    public List<Species> findAll(Long idUsuario) {
        userResolverHelper.resolve(idUsuario);
        return speciesRepository.findAll(ProductDomainStatus.ACTIVE.getCode(), Pageable.unpaged()).getContent();
    }

    @Override
    public void activate(String id, Long idUsuario) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));
        if (species.isActive()) {
            throw new ProductException(ProductErrorType.SPECIES_ALREADY_ACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        species.activate(usuario);
        speciesRepository.save(species);
    }

    @Override
    public void inactivate(String id, Long idUsuario) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.SPECIES_NOT_FOUND));
        if (species.isInactive()) {
            throw new ProductException(ProductErrorType.SPECIES_ALREADY_INACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        species.deactivate(usuario);
        speciesRepository.save(species);
    }
}

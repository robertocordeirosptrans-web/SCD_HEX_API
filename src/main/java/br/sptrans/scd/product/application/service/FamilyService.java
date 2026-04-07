package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.out.repository.FamilyPort;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FamilyService extends AbstractCatalogueService<Family, String> {
    private final FamilyPort familyRepository;
    private final UserResolverHelper userResolverHelper;

    public Family create(Family entity, Long idUsuario) {
        if (familyRepository.existsById(entity.getId())) {
            throw new ProductException(ProductErrorType.FAMILY_CODE_ALREADY_EXISTS);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(LocalDateTime.now());
        return familyRepository.save(entity);
    }

    public Family update(String id, Family entity, Long idUsuario) {
        Family existing = familyRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));
        User usuario = userResolverHelper.resolve(idUsuario);
        existing.setDesFamilia(entity.getDesFamilia() != null ? entity.getDesFamilia() : existing.getDesFamilia());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(LocalDateTime.now());
        return familyRepository.save(existing);
    }

    public Optional<Family> findById(String id) {

        return familyRepository.findById(id);
    }

    public List<Family> findAll(Long idUsuario) {
        userResolverHelper.resolve(idUsuario);
        return familyRepository.findAll(ProductDomainStatus.ACTIVE.getCode(), Pageable.unpaged()).getContent();
    }

    public void activate(String id, Long idUsuario) {
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));
        if (family.isActive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_ACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        family.activate(usuario);
        familyRepository.save(family);
    }

    public void inactivate(String id, Long idUsuario) {
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));
        if (family.isInactive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_INACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        family.deactivate(usuario);
        familyRepository.save(family);
    }
}

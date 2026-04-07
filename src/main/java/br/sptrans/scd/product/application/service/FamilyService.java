package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase;
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

    @Override
    public Family createFamily(CreateFamilyCommand command) {
        if (familyRepository.existsById(command.codFamilia())) {
            throw new ProductException(ProductErrorType.FAMILY_CODE_ALREADY_EXISTS);
        }

        User usuario = userResolverHelper.resolve(command.idUsuario());

        Family family = new Family(
                command.codFamilia(),
                command.desFamilia(),
                ProductDomainStatus.INACTIVE.getCode(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                usuario,
                null
        );

        return familyRepository.save(family);
    }

    @Override
    public Family updateFamily(String codFamilia, UpdateFamilyCommand command) {
        Family existing = familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));

        User usuario = userResolverHelper.resolve(command.idUsuario());

        existing.update(command.desFamilia(), usuario);
        return familyRepository.save(existing);
    }

    @Override
    public Family findByFamily(String codFamilia) {
        return familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));
    }

    @Override
    public Page<Family> findAllFamilies(String codStatus, Pageable pageable) {
        return familyRepository.findAll(codStatus, pageable);
    }

    @Override
    public void activateFamily(String codFamilia, Long idUsuario) {
        Family family = familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));

        if (family.isActive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_ACTIVE);
        }

        User usuario = userResolverHelper.resolve(idUsuario);
        family.activate(usuario);
        familyRepository.save(family);
    }

    @Override
    public void inactivateFamily(String codFamilia, Long idUsuario) {
        Family family = familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));

        if (family.isInactive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_INACTIVE);
        }

        User usuario = userResolverHelper.resolve(idUsuario);
        family.deactivate(usuario);
        familyRepository.save(family);
    }

    @Override
    public void deleteFamily(String codFamilia) {
        if (!familyRepository.existsById(codFamilia)) {
            throw new ProductException(ProductErrorType.FAMILY_NOT_FOUND);
        }
        familyRepository.deleteById(codFamilia);
    }
}

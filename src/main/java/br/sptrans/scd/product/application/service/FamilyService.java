package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.FamilyRepository;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FamilyService implements FamilyManagementUseCase {

    private final FamilyRepository familyRepository;
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

        Family updated = new Family(
                existing.getCodFamilia(),
                command.desFamilia(),
                existing.getCodStatus(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                existing.getIdUsuarioCadastro(),
                usuario
        );

        return familyRepository.save(updated);
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

        familyRepository.updateStatus(codFamilia, ProductDomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateFamily(String codFamilia, Long idUsuario) {
        Family family = familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));

        if (family.isInactive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_INACTIVE);
        }

        familyRepository.updateStatus(codFamilia, ProductDomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteFamily(String codFamilia) {
        if (!familyRepository.existsById(codFamilia)) {
            throw new ProductException(ProductErrorType.FAMILY_NOT_FOUND);
        }
        familyRepository.deleteById(codFamilia);
    }
}

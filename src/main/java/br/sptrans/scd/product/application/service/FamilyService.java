package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase;
import br.sptrans.scd.product.application.port.out.FamilyRepository;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FamilyService implements FamilyManagementUseCase {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    @Override
    public Family createFamily(CreateFamilyCommand command) {
        if (familyRepository.existsById(command.codFamilia())) {
            throw new ProductException(ProductErrorType.FAMILY_CODE_ALREADY_EXISTS);
        }

        User usuario = resolveUser(command.idUsuario());

        Family family = new Family(
                command.codFamilia(),
                command.desFamilia(),
                DomainStatus.INACTIVE.getCode(),
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

        User usuario = resolveUser(command.idUsuario());

        Family updated = new Family(
                existing.getCodFamilia(),
                command.desFamilia(),
                existing.getStFamilias(),
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
    public List<Family> findAllFamilies(String codStatus) {
        return familyRepository.findAll(codStatus);
    }

    @Override
    public void activateFamily(String codFamilia, Long idUsuario) {
        Family family = familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));

        if (family.isActive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_ACTIVE);
        }

        familyRepository.updateStatus(codFamilia, DomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateFamily(String codFamilia, Long idUsuario) {
        Family family = familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(ProductErrorType.FAMILY_NOT_FOUND));

        if (family.isInactive()) {
            throw new ProductException(ProductErrorType.FAMILY_ALREADY_INACTIVE);
        }

        familyRepository.updateStatus(codFamilia, DomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteFamily(String codFamilia) {
        if (!familyRepository.existsById(codFamilia)) {
            throw new ProductException(ProductErrorType.FAMILY_NOT_FOUND);
        }
        familyRepository.deleteById(codFamilia);
    }

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}

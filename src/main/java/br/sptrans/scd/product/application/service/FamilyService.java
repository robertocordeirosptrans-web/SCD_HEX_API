
package br.sptrans.scd.product.application.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.product.application.port.in.FamilyManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.FamilyPort;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;

import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class FamilyService extends
        AbstractCatalogueService<Family, String, FamilyManagementUseCase.CreateFamilyCommand, FamilyManagementUseCase.UpdateFamilyCommand>
        implements FamilyManagementUseCase {
    private final FamilyPort familyRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public Family create(CreateFamilyCommand command) {
        if (familyRepository.existsById(command.codFamilia())) {
            throw new RuntimeException("Família já existe");
        }
        var usuario = userResolverHelper.resolve(command.idUsuario());
        Family entity = new Family();
        entity.setId(command.codFamilia());
        entity.setDesFamilia(command.desFamilia());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return familyRepository.save(entity);
    }

    @Override
    public Family update(String codFamilia, UpdateFamilyCommand command) {
        Family existing = getByIdOrThrow(codFamilia);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesFamilia(command.desFamilia());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return familyRepository.save(existing);
    }

    @Override
    public void activate(String codFamilia, Long idUsuario) {
        Family family = getByIdOrThrow(codFamilia);
        var usuario = userResolverHelper.resolve(idUsuario);
        family.activate(usuario);
        familyRepository.save(family);
    }

    @Override
    public void inactivate(String codFamilia, Long idUsuario) {
        Family family = getByIdOrThrow(codFamilia);
        var usuario = userResolverHelper.resolve(idUsuario);
        family.deactivate(usuario);
        familyRepository.save(family);
    }

    @Override
    public void delete(String codFamilia) {
        familyRepository.deleteById(codFamilia);
    }

    private Family getByIdOrThrow(String codFamilia) {
        return familyRepository.findById(codFamilia)
                .orElseThrow(() -> new ProductException(
                        ProductErrorType.FAMILY_NOT_FOUND));
    }

    @Override
    public Optional<Family> findById(String codFamilia) {
        return familyRepository.findById(codFamilia);
    }

    @Override
    public Page<Family> findAll(String codStatus, Pageable pageable) {
        return familyRepository.findAll(codStatus, pageable);
    }
}

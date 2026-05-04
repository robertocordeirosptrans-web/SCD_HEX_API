
package br.sptrans.scd.product.application.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_FAMILY");

    @Override
    public Family create(CreateFamilyCommand command) {
        var usuario = userResolverHelper.resolve(command.idUsuario());
        
        // Gerar código automaticamente se estiver vazio ou for "0"
        String codFamilia = command.codFamilia();
        if (codFamilia == null || codFamilia.trim().isEmpty() || "0".equals(codFamilia)) {
            Long maxCode = familyRepository.findMaxNumericCode();
            codFamilia = String.valueOf(maxCode + 1);
        } else if (familyRepository.existsById(codFamilia)) {
            throw new ProductException(ProductErrorType.FAMILY_CODE_ALREADY_EXISTS);
        }
        
        Family entity = new Family();
        entity.setId(codFamilia);
        entity.setDesFamilia(command.desFamilia());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} criou Família {} - {}", usuario.getCodLogin(), entity.getId(), entity.getDesFamilia());
        return familyRepository.save(entity);
    }

    @Override
    public Family update(String codFamilia, UpdateFamilyCommand command) {
        Family existing = getByIdOrThrow(codFamilia);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesFamilia(command.desFamilia());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} atualizou Família {} - {}", usuario.getCodLogin(), existing.getId(), existing.getDesFamilia());
        return familyRepository.save(existing);
    }

    @Override
    public void activate(String codFamilia, Long idUsuario) {
        Family family = getByIdOrThrow(codFamilia);
        var usuario = userResolverHelper.resolve(idUsuario);
        family.activate(usuario);
        familyRepository.save(family);

        auditLogger.info("[AUDIT] Usuário {} ativou Família {} - {}", usuario.getCodLogin(), family.getId(), family.getDesFamilia());
    }

    @Override
    public void inactivate(String codFamilia, Long idUsuario) {
        Family family = getByIdOrThrow(codFamilia);
        var usuario = userResolverHelper.resolve(idUsuario);
        family.deactivate(usuario);
        familyRepository.save(family);

        auditLogger.info("[AUDIT] Usuário {} inativou Família {} - {}", usuario.getCodLogin(), family.getId(), family.getDesFamilia());
    }

    @Override
    public void delete(String codFamilia) {
        familyRepository.deleteById(codFamilia);
        auditLogger.info("[AUDIT] Família deletada: {}", codFamilia);
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

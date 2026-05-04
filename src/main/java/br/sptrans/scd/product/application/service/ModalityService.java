package br.sptrans.scd.product.application.service;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.ModalityPort;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ModalityService extends AbstractCatalogueService<Modality, String, ModalityManagementUseCase.CreateModalityCommand, ModalityManagementUseCase.UpdateModalityCommand> implements ModalityManagementUseCase {
    private final ModalityPort modalityRepository;
    private final UserResolverHelper userResolverHelper;

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_MODALITY");

    private Modality getByIdOrThrow(String codModalidade) {
        return modalityRepository.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
    }

    @Override
    public Modality create(CreateModalityCommand command) {
        var usuario = userResolverHelper.resolve(command.idUsuario());
        
        // Gerar código automaticamente se estiver vazio ou for "0"
        String codModalidade = command.codModalidade();
        if (codModalidade == null || codModalidade.trim().isEmpty() || "0".equals(codModalidade)) {
            Long maxCode = modalityRepository.findMaxNumericCode();
            codModalidade = String.valueOf(maxCode + 1);
        } else if (modalityRepository.existsById(codModalidade)) {
            throw new ProductException(ProductErrorType.MODALITY_CODE_ALREADY_EXISTS);
        }
        
        Modality entity = new Modality();
        entity.setId(codModalidade);
        entity.setDesModalidade(command.desModalidade());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} criou Modalidade {} - {}", usuario.getCodLogin(), entity.getId(), entity.getDesModalidade());
        return modalityRepository.save(entity);
    }

    @Override
    public Modality update(String codModalidade, UpdateModalityCommand command) {
        Modality existing = getByIdOrThrow(codModalidade);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesModalidade(command.desModalidade());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} atualizou Modalidade {} - {}", usuario.getCodLogin(), existing.getId(), existing.getDesModalidade());
        return modalityRepository.save(existing);
    }

    @Override
    public void activate(String codModalidade, Long idUsuario) {
        Modality modality = getByIdOrThrow(codModalidade);
        var usuario = userResolverHelper.resolve(idUsuario);
        modality.activate(usuario);
        modalityRepository.save(modality);

        auditLogger.info("[AUDIT] Usuário {} ativou Modalidade {} - {}", usuario.getCodLogin(), modality.getId(), modality.getDesModalidade());
    }

    @Override
    public void inactivate(String codModalidade, Long idUsuario) {
        Modality modality = getByIdOrThrow(codModalidade);
        var usuario = userResolverHelper.resolve(idUsuario);
        modality.deactivate(usuario);
        modalityRepository.save(modality);

        auditLogger.info("[AUDIT] Usuário {} inativou Modalidade {} - {}", usuario.getCodLogin(), modality.getId(), modality.getDesModalidade());
    }

    @Override
    public void delete(String codModalidade) {
        modalityRepository.deleteById(codModalidade);
        auditLogger.info("[AUDIT] Modalidade deletada: {}", codModalidade);
    }

    @Override
    public Optional<Modality> findById(String codModalidade) {
        return modalityRepository.findById(codModalidade);
    }

    @Override
    public Page<Modality> findAll(String codStatus, Pageable pageable) {
        return modalityRepository.findAll(codStatus, pageable);
    }
}

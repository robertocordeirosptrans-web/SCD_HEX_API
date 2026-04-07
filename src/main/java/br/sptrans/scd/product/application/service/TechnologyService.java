package br.sptrans.scd.product.application.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.product.application.port.in.TechnologyManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.TechnologyPort;
import br.sptrans.scd.product.domain.Technology;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyService extends
        AbstractCatalogueService<Technology, String, TechnologyManagementUseCase.CreateTechnologyCommand, TechnologyManagementUseCase.UpdateTechnologyCommand>
        implements TechnologyManagementUseCase {
    private final TechnologyPort technologyRepository;
    private final UserResolverHelper userResolverHelper;

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_TECHNOLOGY");

    private Technology getByIdOrThrow(String codTecnologia) {
        return technologyRepository.findById(codTecnologia)
                .orElseThrow(() -> new ProductException(ProductErrorType.TECHNOLOGY_NOT_FOUND));
    }

    @Override
    public Technology create(CreateTechnologyCommand command) {
        if (technologyRepository.existsById(command.codTecnologia())) {
            throw new ProductException(ProductErrorType.TECHNOLOGY_CODE_ALREADY_EXISTS);
        }
        var usuario = userResolverHelper.resolve(command.idUsuario());
        Technology entity = new Technology();
        entity.setId(command.codTecnologia());
        entity.setDesTecnologia(command.desTecnologia());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} criou Tecnologia {} - {}", usuario.getCodLogin(), entity.getId(), entity.getDesTecnologia());
        return technologyRepository.save(entity);
    }

    @Override
    public Technology update(String codTecnologia, UpdateTechnologyCommand command) {
        Technology existing = getByIdOrThrow(codTecnologia);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesTecnologia(command.desTecnologia());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());

        auditLogger.info("[AUDIT] Usuário {} atualizou Tecnologia {} - {}", usuario.getCodLogin(), existing.getId(), existing.getDesTecnologia());
        return technologyRepository.save(existing);
    }

    @Override
    public void activate(String codTecnologia, Long idUsuario) {
        Technology technology = getByIdOrThrow(codTecnologia);
        var usuario = userResolverHelper.resolve(idUsuario);
        technology.activate(usuario);
        technologyRepository.save(technology);

        auditLogger.info("[AUDIT] Usuário {} ativou Tecnologia {} - {}", usuario.getCodLogin(), technology.getId(), technology.getDesTecnologia());
    }

    @Override
    public void inactivate(String codTecnologia, Long idUsuario) {
        Technology technology = getByIdOrThrow(codTecnologia);
        var usuario = userResolverHelper.resolve(idUsuario);
        technology.deactivate(usuario);
        technologyRepository.save(technology);

        auditLogger.info("[AUDIT] Usuário {} inativou Tecnologia {} - {}", usuario.getCodLogin(), technology.getId(), technology.getDesTecnologia());
    }

    @Override
    public void delete(String codTecnologia) {
        technologyRepository.deleteById(codTecnologia);
        auditLogger.info("[AUDIT] Tecnologia deletada: {}", codTecnologia);
    }

    @Override
    public Optional<Technology> findById(String codTecnologia) {
        return technologyRepository.findById(codTecnologia);
    }

    @Override
    public org.springframework.data.domain.Page<Technology> findAll(String codStatus,
            org.springframework.data.domain.Pageable pageable) {
        return technologyRepository.findAll(codStatus, pageable);
    }
}

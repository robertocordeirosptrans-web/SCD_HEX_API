package br.sptrans.scd.product.application.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase.CreateModalityCommand;
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

    private Modality getByIdOrThrow(String codModalidade) {
        return modalityRepository.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
    }

    @Override
    public Modality create(CreateModalityCommand command) {
        if (modalityRepository.existsById(command.codModalidade())) {
            throw new ProductException(ProductErrorType.MODALITY_CODE_ALREADY_EXISTS);
        }
        var usuario = userResolverHelper.resolve(command.idUsuario());
        Modality entity = new Modality();
        entity.setId(command.codModalidade());
        entity.setDesModalidade(command.desModalidade());
        entity.setActive(true);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return modalityRepository.save(entity);
    }

    @Override
    public Modality update(String codModalidade, UpdateModalityCommand command) {
        Modality existing = getByIdOrThrow(codModalidade);
        var usuario = userResolverHelper.resolve(command.idUsuario());
        existing.setDesModalidade(command.desModalidade());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return modalityRepository.save(existing);
    }

    @Override
    public void activate(String codModalidade, Long idUsuario) {
        Modality modality = getByIdOrThrow(codModalidade);
        var usuario = userResolverHelper.resolve(idUsuario);
        modality.activate(usuario);
        modalityRepository.save(modality);
    }

    @Override
    public void inactivate(String codModalidade, Long idUsuario) {
        Modality modality = getByIdOrThrow(codModalidade);
        var usuario = userResolverHelper.resolve(idUsuario);
        modality.deactivate(usuario);
        modalityRepository.save(modality);
    }

    @Override
    public void delete(String codModalidade) {
        modalityRepository.deleteById(codModalidade);
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

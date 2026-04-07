package br.sptrans.scd.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.out.repository.ModalityPort;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ModalityService extends AbstractCatalogueService<Modality, String> {
    private final ModalityPort modalityRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public Modality create(Modality entity, Long idUsuario) {
        if (modalityRepository.existsById(entity.getId())) {
            throw new ProductException(ProductErrorType.MODALITY_CODE_ALREADY_EXISTS);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        entity.setIdUsuarioCadastro(usuario);
        entity.setDtCadastro(java.time.LocalDateTime.now());
        return modalityRepository.save(entity);
    }

    @Override
    public Modality update(String id, Modality entity, Long idUsuario) {
        Modality existing = modalityRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
        User usuario = userResolverHelper.resolve(idUsuario);
        existing.setDesModalidade(entity.getDesModalidade() != null ? entity.getDesModalidade() : existing.getDesModalidade());
        existing.setIdUsuarioManutencao(usuario);
        existing.setDtManutencao(java.time.LocalDateTime.now());
        return modalityRepository.save(existing);
    }

    @Override
    public Optional<Modality> findById(String id) {
        return modalityRepository.findById(id);
    }

    @Override
    public List<Modality> findAll(Long idUsuario) {
        userResolverHelper.resolve(idUsuario);
        return modalityRepository.findAll(ProductDomainStatus.ACTIVE.getCode(), Pageable.unpaged()).getContent();
    }

    @Override
    public void activate(String id, Long idUsuario) {
        Modality modality = modalityRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
        if (modality.isActive()) {
            throw new ProductException(ProductErrorType.MODALITY_ALREADY_ACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        modality.activate(usuario);
        modalityRepository.save(modality);
    }

    @Override
    public void inactivate(String id, Long idUsuario) {
        Modality modality = modalityRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
        if (modality.isInactive()) {
            throw new ProductException(ProductErrorType.MODALITY_ALREADY_INACTIVE);
        }
        User usuario = userResolverHelper.resolve(idUsuario);
        modality.deactivate(usuario);
        modalityRepository.save(modality);
    }
}

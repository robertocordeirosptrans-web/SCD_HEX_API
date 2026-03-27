package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.ModalityManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.ModalityRepository;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ModalityService implements ModalityManagementUseCase {

    private final ModalityRepository modalityRepository;
    private final UserRepository userRepository;

    @Override
    public Modality createModality(CreateModalityCommand command) {
        if (modalityRepository.existsById(command.codModalidade())) {
            throw new ProductException(ProductErrorType.MODALITY_CODE_ALREADY_EXISTS);
        }

        User usuario = resolveUser(command.idUsuario());

        Modality modality = new Modality(
                command.codModalidade(),
                command.desModalidade(),
                DomainStatus.INACTIVE.getCode(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                usuario,
                null
        );

        return modalityRepository.save(modality);
    }

    @Override
    public Modality updateModality(String codModalidade, UpdateModalityCommand command) {
        Modality existing = modalityRepository.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));

        User usuario = resolveUser(command.idUsuario());

        Modality updated = new Modality(
                existing.getCodModalidade(),
                command.desModalidade(),
                existing.getCodStatus(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                existing.getIdUsuarioCadastro(),
                usuario
        );

        return modalityRepository.save(updated);
    }

    @Override
    public Modality findByModality(String codModalidade) {
        return modalityRepository.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));
    }

    @Override
    public List<Modality> findAllModalities(String codStatus) {
        return modalityRepository.findAll(codStatus);
    }

    @Override
    public void activateModality(String codModalidade, Long idUsuario) {
        Modality modality = modalityRepository.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));

        if (modality.isActive()) {
            throw new ProductException(ProductErrorType.MODALITY_ALREADY_ACTIVE);
        }

        modalityRepository.updateStatus(codModalidade, DomainStatus.ACTIVE.getCode(), idUsuario);
    }

    @Override
    public void inactivateModality(String codModalidade, Long idUsuario) {
        Modality modality = modalityRepository.findById(codModalidade)
                .orElseThrow(() -> new ProductException(ProductErrorType.MODALITY_NOT_FOUND));

        if (modality.isInactive()) {
            throw new ProductException(ProductErrorType.MODALITY_ALREADY_INACTIVE);
        }

        modalityRepository.updateStatus(codModalidade, DomainStatus.INACTIVE.getCode(), idUsuario);
    }

    @Override
    public void deleteModality(String codModalidade) {
        if (!modalityRepository.existsById(codModalidade)) {
            throw new ProductException(ProductErrorType.MODALITY_NOT_FOUND);
        }
        modalityRepository.deleteById(codModalidade);
    }

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}

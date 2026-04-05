package br.sptrans.scd.auth.application.usecases.user;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserCommandPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;


import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.cache.InvalidateUserCache;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Atualizar Dados Cadastrais de Usuário
 * 
 * Responsável por: - Atualizar dados cadastrais (nome, e-mail, CPF, RG,
 * jornada) - Manter COD_LOGIN imutável após criação - Registrar data/hora de
 * modificação
 * 
 * Portos utilizados: - Output Port: UserReader — buscar usuário existente -
 * Output Port: UserWriter — persistir alterações
 */
@Component
@Transactional
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserUseCase.class);

    private final UserQueryPort userReader;
    private final UserCommandPort userWriter;

    /**
     * Atualiza dados cadastrais do usuário.
     * 
     * Regras de negócio: - COD_LOGIN é imutável — qualquer tentativa de
     * alteração é ignorada - Apenas dados como nome, e-mail, CPF, RG podem ser
     * alterados - Atualiza data de modificação
     * 
     * @param command contém os dados a atualizar
     * @return usuário com dados atualizados
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    @InvalidateUserCache
    public User updateUser(UserManagementUseCase.UpdateUserCommand command) {
        log.info("Atualizando usuário ID: {}", command.idUsuario());
        
        // Busca usuário existente
        User user = userReader.findById(command.idUsuario())
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. ID: {}", command.idUsuario());
                    return new ResourceNotFoundException("Usuário", "id", command.idUsuario());
                });

        // Note: COD_LOGIN é imutável — não há campo para alterar aqui
        
        // Atualiza dados editáveis
        user.setNomUsuario(command.nomUsuario().trim());
        user.setNomEmail(command.nomEmail().trim());
        user.setCodCpf(command.codCpf());
        user.setCodRg(command.codRg());
        user.setDtModi(LocalDateTime.now());

        // Persiste alterações
        userWriter.update(user);
        log.info("Usuário atualizado com sucesso. ID: {}", user.getIdUsuario());
        
        return user;
    }
}

package br.sptrans.scd.auth.application.usecases.user;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserCommandPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;


import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.shared.cache.InvalidateUserCache;
import br.sptrans.scd.shared.exception.DuplicateResourceException;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Criar Novo Usuário
 * 
 * Responsável por: - Validar unicidade de COD_LOGIN - Gerar senha temporária -
 * Criar usuário com status inicial ATIVO - Forçar troca de senha no primeiro
 * acesso
 * 
 * Portos utilizados: - Output Port: UserReader — verificar duplicidade -
 * Output Port: UserWriter — persistir novo usuário
 */
@Component
@Transactional
@RequiredArgsConstructor
public class CreateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateUserUseCase.class);
    private static final String TEMP_PASSWORD = "SBEReset@#2026";

    private final UserQueryPort userReader;
    private final UserCommandPort userWriter;

    /**
     * Cria novo usuário com senha temporária.
     * 
     * Regras de negócio: - COD_LOGIN único e imutável após criação - Senha
     * inicial gerada pelo sistema; usuário obrigado a trocar no primeiro acesso
     * - DT_EXPIRA_SENHA = now() para forçar mudança imediata - Define jornada
     * de acesso se fornecida
     * 
     * @param command contém dados do novo usuário
     * @return usuário criado
     * @throws DuplicateResourceException se COD_LOGIN já existe
     */
    @InvalidateUserCache
    public User createUser(UserManagementUseCase.CreateUserCommand command) {
        log.info("Criando novo usuário. Login: {}", command.codLogin());
        
        // Valida COD_LOGIN único
        if (userReader.existsByLogin(command.codLogin())) {
            log.warn("Login duplicado: {}", command.codLogin());
            throw new DuplicateResourceException("Login", "codLogin", command.codLogin());
        }

        // Gera hash da senha temporária
        String passwordHash = PasswordHashUtil.hashBcrypt(TEMP_PASSWORD);

        // Cria entidade de usuário
        User user = new User();
        user.setCodLogin(command.codLogin().trim().toLowerCase());
        user.setNomUsuario(command.nomUsuario().trim());
        user.setNomEmail(command.nomEmail().trim());
        user.setCodCpf(command.codCpf());
        user.setCodRg(command.codRg());
        user.setCodSenha(passwordHash);
        user.setSenhaAntiga(null);
        user.setCodStatus(UserStatus.ACTIVE);
        user.setNumTentativasFalha(0);
        user.setNumDiasSemanasPermitidos(command.numDiasSemanasPermitidos());
        user.setDtJornadaIni(command.dtJornadaIni());
        user.setDtJornadaFim(command.dtJornadaFim());
        
        // Força troca de senha no primeiro acesso
        user.setDtExpiraSenha(LocalDateTime.now());
        user.setDtCriacao(LocalDateTime.now());
        user.setDtModi(LocalDateTime.now());

        // Persiste novo usuário
        userWriter.save(user);
        log.info("Usuário criado com sucesso. ID: {}", user.getIdUsuario());
        
        return user;
    }
}

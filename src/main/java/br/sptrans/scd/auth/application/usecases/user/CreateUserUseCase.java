package br.sptrans.scd.auth.application.usecases.user;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.ClassificationPersonPort;
import br.sptrans.scd.auth.application.port.out.UserCommandPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.shared.cache.InvalidateUserCache;
import br.sptrans.scd.shared.exception.DuplicateResourceException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.helper.LogSanitizer;
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
    private final ClassificationPersonPort classificationPersonPort;

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
        log.info("Criando novo usuário. Login: {}", LogSanitizer.maskLogin(command.codLogin()));

        // Valida COD_LOGIN único
        if (userReader.existsByLogin(command.codLogin())) {
            log.warn("Tentativa de criação com login duplicado");
            throw new DuplicateResourceException("Login", "codLogin", command.codLogin());
        }

        // Busca e valida ClassificationPerson
        ClassificationPerson classification = command.codClassificacaoPessoa();

        ClassificationPerson fullClassification = classificationPersonPort
                .findById(classification.getCodClassificacaoPessoa())
                .orElseThrow(() -> {
                    log.warn("Classificação de pessoa não encontrada: {}", classification.getCodClassificacaoPessoa());
                    return new ResourceNotFoundException("ClassificationPerson", "codClassificacaoPessoa",
                            classification.getCodClassificacaoPessoa());
                });

        // Gera hash da senha temporária
        String passwordHash = PasswordHashUtil.hashBcrypt(TEMP_PASSWORD);

        // Cria entidade de usuário
        User user = new User();
        user.setCodLogin(command.codLogin().trim().toLowerCase());
        user.setNomUsuario(command.nomUsuario().trim());
        user.setNomEmail(command.nomEmail().trim());
        user.setDesEndereco(command.desEndereco().trim());
        user.setNomDepartamento(command.nomDepartamento().trim());
        user.setNomCargo(command.nomCargo().trim());
        user.setNomFuncao(command.nomFuncao().trim());
        user.setNumTelefone(command.numTelefone());
        user.setCodEmpresa(command.codEmpresa().trim());
        user.setCodClassificacaoPessoa(fullClassification);
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

package br.sptrans.scd.auth.application.usecases.auth;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.out.AuthenticationPort;
import br.sptrans.scd.auth.application.port.out.AuthorizationPort;
import br.sptrans.scd.auth.application.port.out.GroupUserPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.exception.AccountBlockedException;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.InactiveUserException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.helper.LogSanitizer;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Autenticar Usuário
 * 
 * Responsável por: - Validar credenciais de login - Verificar bloqueios e
 * inatividade - Validar jornada de acesso - Registrar tentativas
 * 
 * Portos utilizados: - Output Port: userPort — buscar usuário por login -
 * Output Port: AuthenticationPort — atualizar tentativas e último acesso
 * - Output Port: authorizationPort — carregar perfis e funcionalidades
 * - Output Port: GroupUserRepository — carregar grupos do usuário
 */
@Component
@Transactional
@RequiredArgsConstructor
public class AuthenticateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateUserUseCase.class);

    private final UserQueryPort userPort;
    private final AuthenticationPort authenticationPort;
    private final AuthorizationPort authorizationPort;
    private final GroupUserPort groupUserRepository;

    /**
     * Autentica um usuário com as credenciais fornecidas.
     * 
     * Regras de negócio: - Bloqueia após 3 tentativas - Conta
     * bloqueada/inativa: exceção com mensagem distinta - Valida jornada de
     * acesso - Sucesso: reseta tentativas, persiste último acesso, retorna
     * usuário com permissões
     * 
     * @param command credenciais de autenticação
     * @return usuário autenticado com contexto
     * @throws AuthenticationFailedException se credenciais inválidas
     * @throws AccountBlockedException se conta bloqueada
     * @throws InactiveUserException se conta inativa
     */
    public User authenticate(AuthUseCase.AuthComand command) {
        log.info("Iniciando autenticação para login: {}", LogSanitizer.maskLogin(command.codLogin()));
        
        // Busca usuário
        User user = userPort.findByCodLogin(command.codLogin())
            .orElseThrow(() -> {
                log.warn("Falha na autenticação — usuário não encontrado");
                return new AuthenticationFailedException("Usuário ou senha inválidos.");
            });
        
        // Valida conta bloqueada
        if (user.isBlocked()) {
            log.warn("Tentativa de acesso em conta bloqueada. ID: {}", user.getIdUsuario());
            throw new AccountBlockedException(
                "Conta bloqueada por excesso de tentativas. Contate o administrador.");
        }
        
        // Valida conta inativa
        if (user.isInactive()) {
            log.warn("Tentativa de acesso em conta inativa. ID: {}", user.getIdUsuario());
            throw new InactiveUserException(
                "Conta inativa. Contate o administrador.");
        }

        // Valida senha
        String hashArmazenado = user.getCodSenha() != null ? user.getCodSenha().trim() : null;

        if (!PasswordHashUtil.verificar(command.senha(), hashArmazenado)) {
            log.warn("Credenciais inválidas. ID: {}, tentativa: {}", user.getIdUsuario(), user.getNumTentativasFalha() + 1);
            user.registrarTentativaFalha();
            
            authenticationPort.atualizarTentativasEStatus(
                    user.getIdUsuario(),
                    user.getNumTentativasFalha(),
                    user.getCodStatus() != null ? user.getCodStatus().getCode() : null);
            
            if (user.isBlocked()) {
                log.warn("Conta bloqueada após excesso de tentativas. ID: {}", user.getIdUsuario());
                throw new AccountBlockedException(
                    "Conta bloqueada após 3 tentativas inválidas. Contate o administrador.");
            }
            throw new AuthenticationFailedException(
                "Usuário ou senha inválidos. Tentativa " + user.getNumTentativasFalha() + " de 3.");
        }

        // Valida jornada de acesso
        if (!user.acessoPermitidoAgora()) {
            log.warn("Acesso fora da jornada permitida. ID: {}", user.getIdUsuario());
            throw new AuthenticationFailedException(
                "Acesso não permitido neste dia/horário conforme sua jornada configurada.");
        }
        
        // Autentica com sucesso
        log.info("Login bem-sucedido. ID: {}", user.getIdUsuario());
        user.resetarTentativas();
        
        authenticationPort.atualizarTentativasEStatus(
            user.getIdUsuario(), 0, 
            user.getCodStatus() != null ? user.getCodStatus().getCode() : null);
        
        authenticationPort.atualizarUltimoAcesso(user.getIdUsuario());
        
        return user;
    }

    /**
     * Carrega o contexto completo do usuário autenticado (perfis, permissões,
     * grupos).
     * 
     * @param codLogin login do usuário
     * @return contexto completo para autorização
     */
    public AuthUseCase.UserContext loadUserContext(String codLogin) {
        User user = userPort.findByCodLogin(codLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "login", codLogin));

        // Carrega perfis efetivos
        Set<String> roles = authorizationPort.carregarPerfisEfetivos(user.getIdUsuario())
                .stream()
                .map(profile -> profile.getCodPerfil())
                .collect(Collectors.toSet());

        // Carrega funcionalidades efetivas
        Set<String> permissions = authorizationPort.carregarFuncionalidadesEfetivas(user.getIdUsuario())
                .stream()
                .map(func -> func.canonicalKey())
                .collect(Collectors.toSet());

        // Carrega grupos ativos
        Set<String> grupos = groupUserRepository
            .findById_IdUsuarioAndCodStatus(user.getIdUsuario(), "A")
                .stream()
                .map(GroupUser::getId)
                .map(id -> id.getCodGrupo())
                .collect(Collectors.toSet());

        return new AuthUseCase.UserContext(
            user.getIdUsuario(), 
            user.getNomUsuario(), 
            roles, 
            permissions, 
            grupos);
    }
}

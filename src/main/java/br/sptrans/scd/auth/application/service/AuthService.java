package br.sptrans.scd.auth.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.application.port.out.AuthenticationRepository;
import br.sptrans.scd.auth.application.port.out.AuthorizationRepository;
import br.sptrans.scd.auth.application.port.out.GatewayEmail;
import br.sptrans.scd.auth.application.port.out.GroupUserRepository;
import br.sptrans.scd.auth.application.port.out.PasswordTokenRepository;
import br.sptrans.scd.auth.application.port.out.UserReader;
import br.sptrans.scd.auth.application.port.out.UserStatusRepository;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.PasswordResetToken;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;
import br.sptrans.scd.shared.exception.AccountBlockedException;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.InactiveUserException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.exception.ValidationException;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${scd.auth.token-ttl-minutos:15}")
    private long tokenTtlMinutos;

    @Value("${scd.auth.senha-expira-dias:90}")
    private long senhaExpiraDias;

    private final UserReader userReader;
    private final UserStatusRepository userStatusRepository;
    private final AuthenticationRepository authenticationRepository;
    private final AuthorizationRepository authorizationRepository;
    private final GroupUserRepository groupUserRepository;
    private final PasswordTokenRepository tokenRepository;
    private final GatewayEmail gatewayEmail;
    private final PasswordValidator passwordValidator;


    // ── Autenticando um usuario ───────────────────────────────────────────────────────────
    @Override
    public User autenticar(AuthComand comando) {
        log.info("Iniciando autenticação para login: {}", comando.codLogin());
        User user = userReader.findByCodLogin(comando.codLogin())
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado: {}", comando.codLogin());
                return new AuthenticationFailedException("Usuário ou senha inválidos.");
            });
        // Conta bloqueada
        if (user.isBlocked()) {
            log.warn("Tentativa de acesso em conta bloqueada. Login: {}", user.getCodLogin());
            throw new AccountBlockedException("Conta bloqueada por excesso de tentativas. Contate o administrador.");
        }
        // Conta inativa
        if (user.isInactive()) {
            log.warn("Tentativa de acesso em conta inativa. Login: {}", user.getCodLogin());
            throw new InactiveUserException("Conta inativa. Contate o administrador.");
        }

        String hashArmazenado = user.getCodSenha() != null ? user.getCodSenha().trim() : null;

        if (!PasswordHashUtil.verificar(comando.senha(), hashArmazenado)) {
            log.warn("Credenciais inválidas para o login: {}", user.getCodLogin());
            user.registrarTentativaFalha();
            authenticationRepository.atualizarTentativasEStatus(
                    user.getIdUsuario(),
                    user.getNumTentativasFalha(),
                    user.getCodStatus() != null ? user.getCodStatus().getCode() : null);
            if (user.isBlocked()) {
                log.warn("Conta bloqueada após excesso de tentativas. Login: {}", user.getCodLogin());
                throw new AccountBlockedException("Conta bloqueada após 3 tentativas inválidas. Contate o administrador.");
            }
            throw new AuthenticationFailedException("Usuário ou senha inválidos. Tentativa " + user.getNumTentativasFalha() + " de 3.");
        }

        // Valida jornada de acesso
        if (!user.acessoPermitidoAgora()) {
            log.warn("Acesso não permitido para usuário: {} (restrição de jornada)", user.getCodLogin());
            throw new AuthenticationFailedException("Acesso não permitido neste dia/horário conforme sua jornada configurada.");
        }
        log.info("Login bem-sucedido para usuário: {}", user.getCodLogin());
        user.resetarTentativas();
        authenticationRepository.atualizarTentativasEStatus(
            user.getIdUsuario(), 0, user.getCodStatus() != null ? user.getCodStatus().getCode() : null);
        authenticationRepository.atualizarUltimoAcesso(user.getIdUsuario());
        return user;
    }

    // ── Recuperação de Senha ───────────────────────────────────────────────────────────
    @Override
    public void recoveryResetPassword(ResetRequestComand comando) {
        User user = userReader.findByNomEmail(comando.email())
            .orElseThrow(() -> new ResourceNotFoundException("E-mail não cadastrado."));

        // Invalida token anterior, se existir
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());

        // Cria novo token com TTL (inicialmente como "não utilizado")
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setIdUsuario(user.getIdUsuario());
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setDtExpiracao(LocalDateTime.now().plusMinutes(tokenTtlMinutos));

        tokenRepository.save(resetToken);

        // Envia e-mail via adaptador SMTP
        gatewayEmail.sendPasswordResetEmail(
            user.getNomEmail(),
            user.getNomUsuario(),
            resetToken.getToken());
    }


    @Override
    public AuthUseCase.UserContext loadUserContext(String codLogin) {
        User user = userReader.findByCodLogin(codLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "login", codLogin));

        // Carrega perfis e funcionalidades para o contexto do usuário
        Set<String> roles = authorizationRepository.carregarPerfisEfetivos(user.getIdUsuario())
                .stream()
                .map(profile -> profile.getCodPerfil())
                .collect(Collectors.toSet());

        Set<String> permissions = authorizationRepository.carregarFuncionalidadesEfetivas(user.getIdUsuario())
                .stream()
                .map(func -> func.canonicalKey())
                .collect(Collectors.toSet());

                // Buscar grupos ativos do usuário
        List<GroupUser> gruposUsuario = groupUserRepository.findById_IdUsuarioAndCodStatus(user.getIdUsuario(), "A");
        Set<String> grupos = gruposUsuario.stream()
                .map(gu -> gu.getId().getCodGrupo())
                .collect(Collectors.toSet());

        

        return new AuthUseCase.UserContext(user.getIdUsuario(), user.getNomUsuario(), roles, permissions, grupos);
    }

    // ── redefinirSenha ───────────────────────────────────────────────────────
    @Override
    public void resetPassword(ResetPasswordComand comando) {
        PasswordResetToken tokenObj = tokenRepository.findByToken(comando.token())
                .orElseThrow(() -> new AuthenticationFailedException("Token inválido ou não encontrado."));

        if (tokenObj.isExpired()) {
            throw new AuthenticationFailedException("Token expirado. Solicite um novo e-mail de recuperação.");
        }

        if (!tokenObj.isValid()) {
            throw new AuthenticationFailedException("Token já utilizado. Solicite um novo e-mail de recuperação.");
        }

        User user = userReader.findById(tokenObj.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", tokenObj.getIdUsuario()));

        // Valida complexidade
        PasswordValidationResult validacao = passwordValidator.validate(comando.novaSenha(), user.getCodSenha());
        if (!validacao.isValid()) {
            throw new ValidationException(validacao.getErrorsAsString());
        }

        // Persiste nova senha via port dedicado (evita carregar/salvar entidade completa)
        String newHash = PasswordHashUtil.hashBcrypt(comando.novaSenha());
        String oldHash = user.getCodSenha();
        userStatusRepository.updatePassword(
                user.getIdUsuario(), newHash, oldHash,
                LocalDateTime.now().plusDays(senhaExpiraDias));

        // Marca token como utilizado e invalida todos os demais
        tokenObj.markAsUsed();
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());
    }

}


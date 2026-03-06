package br.sptrans.scd.auth.application.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.adapter.in.rest.ProviderJwtToken;
import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.AthenticationErrorType;
import br.sptrans.scd.auth.application.port.out.GatewayEmail;
import br.sptrans.scd.auth.application.port.out.PasswordTokenRepository;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.PasswordResetToken;
import br.sptrans.scd.auth.domain.User;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService implements AuthUseCase {

    // Padrões de validação de senha
    private static final Pattern TEM_MAIUSCULA = Pattern.compile(".*[A-Z].*");
    private static final Pattern TEM_MINUSCULA = Pattern.compile(".*[a-z].*");
    private static final Pattern TEM_NUMERO = Pattern.compile(".*\\d.*");
    private static final Pattern TEM_ESPECIAL = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    private static final Pattern TEM_SEQUENCIAL = Pattern.compile(
            ".*(012|123|234|345|456|567|678|789|890"
            + "|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz"
            + "|ABC|BCD|CDE|DEF|EFG|FGH|GHI|HIJ|IJK|JKL|KLM|LMN|MNO|NOP|OPQ|PQR|QRS|RST|STU|TUV|UVW|VWX|WXY|XYZ).*"
    );

    @Value("${scd.auth.token-ttl-minutos:15}")
    private long tokenTtlMinutos;

    private final UserRepository userRepository;
    private final PasswordTokenRepository tokenRepository;
    private final GatewayEmail gatewayEmail;

    public AuthService(
            UserRepository userRepository,
            PasswordTokenRepository tokenRepository,
            GatewayEmail gatewayEmail) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.gatewayEmail = gatewayEmail;
    }

    // ── Autenticando um usuario ───────────────────────────────────────────────────────────
    @Override
    public User autenticar(AuthComand comando) {
        User user = userRepository.findByCodLogin(comando.codLogin())
                .orElseThrow(() -> new AuthenticationException(
                AthenticationErrorType.CREDENCIAIS_INVALIDAS,
                "Usuário ou senha inválidos."));

        // Conta bloqueada
        if (user.isBlocked()) {
            throw new AuthenticationException(
                    AthenticationErrorType.CONTA_BLOQUEADA,
                    "Conta bloqueada por excesso de tentativas. Contate o administrador.");
        }
        // Conta inativa
        if (user.isInactive()) {
            throw new AuthenticationException(
                    AthenticationErrorType.CONTA_INATIVA,
                    "Conta inativa. Contate o administrador.");
        }

        String senhaRecebida = comando.senha();
        boolean isMd5 = senhaRecebida.matches("^[a-fA-F0-9]{32}$");

        if (isMd5) {
            // Valida senha MD5
            if (!senhaRecebida.equalsIgnoreCase(user.getCodSenha())) {
                user.registrarTentativaFalha();
                userRepository.atualizarTentativasEStatus(
                        user.getIdUsuario(),
                        user.getNumTentativasFalha(),
                        user.getStatus() != null ? user.getStatus().getCode() : null);
                if (user.isBlocked()) {
                    throw new AuthenticationException(
                            AthenticationErrorType.CONTA_BLOQUEADA,
                            "Conta bloqueada após 3 tentativas inválidas. Contate o administrador.");
                }
                throw new AuthenticationException(
                        AthenticationErrorType.CREDENCIAIS_INVALIDAS,
                        "Usuário ou senha inválidos. Tentativa " + user.getNumTentativasFalha() + " de 3.");
            }
        } else {
            // Valida JWT recebido como senha
            try {

                ProviderJwtToken jwtProvider = new ProviderJwtToken();
                Claims claims = jwtProvider.validarEExtrairClaims(senhaRecebida);
                String subject = claims.getSubject();
                if (!subject.equalsIgnoreCase(user.getCodLogin())) {
                    throw new AuthenticationException(
                            AthenticationErrorType.CREDENCIAIS_INVALIDAS,
                            "Senha invalida.");
                }
            } catch (Exception e) {
                throw new AuthenticationException(
                        AthenticationErrorType.CREDENCIAIS_INVALIDAS,
                        "Senha invalida.");
            }
        }

        // Valida jornada de acesso
        if (!user.acessoPermitidoAgora()) {
            throw new AuthenticationException(
                    AthenticationErrorType.FORA_DA_JORNADA,
                    "Acesso não permitido neste dia/horário conforme sua jornada configurada.");
        }
        // Login bem-sucedido
        user.resetarTentativas();
        userRepository.atualizarTentativasEStatus(
            user.getIdUsuario(), 0, user.getStatus() != null ? user.getStatus().getCode() : null);
        userRepository.atualizarUltimoAcesso(user.getIdUsuario());
        // Carrega permissões
        // Set<Functionality> permissoes = userRepository
        //         .carregarFuncionalidadesEfetivas(user.getIdUsuario());
        // user.setFuncionalidadesDiretas(permissoes);
        return user;
    }

    // ── Recuperação de Senha ───────────────────────────────────────────────────────────
    @Override
    public void recoveryResetPassword(ResetRequestComand comando) {
        User user = userRepository.findByNomEmail(comando.email())
            .orElseThrow(() -> new AuthenticationException(
            AthenticationErrorType.CREDENCIAIS_INVALIDAS,
            "E-mail não cadastrado."));

        // Invalida token anterior, se existir
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());

        // Cria novo token com TTL
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setIdUsuario(user.getIdUsuario());
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(tokenTtlMinutos));
        resetToken.isUsed();

        tokenRepository.save(resetToken);

        // Envia e-mail via adaptador SMTP
        gatewayEmail.sendPasswordResetEmail(
            user.getNomEmail(),
            user.getNomUsuario(),
            resetToken.getToken());
    }

    @Override
    public Set<Functionality> loadPermissions(Long idUsuario) {
              return userRepository.carregarFuncionalidadesEfetivas(idUsuario);
    }

    // ── redefinirSenha ───────────────────────────────────────────────────────
    @Override
    public void resetPassword(ResetPasswordComand comando) {
        PasswordResetToken tokenObj = tokenRepository.findByToken(comando.token())
                .orElseThrow(() -> new AuthenticationException(
                AthenticationErrorType.TOKEN_INVALIDO,
                "Token inválido ou não encontrado."));

        if (tokenObj.isExpired()) {
            throw new AuthenticationException(
                    AthenticationErrorType.TOKEN_EXPIRADO,
                    "Token expirado. Solicite um novo e-mail de recuperação.");
        }

        if (!tokenObj.isValid()) {
            throw new AuthenticationException(
                    AthenticationErrorType.TOKEN_INVALIDO,
                    "Token já utilizado. Solicite um novo e-mail de recuperação.");
        }

        User user = userRepository.findById(tokenObj.getIdUsuario())
                .orElseThrow(() -> new AuthenticationException(
                AthenticationErrorType.TOKEN_INVALIDO,
                "Usuário associado ao token não encontrado."));

        // Impede reutilização da senha anterior comparando JWTs
        ProviderJwtToken jwtProvider = new ProviderJwtToken();
        String novaSenhaJwt = jwtProvider.gerarToken(user.getIdUsuario(), user.getCodLogin(), Set.of());
        String oldSenhaJwt = user.getOldSenha();

        // Se oldSenhaJwt não for nulo, comparar os hashes dos JWTs
        if (oldSenhaJwt != null && !oldSenhaJwt.isEmpty()) {
            String hashNova = Integer.toHexString(novaSenhaJwt.hashCode());
            String hashOld = Integer.toHexString(oldSenhaJwt.hashCode());
            if (hashNova.equals(hashOld)) {
                throw new AuthenticationException(
                        AthenticationErrorType.SENHA_REUTILIZADA,
                        "A nova senha não pode ser igual à senha anterior.");
            }
        }

        // Valida complexidade
        validarComplexidadeSenha(comando.novaSenha());

        // Persiste nova senha como JWT
        user.setOldSenha(user.getCodSenha());
        user.setCodSenha(novaSenhaJwt);
        userRepository.save(user);

        // Invalida o token
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());
    }

    // ── Validações ───────────────────────────────────────────────────────
    private void validarComplexidadeSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new AuthenticationException(
                    AthenticationErrorType.SENHA_FRACA,
                    "A senha deve ter no mínimo 8 caracteres.");
        }
        if (!TEM_MAIUSCULA.matcher(senha).matches()) {
            throw new AuthenticationException(
                    AthenticationErrorType.SENHA_FRACA,
                    "A senha deve conter ao menos uma letra maiúscula.");
        }
        if (!TEM_MINUSCULA.matcher(senha).matches()) {
            throw new AuthenticationException(
                    AthenticationErrorType.SENHA_FRACA,
                    "A senha deve conter ao menos uma letra minúscula.");
        }
        if (!TEM_NUMERO.matcher(senha).matches()) {
            throw new AuthenticationException(
                    AthenticationErrorType.SENHA_FRACA,
                    "A senha deve conter ao menos um número.");
        }
        if (!TEM_ESPECIAL.matcher(senha).matches()) {
            throw new AuthenticationException(
                    AthenticationErrorType.SENHA_FRACA,
                    "A senha deve conter ao menos um caractere especial (!@#$% etc.).");
        }
        if (TEM_SEQUENCIAL.matcher(senha.toLowerCase()).matches()) {
            throw new AuthenticationException(
                    AthenticationErrorType.SENHA_FRACA,
                    "A senha não pode conter sequências óbvias (abc, 123 etc.).");
        }
    }

}

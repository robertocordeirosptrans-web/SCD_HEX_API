package br.sptrans.scd.auth.application.service;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.AthenticationErrorType;
import br.sptrans.scd.auth.application.port.out.GatewayEmail;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.application.port.out.PasswordTokenRepository;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.PasswordResetToken;
import br.sptrans.scd.auth.domain.User;

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
        // Valida senha MD5
        // String senhaHash = md5(comando.senha());
        // if (!senhaHash.equalsIgnoreCase(user.getCodSenha())) {
        //     user.registrarTentativaFalha();
        //     userRepository.atualizarTentativasEStatus(
        //             user.getIduser(),
        //             user.getNumTentativas(),
        //             user.getCodStatus());
        //     if (usuario.estaBloqueada()) {
        //         throw new AuthenticationException(
        //                 AthenticationErrorType.CONTA_BLOQUEADA,
        //                 "Conta bloqueada após 3 tentativas inválidas. Contate o administrador.");
        //     }
        //     throw new AuthenticationException(
        //             AthenticationErrorType.CREDENCIAIS_INVALIDAS,
        //             "Usuário ou senha inválidos. Tentativa " + usuario.getNumTentativas() + " de 3.");
        // }
        // Valida jornada de acesso
        if (!user.acessoPermitidoAgora()) {
            throw new AuthenticationException(
                    AthenticationErrorType.FORA_DA_JORNADA,
                    "Acesso não permitido neste dia/horário conforme sua jornada configurada.");
        }
        // Login bem-sucedido
        user.resetarTentativas();
        userRepository.atualizarTentativasEStatus(
                user.getIdUsuario(), 0, user.getCodStatus());
        userRepository.atualizarUltimoAcesso(user.getIdUsuario());
        // Carrega permissões
        // Set<Functionality> permissoes = userRepository
        //         .carregarFuncionalidadesEfetivas(user.getIdUsuario());
        // user.setFuncionalidadesDiretas(permissoes);
        return user;
    }

    // ── carregarPermissoes ───────────────────────────────────────────────────
    @Transactional
    public Set<Functionality> carregarPermissoes(Long idUsuario) {
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

        // Impede reutilização da senha anterior
        // String novaSenhaHash = md5(comando.novaSenha());
        // if (novaSenhaHash.equalsIgnoreCase(usuario.getOldSenha())) {
        //     throw new AuthenticationException(
        //             AthenticationErrorType.SENHA_REUTILIZADA,
        //             "A nova senha não pode ser igual à senha anterior.");
        // }

        // Valida complexidade
        validarComplexidadeSenha(comando.novaSenha());

        // Persiste nova senha
        user.setOldSenha(user.getCodSenha());
        user.setCodSenha(novaSenhaHash);
        userRepository.salvar(user);

        // Invalida o token
        tokenRepository.marcarComoUsado(comando.token());
    }

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

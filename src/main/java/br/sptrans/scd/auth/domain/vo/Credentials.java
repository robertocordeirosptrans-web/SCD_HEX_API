package br.sptrans.scd.auth.domain.vo;

import java.time.LocalDateTime;

import br.sptrans.scd.shared.exception.AccountBlockedException;
import lombok.Builder;
import lombok.Value;

/**
 * Value Object imutável: credenciais de autenticação do usuário.
 * Encapsula login, hash da senha, tentativas de falha e expiração.
 */
@Value
@Builder(toBuilder = true)
public class Credentials {

    private static final int MAX_TENTATIVAS_FALHA = 3;

    String codLogin;
    String codSenha;
    String senhaAntiga;
    Integer numTentativasFalha;
    LocalDateTime dtExpiraSenha;

    /**
     * Factory: cria credenciais para um novo usuário com valores padrão.
     */
    public static Credentials novo(String codLogin, String codSenha) {
        return Credentials.builder()
                .codLogin(codLogin.toLowerCase().trim())
                .codSenha(codSenha)
                .senhaAntiga(null)
                .numTentativasFalha(0)
                .dtExpiraSenha(LocalDateTime.now().plusMonths(3))
                .build();
    }

    /**
     * Retorna nova instância com tentativa de falha incrementada.
     * Lança {@link AccountBlockedException} ao atingir o limite.
     */
    public Credentials registrarTentativaFalha() {
        int tentativas = (numTentativasFalha == null ? 0 : numTentativasFalha) + 1;
        if (tentativas >= MAX_TENTATIVAS_FALHA) {
            throw new AccountBlockedException();
        }
        return toBuilder().numTentativasFalha(tentativas).build();
    }

    /**
     * Retorna nova instância com tentativas zeradas.
     */
    public Credentials zerarTentativas() {
        return toBuilder().numTentativasFalha(0).build();
    }

    /**
     * Retorna nova instância com nova senha (e antiga arquivada).
     */
    public Credentials alterarSenha(String novoHash, LocalDateTime novaExpiracao) {
        return toBuilder()
                .senhaAntiga(this.codSenha)
                .codSenha(novoHash)
                .numTentativasFalha(0)
                .dtExpiraSenha(novaExpiracao)
                .build();
    }

    public boolean isSenhaExpirada() {
        return dtExpiraSenha != null && LocalDateTime.now().isAfter(dtExpiraSenha);
    }
}

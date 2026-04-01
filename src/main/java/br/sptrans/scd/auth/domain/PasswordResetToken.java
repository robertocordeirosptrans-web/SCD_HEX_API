package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetToken {

    private Long id;
    private Long idUsuario;
    private String token;                  // UUID único gerado pelo sistema
    private LocalDateTime dtExpiracao;     // now + TTL configurável (padrão: 15 min)
    private boolean usado;                 // true após redefinição bem-sucedida
    private LocalDateTime dtCriacao;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.dtExpiracao);
    }

    public boolean isValid() {
        return !isExpired() && !this.usado;
    }

    public void marcarComoUsado() {
        this.usado = true;
    }

   

}

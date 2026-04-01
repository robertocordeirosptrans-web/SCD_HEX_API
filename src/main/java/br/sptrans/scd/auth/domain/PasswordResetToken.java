package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDtExpiracao() {
        return dtExpiracao;
    }

    public void setDtExpiracao(LocalDateTime dtExpiracao) {
        this.dtExpiracao = dtExpiracao;
    }

    public LocalDateTime getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(LocalDateTime dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

}

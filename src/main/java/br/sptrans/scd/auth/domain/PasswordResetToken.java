package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

public class PasswordResetToken {

    private Long id;
    private Long idUsuario;
    private String token;               // UUID único gerado pelo sistema
    private LocalDateTime expiryDate;   // now + TTL configurável (padrão: 15 min)
    private boolean used;               // true após redefinição bem-sucedida
    private LocalDateTime dtCriacao;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    public boolean isValid() {
        return !isExpired() && !this.used;
    }

    public void isUsed() {
        this.used = true;
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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(LocalDateTime dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

}

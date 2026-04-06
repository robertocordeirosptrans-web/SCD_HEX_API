package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.out.persistence.entity.PasswordTokenEntityJpa;
import br.sptrans.scd.auth.domain.PasswordResetToken;

/**
 * Mapper — Conversão entre PasswordResetToken (domínio) e PasswordTokenEntityJpa (JPA)
 */
public class PasswordTokenMapper {

    public static PasswordResetToken toDomain(PasswordTokenEntityJpa entity) {
        if (entity == null) {
            return null;
        }
        
        PasswordResetToken token = new PasswordResetToken();
        token.setId(entity.getId());
        token.setIdUsuario(entity.getIdUsuario());
        token.setToken(entity.getToken());
        token.setDtExpiracao(entity.getDtExpiracao());
        token.setUsado(entity.isUsed());
        token.setDtCriacao(entity.getDtCriacao());
        
        return token;
    }

    public static PasswordTokenEntityJpa toEntity(PasswordResetToken domain) {
        if (domain == null) {
            return null;
        }
        
        PasswordTokenEntityJpa entity = new PasswordTokenEntityJpa();
        entity.setId(domain.getId());
        entity.setIdUsuario(domain.getIdUsuario());
        entity.setToken(domain.getToken());
        entity.setDtExpiracao(domain.getDtExpiracao());
        entity.setUsed(domain.isUsado());
        entity.setDtCriacao(domain.getDtCriacao());
        
        return entity;
    }
}

package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ProfileFunctionalityJpaId implements Serializable {

    @Column(name = "COD_PERFIL")
    private String codPerfil;
    @Column(name = "COD_SISTEMA")
    private String codSistema;
    @Column(name = "COD_MODULO")
    private String codModulo;
    @Column(name = "COD_ROTINA")
    private String codRotina;
    @Column(name = "COD_FUNCIONALIDADE")
    private String codFuncionalidade;


}

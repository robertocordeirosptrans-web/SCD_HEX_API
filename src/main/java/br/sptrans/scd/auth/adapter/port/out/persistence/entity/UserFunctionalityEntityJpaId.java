package br.sptrans.scd.auth.adapter.port.out.persistence.entity;

import java.io.Serializable;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFunctionalityEntityJpaId implements Serializable{

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
    @Column(name = "COD_SISTEMA")
    private String codSistema;
    @Column(name = "COD_MODULO")
    private String codModulo;
    @Column(name = "COD_ROTINA")
    private String codRotina;
    @Column(name = "COD_FUNCIONALIDADE")
    private String codFuncionalidade;


}

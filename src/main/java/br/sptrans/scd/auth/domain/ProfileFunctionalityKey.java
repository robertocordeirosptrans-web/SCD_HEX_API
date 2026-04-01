package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProfileFunctionalityKey implements Serializable {

    private String codSistema;

    private String codModulo;

    private String codRotina;

    private String codFuncionalidade;

    private String codPerfil;

  
}

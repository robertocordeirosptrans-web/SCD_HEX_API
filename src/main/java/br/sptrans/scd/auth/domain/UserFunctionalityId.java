package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

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
public class UserFunctionalityId {

    private String codSistema;

    private String codModulo;

    private String codRotina;

    private String codFuncionalidade;

    private Long idUsuario;

    private LocalDate dtInicioValidade;


}

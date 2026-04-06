package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.io.Serializable;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalityEntityJpaKey implements Serializable{

    @Column(name = "COD_SISTEMA", length = 10)
    private String codSistema;
    @Column(name = "COD_MODULO", length = 10)
    private String codModulo;
    @Column(name = "COD_ROTINA", length = 10)
    private String codRotina;
    @Column(name = "COD_FUNCIONALIDADE", length = 30)
    private String codFuncionalidade;


}

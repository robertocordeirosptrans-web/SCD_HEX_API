package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Embeddable
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class RoutineEntityJpaKey implements Serializable {

    @Column(name = "COD_SISTEMA", length = 10)
    private String codSistema;
    @Column(name = "COD_MODULO", length = 10)
    private String codModulo;
    @Column(name = "COD_ROTINA", length = 10)
    private String codRotina;
}

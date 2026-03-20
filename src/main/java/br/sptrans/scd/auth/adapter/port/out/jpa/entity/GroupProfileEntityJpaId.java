package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@RequiredArgsConstructor
public class GroupProfileEntityJpaId implements Serializable{

    @Column(name = "COD_GRUPO")
    private String codGrupo;
    @Column(name = "COD_PERFIL")
    private String codPerfil;

}

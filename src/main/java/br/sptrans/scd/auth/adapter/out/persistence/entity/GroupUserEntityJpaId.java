package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@RequiredArgsConstructor
public class GroupUserEntityJpaId implements Serializable {

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
    @Column(name = "COD_GRUPO")
    private String codGrupo;


}

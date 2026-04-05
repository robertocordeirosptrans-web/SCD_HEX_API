package br.sptrans.scd.auth.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class UserProfileId implements Serializable {
    private Long idUsuario;
    private String codPerfil;
    private LocalDateTime dtInicioValidade;
    private LocalDateTime dtFimValidade;

 

}
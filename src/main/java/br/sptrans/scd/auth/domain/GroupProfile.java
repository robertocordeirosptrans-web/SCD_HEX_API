package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupProfile {

    private GroupProfileKey id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDate dtModi;
    private Group grupo;
    private Profile perfil;

}

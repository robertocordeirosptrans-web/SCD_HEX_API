package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupProfile {

    private GroupProfileKey id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDate dtModi;
    private Group group;
    private Profile profile;

}

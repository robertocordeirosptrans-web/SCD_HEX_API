package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileFunctionality {

    private ProfileFunctionalityKey id;
    private Long idUsuarioManutencao;
    private LocalDate dtInicioValidade;
    private Functionality funcionalidade;
    private Profile perfil;
    private User usuarioManutencao;

  
}

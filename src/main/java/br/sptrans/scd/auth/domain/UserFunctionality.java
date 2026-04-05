package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFunctionality {

    private UserFunctionalityId id;
    private LocalDateTime dtFimValidade;
    private Long idUsuarioManutencao;
    private LocalDateTime dtModi;
    private String codStatusUsuFun;
    private Functionality funcionalidade;
    private User usuario;

   
}

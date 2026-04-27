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
public class Functionality {

    private FunctionalityKey id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDateTime dtModi;
    private String flgMonitoracao;
    private LocalDateTime dtSinc;
    private String nomFuncionalidade;
    private String flgEvento;


    public String canonicalKey() {
        return id.getCodSistema() + "_" + id.getCodModulo() + "_" + id.getCodRotina() + "_" + id.getCodFuncionalidade();
    }

    

}

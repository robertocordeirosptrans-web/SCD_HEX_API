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
public class Systems {
    private String codSistema;
    private String nomSistema;
    private String nomCaminhoDiretorio;
    private String codStatus;
    private LocalDateTime dtManutencao;
    private Long idUsuarioManutencao;


}

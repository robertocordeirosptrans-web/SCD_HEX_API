package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementValidity {

    private AgreementValidityKey id;

    private LocalDateTime dtFimValidade;

    private LocalDateTime dtInicioValidade;

    private String codStatus;

    private LocalDateTime dtManutencao;

    private Long idUsuario;
}

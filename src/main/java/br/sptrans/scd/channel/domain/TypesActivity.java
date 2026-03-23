package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TypesActivity {

    private String codAtividade;

    private String desAtividade;

    private String codStatus;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;
}

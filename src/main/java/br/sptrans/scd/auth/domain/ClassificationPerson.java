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
public class ClassificationPerson {

    private String codClassificacaoPessoa;
    private String desClassificacaoPessoa;
    private String flgVenda;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String stClassificacoesPessoa;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;

    


}
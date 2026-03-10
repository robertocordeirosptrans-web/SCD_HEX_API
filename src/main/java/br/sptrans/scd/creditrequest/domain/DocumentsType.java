package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsType {

    private String codTipoDocumento;
    private String numLogicoCartao;
    private String stTiposDocumentos;
    private LocalDateTime dtInicioVigencia;
    private LocalDateTime dtTerminoVigencia;
    private Long idUsuarioCadastro;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private Long idUsuarioManutencao;
    private List<Situation> situacoes;
}

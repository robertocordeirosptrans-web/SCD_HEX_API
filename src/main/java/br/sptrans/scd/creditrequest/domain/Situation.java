package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Situation {

    private DocumentsType tipoDocumento;
    private String desSituacao;
    private String stSituacoes;
    private LocalDateTime dtInicioVigencia;
    private LocalDateTime dtTerminoVigencia;
    private Long idUsuarioCadastro;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private Integer flgSituacaoFinal;

    /**
     * Verifica se a situação está vigente.
     */
    public boolean isVigente() {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        return (dtInicioVigencia == null || !agora.isBefore(dtInicioVigencia)) &&
                (dtTerminoVigencia == null || !agora.isAfter(dtTerminoVigencia));
    }

    /**
     * Verifica se é situação final.
     */
    public boolean isFinal() {
        return flgSituacaoFinal != null && flgSituacaoFinal == 1;
    }
}

package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Systems {
    private String codSistema;
    private String nomSistema;
    private String nomCaminhoDiretorio;
    private String codStatus;
    private LocalDateTime dtManutencao;
    private Long idUsuarioManutencao;

    public Systems(String codSistema, String nomSistema, String nomCaminhoDiretorio, String codStatus, LocalDateTime dtManutencao, Long idUsuarioManutencao) {
        this.codSistema = codSistema;
        this.nomSistema = nomSistema;
        this.nomCaminhoDiretorio = nomCaminhoDiretorio;
        this.codStatus = codStatus;
        this.dtManutencao = dtManutencao;
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public String getCodSistema() { return codSistema; }
    public String getNomSistema() { return nomSistema; }
    public String getNomCaminhoDiretorio() { return nomCaminhoDiretorio; }
    public String getCodStatus() { return codStatus; }
    public LocalDateTime getDtManutencao() { return dtManutencao; }
    public Long getIdUsuarioManutencao() { return idUsuarioManutencao; }
}

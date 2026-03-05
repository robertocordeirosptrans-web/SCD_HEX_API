package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Functionality {

    private FunctionalityKey id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDateTime dtModi;
    private String flgMonitoracao;
    private LocalDateTime dtSinc;
    private String nomFuncionalidade;
    private String flgEvento;
    private String codSistema;
    private String codModulo;
    private String codRotina;
    private String codFuncionalidade;


    public String canonicalKey() {
        return codSistema + "_" + codModulo + "_" + codRotina + "_" + codFuncionalidade;
    }

    public Long getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public void setIdUsuarioManutencao(Long idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public LocalDateTime getDtModi() {
        return dtModi;
    }

    public void setDtModi(LocalDateTime dtModi) {
        this.dtModi = dtModi;
    }

    public String getFlgMonitoracao() {
        return flgMonitoracao;
    }

    public void setFlgMonitoracao(String flgMonitoracao) {
        this.flgMonitoracao = flgMonitoracao;
    }

    public LocalDateTime getDtSinc() {
        return dtSinc;
    }

    public void setDtSinc(LocalDateTime dtSinc) {
        this.dtSinc = dtSinc;
    }

    public String getNomFuncionalidade() {
        return nomFuncionalidade;
    }

    public void setNomFuncionalidade(String nomFuncionalidade) {
        this.nomFuncionalidade = nomFuncionalidade;
    }

    public String getFlgEvento() {
        return flgEvento;
    }

    public void setFlgEvento(String flgEvento) {
        this.flgEvento = flgEvento;
    }

}

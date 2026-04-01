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
public class Routine {

  
    private RoutineId id;
    private Long idUsuarioManutencao;
    private LocalDateTime dtModi;
    private String codStatus;
    private String nomRotina;
    private String nomLink;



    public Long getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public void setIdUsuarioManutencao(Long idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public LocalDateTime getDtModi() {
        return dtModi;
    }

    public void setDtModi(LocalDateTime dtModi) {
        this.dtModi = dtModi;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public String getNomRotina() {
        return nomRotina;
    }

    public void setNomRotina(String nomRotina) {
        this.nomRotina = nomRotina;
    }

    public String getNomLink() {
        return nomLink;
    }

    public void setNomLink(String nomLink) {
        this.nomLink = nomLink;
    }
}
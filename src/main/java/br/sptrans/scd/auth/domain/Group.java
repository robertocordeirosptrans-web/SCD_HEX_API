package br.sptrans.scd.auth.domain;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codGrupo")
public class Group {

    private String codGrupo;
    private Long idUsuarioManutencao;
    private LocalDate dtModi;
    private String codStatus;
    private String nomGrupo;
    private Set<GroupProfile> perfis;
    private Set<GroupUser> usuarios;

    public boolean isActive() {
        return "A".equalsIgnoreCase(this.codStatus);
    }

    public Set<GroupUser> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<GroupUser> usuarios) {
        this.usuarios = usuarios;
    }

    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }

    public void setIdUsuarioManutencao(Long idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public void setDtModi(LocalDate dtModi) {
        this.dtModi = dtModi;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public void setNomGrupo(String nomGrupo) {
        this.nomGrupo = nomGrupo;
    }

    public void setPerfis(Set<GroupProfile> perfis) {
        this.perfis = perfis;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public Long getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public LocalDate getDtModi() {
        return dtModi;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public String getNomGrupo() {
        return nomGrupo;
    }

    public Set<GroupProfile> getPerfis() {
        return perfis;
    }
}

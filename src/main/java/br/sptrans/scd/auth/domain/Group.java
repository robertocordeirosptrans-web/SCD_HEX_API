package br.sptrans.scd.auth.domain;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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



}

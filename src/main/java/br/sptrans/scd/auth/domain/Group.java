package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    private String codGrupo;
    private Long idUsuarioManutencao;
    private LocalDateTime dtModi;
    private String codStatus;
    private String nomGrupo;
    private Set<GroupProfile> perfis;
    private Set<GroupUser> usuarios;
    private User usuarioManutencao;

    public boolean isActive() {
        return "A".equalsIgnoreCase(this.codStatus);
    }



}

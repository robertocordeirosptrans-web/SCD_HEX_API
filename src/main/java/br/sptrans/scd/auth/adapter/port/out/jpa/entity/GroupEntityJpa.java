package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "GRUPOS", schema = "SPTRANSDBA")
public class GroupEntityJpa {

    @Id
    @Column(name = "COD_GRUPO", length = 20)
    private String codGrupo;

    @Column(name = "NOM_GRUPO", length = 100)
    private String nomGrupo;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;
    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private Set<GroupProfileEntityJpa> grupoPerfis = new HashSet<>();

    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private Set<GroupUserEntityJpa> grupoUsuarios = new HashSet<>();
}

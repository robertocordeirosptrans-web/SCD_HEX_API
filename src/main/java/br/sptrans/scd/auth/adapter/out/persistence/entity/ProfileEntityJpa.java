package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERFIS", schema = "SPTRANSDBA")
public class ProfileEntityJpa {

    @Id
    @Column(name = "COD_PERFIL", length = 20)
    private String codPerfil;

    @Column(name = "NOM_PERFIL", length = 100)
    private String nomPerfil;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_MANUTENCAO", insertable = false, updatable = false)
    private UserEntityJpa usuarioManutencao;

    @OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
    private Set<ProfileFunctionalityJpa> perfilFuncionalidades = new HashSet<>();

    @PrePersist
    protected void aoInserir() {
  
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

    

}

package br.sptrans.scd.auth.adapter.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class ProfileEntityJpa {

    @Id
    @Column(name = "COD_PERFIL", length = 20)
    private String codPerfil;

    @Column(name = "NOM_PERFIL", length = 100)
    private String nomPerfil;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;
    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;
    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    // @OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
    // private Set<PerfilFuncionalidadeJpaEntidade> perfilFuncionalidades = new HashSet<>();

    @PrePersist
    protected void aoInserir() {
        this.dtCadastro = LocalDateTime.now();
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

    

}

package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERFIL_FUNCIONALIDADES", schema = "SPTRANSDBA")
public class ProfileFunctionalityJpa {

    @EmbeddedId
    private ProfileFunctionalityJpaId id;

    @Column(name = "DT_INICIO_VALIDADE")
    private LocalDateTime dtInicioValidade;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_MANUTENCAO", insertable = false, updatable = false)
    private UserEntityJpa usuarioManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codPerfil")
    @JoinColumn(name = "COD_PERFIL")
    private ProfileEntityJpa perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "COD_SISTEMA", referencedColumnName = "COD_SISTEMA", insertable = false, updatable = false),
        @JoinColumn(name = "COD_MODULO", referencedColumnName = "COD_MODULO", insertable = false, updatable = false),
        @JoinColumn(name = "COD_ROTINA", referencedColumnName = "COD_ROTINA", insertable = false, updatable = false),
        @JoinColumn(name = "COD_FUNCIONALIDADE", referencedColumnName = "COD_FUNCIONALIDADE", insertable = false, updatable = false)
    })
    private FunctionalityEntityJpa funcionalidade;

    @PrePersist
    protected void aoInserir() {
        this.dtInicioValidade = LocalDateTime.now();

    }


}

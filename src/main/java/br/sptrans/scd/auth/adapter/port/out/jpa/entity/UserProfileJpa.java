package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "USUARIO_PERFIS", schema = "SPTRANSDBA")
public class UserProfileJpa {

    @EmbeddedId
    private UserProfileJpaId id;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;    // "A" = Ativo  "I" = Inativo

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private UserEntityJpa usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codPerfil")
    @JoinColumn(name = "COD_PERFIL")
    private ProfileEntityJpa perfil;

}

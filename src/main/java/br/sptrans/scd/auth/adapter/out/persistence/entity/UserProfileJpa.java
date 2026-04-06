package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "USUARIO_PERFIS", schema = "SPTRANSDBA")
public class UserProfileJpa {

    @EmbeddedId
    private UserProfileJpaId id;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;    // "A" = Ativo  "I" = Inativo

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "DES_OBSERVACAO", length = 60)
    private String desObservacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private UserEntityJpa usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codPerfil")
    @JoinColumn(name = "COD_PERFIL")
    private ProfileEntityJpa perfil;

}

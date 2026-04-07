package br.sptrans.scd.product.adapter.out.persistence.entity;


import java.time.LocalDateTime;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FAMILIAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyEntityJpa {

    @Id
    @Column(name = "COD_FAMILIA", nullable = false, length = 20)
    private String codFamilia;

    @Column(name = "DES_FAMILIA", length = 60)
    private String desFamilia;

    @Column(name = "ST_FAMILIA", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO", length = 20)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO", length = 20)
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private UserEntityJpa usuarioCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_MANUTENCAO")
    private UserEntityJpa usuarioManutencao;
}

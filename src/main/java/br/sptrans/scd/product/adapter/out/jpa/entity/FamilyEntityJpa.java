package br.sptrans.scd.product.adapter.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(name = "ST_FAMILIAS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO", length = 20)
    private String dtCadastro;

    @Column(name = "DT_MANUTENCAO", length = 20)
    private String dtManutencao;
}

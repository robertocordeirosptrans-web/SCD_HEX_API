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
@Table(name = "TECNOLOGIAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyEntityJpa {

    @Id
    @Column(name = "COD_TECNOLOGIA", nullable = false, length = 20)
    private String codTecnologia;

    @Column(name = "DES_TECNOLOGIA", length = 60)
    private String desTecnologia;

    @Column(name = "ST_TECNOLOGIAS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private String dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private String dtManutencao;
}

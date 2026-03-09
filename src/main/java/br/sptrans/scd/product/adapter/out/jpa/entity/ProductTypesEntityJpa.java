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
@Table(name = "TIPOS_PRODUTOS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypesEntityJpa {

    @Id
    @Column(name = "COD_TIPO_PRODUTO", nullable = false, length = 20)
    private String codTipoProduto;

    @Column(name = "DES_TIPO_PRODUTO", length = 60)
    private String desTipoProduto;

    @Column(name = "ST_TIPOS_PRODUTOS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private String dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private String dtManutencao;
}

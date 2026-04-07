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
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "COD_TIPO_PRODUTO", nullable = false, length = 20)
    private String codTipoProduto;

    @Column(name = "DES_TIPO_PRODUTO", length = 60)
    private String desTipoProduto;

    @NotNull
    @Column(name = "ST_TIPOS_PRODUTOS", length = 1)
    private String codStatus;

    @NotNull
    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private UserEntityJpa usuarioCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_MANUTENCAO")
    private UserEntityJpa usuarioManutencao;
}

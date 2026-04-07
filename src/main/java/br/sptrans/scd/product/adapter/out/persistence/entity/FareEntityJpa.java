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
@Table(name = "TARIFAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FareEntityJpa {

    @Id
    @Column(name = "COD_TARIFA", nullable = false, length = 20)
    private String codTarifa;

    @Column(name = "COD_PRODUTO", nullable = false, length = 20)
    private String codProduto;

    @Column(name = "DT_VIGENCIA_INI", nullable = false)
    private LocalDateTime dtVigenciaInicio;

    @Column(name = "DT_VIGENCIA_FIM", nullable = false)
    private LocalDateTime dtVigenciaFim;

    @Column(name = "DES_TARIFA", length = 60)
    private String desTarifa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private UserEntityJpa usuarioCadastro;

    @Column(name = "VL_TARIFA", length = 15)
    private Long valTarifa;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_MANUTENCAO")
    private UserEntityJpa usuarioManutencao;

    @Column(name = "ST_TARIFAS", nullable = false)
    private String codStatus;

}

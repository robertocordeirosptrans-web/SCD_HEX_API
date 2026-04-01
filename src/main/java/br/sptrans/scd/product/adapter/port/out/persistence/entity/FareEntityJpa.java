package br.sptrans.scd.product.adapter.port.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private LocalDateTime dtVigenciaIni;

    @Column(name = "DT_VIGENCIA_FIM", nullable = false)
    private LocalDateTime dtVigenciaFim;

    @Column(name = "DES_TARIFA", length = 60)
    private String desTarifa;

    @Column(name = "ID_USUARIO_CADASTRO", length = 60)
    private Long idUsuarioCadastro;

    @Column(name = "VL_TARIFA", length = 15)
    private Long vlTarifa;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO", length = 20)
    private Long idUsuarioManutencao;

    @Column(name = "ST_TARIFAS", nullable = false)
    private String stTarifas;   

}

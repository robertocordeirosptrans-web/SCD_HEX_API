package br.sptrans.scd.channel.adapter.port.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CONVENIOS_VIGENCIAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementValidityEntityJpa {

    @EmbeddedId
    private AgreementValidityKeyEntityJpa id;

    @Column(name = "DT_FIM_VALIDADE")
    private LocalDateTime dataFimValidade;

    @Column(name = "DT_INICIO_VALIDADE")
    private LocalDateTime dataInicioValidade;

    @Column(name = "COD_STATUS", length = 1)
    private String status;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dataManutencao;

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
}

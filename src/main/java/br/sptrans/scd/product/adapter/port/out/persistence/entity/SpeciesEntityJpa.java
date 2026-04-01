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
@Table(name = "ESPECIES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesEntityJpa {

    @Id
    @Column(name = "COD_ESPECIE", nullable = false, length = 20)
    private String codEspecie;

    @Column(name = "DES_ESPECIE", length = 20)
    private String desEspecie;

    @Column(name = "ST_ESPECIES", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO", length = 20)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO", length = 20)
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_CADASTRO", length = 20)
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO", length = 20)
    private Long idUsuarioManutencao;
}

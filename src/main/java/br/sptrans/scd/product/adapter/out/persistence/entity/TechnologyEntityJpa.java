package br.sptrans.scd.product.adapter.out.persistence.entity;

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
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_CADASTRO", length = 50)
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO", length = 50)
    private Long idUsuarioManutencao;
}

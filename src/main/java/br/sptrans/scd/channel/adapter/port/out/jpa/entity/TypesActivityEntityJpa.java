package br.sptrans.scd.channel.adapter.port.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TIPOS_ATIVIDADE", schema="SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypesActivityEntityJpa {

    @Id
    @Column(name = "COD_ATIVIDADE", nullable = false, length = 20)
    private String codAtividade;

    @Column(name = "DES_ATIVIDADE", nullable = false, length = 20)
    private String desAtividade;

    @Column(name = "ST_ATIVIDADE", nullable = false, length = 20)
    private String codStatus;

    @Column(name = "DT_CADASTRO", nullable = false, length = 20)
    private String dtCadastro;

    @Column(name = "DT_MANUTENCAO", nullable = false, length = 20)
    private String dtManutencao;
}

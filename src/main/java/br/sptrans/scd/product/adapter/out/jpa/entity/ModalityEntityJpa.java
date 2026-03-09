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
@Table(name = "MODALIDADES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModalityEntityJpa {

    @Id
    @Column(name = "COD_MODALIDADE", nullable = false, length = 20)
    private String codModalidade;

    @Column(name = "DES_MODALIDADE", length = 20)
    private String desModalidade;

    @Column(name = "ST_MODALIDADES", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO", length = 20)
    private String dtCadastro;

    @Column(name = "DT_MANUTENCAO", length = 20)
    private String dtManutencao;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}

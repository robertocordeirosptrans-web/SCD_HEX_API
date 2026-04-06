package br.sptrans.scd.auth.adapter.port.out.persistence.entity;

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
@Table(name = "CLASSIFICACOES_PESSOAS", schema="SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationPersonEntity {
    @Id
    @Column(name = "COD_CLASSIFICACAO_PESSOA", nullable = false, length = 20)
    private String codClassificacaoPessoa;

    @Column(name = "DES_CLASSIFICACAO_PESSOA", nullable = false, length = 60)
    private String desClassificacaoPessoa;

    @Column(name = "FLG_VENDA", length = 1)
    private String flgVenda;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ST_CLASSIFICACOES_PESSOA", nullable = false, length = 1)
    private String stClassificacoesPessoa;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}

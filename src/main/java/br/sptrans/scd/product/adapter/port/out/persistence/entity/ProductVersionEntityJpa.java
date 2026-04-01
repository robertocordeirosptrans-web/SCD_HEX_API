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
@Table(name = "PRODUTOS_VERSOES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVersionEntityJpa {

    @Id
    @Column(name = "COD_VERSAO", nullable = false, length = 20)
    private String codVersao;

    @Column(name = "COD_PRODUTO", length = 20)
    private String codProduto;

    @Column(name = "DT_VALIDADE")
    private LocalDateTime dtValidade;

    @Column(name = "DT_VIDA_INICIO")
    private LocalDateTime dtVidaInicio;

    @Column(name = "DT_VIDA_FIM")
    private LocalDateTime dtVidaFim;

    @Column(name = "DT_LIBERACAO")
    private LocalDateTime dtLiberacao;

    @Column(name = "DT_LANCAMENTO")
    private LocalDateTime dtLancamento;

    @Column(name = "DT_VENDA_INICIO")
    private LocalDateTime dtVendaInicio;

    @Column(name = "DT_VENDA_FIM")
    private LocalDateTime dtVendaFim;

    @Column(name = "DT_USO_INICIO")
    private LocalDateTime dtUsoIni;

    @Column(name = "DT_USO_FIM")
    private LocalDateTime dtUsoFim;

    @Column(name = "DT_TROCA_INICIO")
    private LocalDateTime dtTrocaIni;

    @Column(name = "DT_TROCA_FIM")
    private LocalDateTime dtTrocaFim;

    @Column(name = "FLG_BLOQ_FABRICACAO", length = 1)
    private String flgBloqFabricacao;

    @Column(name = "FLG_BLOQ_VENDA", length = 1)
    private String flgBloqVenda;

    @Column(name = "FLG_BLOQ_DISTRIBUICAO", length = 1)
    private String flgBloqDistribuicao;

    @Column(name = "FLG_BLOQ_TROCA", length = 1)
    private String flgBloqTroca;

    @Column(name = "FLG_BLOQ_AQUISICAO", length = 1)
    private String flgBloqAquisicao;

    @Column(name = "FLG_BLOQ_PEDIDO", length = 1)
    private String flgBloqPedido;

    @Column(name = "FLG_BLOQ_DEVOLUCAO", length = 1)
    private String flgBloqDevolucao;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ST_PRODUTOS_VERSOES", length = 1)
    private String stProdutosVersoes;

    @Column(name = "DES_PROD_VERSAO", length = 60)
    private String desProdutoVersoes;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

}

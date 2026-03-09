package br.sptrans.scd.product.adapter.out.jpa.entity;

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
@Table(name = "PRODUTOS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntityJpa {

    @Id
    @Column(name = "COD_PRODUTO", nullable = false, length = 20)
    private String codProduto;

    @Column(name = "DES_PRODUTO", length = 20)
    private String desProduto;

    @Column(name = "DES_EMISSOR_RESPONSAVEL", length = 60)
    private String desEmissorResponsavel;

    @Column(name = "ST_PRODUTOS", length = 1)
    private String codStatus;

    @Column(name = "DES_UTILIZACAO", length = 60)
    private String desUtilizacao;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

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

    @Column(name = "FLG_INICIALIZADO", length = 1)
    private String flgInicializado;

    @Column(name = "FLG_COMERCIALIZADO", length = 1)
    private String flgComercializado;

    @Column(name = "FLG_REST_MANUAL", length = 1)
    private String flgRestManual;

    @Column(name = "COD_ENTIDADE", length = 1)
    private String codEntidade;

    @Column(name = "COD_TIPO_CARTAO", length = 20)
    private String codTipoCartao;

    @Column(name = "COD_CLASSIFICACAO_PESSOA")
    private String codClassificacaoPessoa;

    @Column(name = "COD_TIPO_PRODUTO")
    private String codTipoProduto;

    @Column(name = "COD_TECNOLOGIA")
    private String codTecnologia;

    @Column(name = "COD_MODALIDADE")
    private String codModalidade;

    @Column(name = "COD_FAMILIA")
    private String codFamilia;

    @Column(name = "COD_ESPECIE")
    private String codEspecie;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}

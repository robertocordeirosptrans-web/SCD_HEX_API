package br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SOLICITACAO_LOTE_SCP", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLotSCPEntityJpa {
	@Column(name = "COD_TIPO_CANAL", length = 20, nullable = false)
	private String codTipoCanal;

	@Column(name = "COD_CANAL", length = 20, nullable = false)
	private String codCanal;

	@Column(name = "NR_SOLICITACAO", nullable = false)
	private Long nrSolicitacao;

	@Column(name = "ID_LOTE", nullable = false)
	private Long idLote;

	@Column(name = "FLG_FASE_SOLICITACAO", length = 1, nullable = false)
	private String flgFaseSolicitacao;

	@Column(name = "QTD_PRODUTO")
	private Long qtdProduto;

	@Column(name = "ST_SOLICITACAO_LOTE_SCP", length = 1)
	private String stSolicitacaoLoteScp;

	@Column(name = "DT_CADASTRO")
	private java.util.Date dtCadastro;

	@Column(name = "DT_MANUTENCAO")
	private java.util.Date dtManutencao;

	@Column(name = "ID_USUARIO_CADASTRO")
	private Long idUsuarioCadastro;

	@Column(name = "ID_USUARIO_MANUTENCAO")
	private Long idUsuarioManutencao;
}

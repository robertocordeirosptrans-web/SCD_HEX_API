package br.sptrans.scd.initializedcards.adapter.port.out.jpa.adapter;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.application.port.out.RequestInitializedRepository;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class RequestInitializedAdapter implements RequestInitializedRepository {

    private final JdbcTemplate jdbc;

	private static final String SQL_INSERT = """
		INSERT INTO SPTRANSDBA.SOLICITA_CARTAO_INICIALIZADO (
			COD_TIPO_CANAL, COD_CANAL, NR_SOLICITACAO, COD_ADQUIRENTE, COD_PRODUTO,
			QTD_SOLICITADA, QTD_ATENDIDA, QTD_RECEBIDA, FLG_TIPO_SAIDA, FLG_TIPO_VOLUME, FLG_ASSOCIACAO_USUARIO,
			FLG_GERA_ARQUIVO, FLG_RESP_ENTREGA_RETIRADA, DES_NOME_RESP_ENTREGA, COD_TIPO_DOCTO_RESP_ENTREGA,
			COD_DOCTO_RESP_ENTREGA, COD_ENDERECO_ENTREGA, DES_NOME_RESP_RECEBIMENTO, COD_TIPO_DOCTO_RESP_RECEBE,
			COD_DOCTO_RESP_RECEBE, DES_MOTIVO_DIFERENCA_RECEBE, FLG_APROVADO, ID_USUARIO_APROVACAO, DT_PREVISTA_ENTREGA,
			DT_SOLICITACAO, DT_APROVACAO, DT_ASSOCIACAO_LOTE_SCP, DT_GERACAO_ARQUIVO, DT_ASSOCIACAO_USUARIO, DT_ENVIO,
			DT_RECEBIMENTO, DT_DEVOLUCAO, FLG_FASE_SOLICITACAO, ST_SOLICITA_CARTAO_INICIALIZAD, DT_CADASTRO, DT_MANUTENCAO,
			DT_CANCELAMENTO, ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
		) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	""";

	private static final String SQL_SELECT_BASE = """
		SELECT * FROM SPTRANSDBA.SOLICITA_CARTAO_INICIALIZADO
	""";

	@Override
	public RequestInitializedCards save(RequestInitializedCards entity) {
		jdbc.update(SQL_INSERT,
			entity.getCodTipoCanal(),
			entity.getCodCanal() != null ? entity.getCodCanal().getDesCanal() : null,
			entity.getNrSolicitacao(),
			entity.getCodAdquirente(),
			entity.getCodProduto() != null ? entity.getCodProduto().getDesProduto() : null,
			entity.getQtdSolicitada(),
			entity.getQtdAtendida(),
			entity.getQtdRecebida(),
			entity.getFlgTipoSaida(),
			entity.getFlgTipoVolume(),
			entity.getFlgAssociaUsuario(),
			entity.getFlgGerarArquivo(),
			entity.getFlgRespEntregaRetirada(),
			entity.getDesRespEntrega(),
			entity.getCodTipoRespEntrega(),
			entity.getCodDoctoRespEntrega(),
			entity.getCodEnderecoEntrega(),
			entity.getDesRespRecebimento(),
			entity.getCodTipoDoctoRespRecebe(),
			entity.getCodDoctoRespRecebe(),
			entity.getDesMotivoDiferencaRecebe(),
			entity.getFlgAprovado(),
			entity.getIdUsuarioAprovacao() != null ? entity.getIdUsuarioAprovacao().getIdUsuario() : null,
			entity.getDtPrevistaEntrega(),
			entity.getDtSolicitacao(),
			entity.getDtAprovacao(),
			entity.getDtAssociacaoLoteSCP(),
			entity.getDtGeracaoArquivo(),
			entity.getDtAssociacaoUsuario(),
			entity.getDtEnvio(),
			entity.getDtRecebimento(),
			entity.getDtDevolucao(),
			entity.getFlgFaseSolicitacao(),
			entity.getStSolicitaCartaoInicializad(),
			entity.getDtCadastro(),
			entity.getDtManutencao(),
			entity.getDtCancelamento(),
			entity.getIdUsuarioCadastro() != null ? entity.getIdUsuarioCadastro().getIdUsuario() : null,
			entity.getIdUsuarioManutencao() != null ? entity.getIdUsuarioManutencao().getIdUsuario() : null
		);
		return entity;
	}

	@Override
	public RequestInitializedCards findById(String codCanal, Long nrSolicitacao) {
		String sql = SQL_SELECT_BASE + " WHERE COD_CANAL = ? AND NR_SOLICITACAO = ?";
		// Aqui você deve mapear o ResultSet para RequestInitializedCards
		return jdbc.query(sql, rs -> {
			if (rs.next()) {
				// TODO: Mapear os campos do ResultSet para a entidade
				return new RequestInitializedCards();
			}
			return null;
		}, codCanal, nrSolicitacao);
	}

	@Override
	public RequestInitializedCards findAll(String codCanal, Long nrSolicitacao, String codAdquirente) {
		String sql = SQL_SELECT_BASE + " WHERE COD_CANAL = ? AND NR_SOLICITACAO = ? AND COD_ADQUIRENTE = ?";
		// Aqui você deve mapear o ResultSet para RequestInitializedCards
		return jdbc.query(sql, rs -> {
			if (rs.next()) {
				// TODO: Mapear os campos do ResultSet para a entidade
				return new RequestInitializedCards();
			}
			return null;
		}, codCanal, nrSolicitacao, codAdquirente);
	}

	
}

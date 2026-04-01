package br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.UserMapper;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.RequestLotSCPEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.RequestLotSCPEntityJpaKey;
import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.RequestLotSCPKey;

@Component
public class RequestLotMapper {

	public static RequestLotSCP toDomain(RequestLotSCPEntityJpa entity) {
		if (entity == null) return null;
		RequestLotSCP domain = new RequestLotSCP();
		domain.setId(toDomainKey(entity.getId()));
		domain.setQtdProduto(entity.getQtdProduto());
		domain.setStSolicitacaoLoteSCP(entity.getStSolicitacaoLoteScp());
		domain.setDtCadastro(toLocalDateTime(entity.getDtCadastro()));
		domain.setDtManutencao(toLocalDateTime(entity.getDtManutencao()));
		domain.setIdUsuarioCadastro(UserMapper.toDomain(toUserEntity(entity.getIdUsuarioCadastro())));
		domain.setIdUsuarioManutencao(UserMapper.toDomain(toUserEntity(entity.getIdUsuarioManutencao())));
		return domain;
	}

	public static RequestLotSCPEntityJpa toEntity(RequestLotSCP domain) {
		if (domain == null) return null;
		RequestLotSCPEntityJpa entity = new RequestLotSCPEntityJpa();
		entity.setId(toEntityKey(domain.getId()));
		entity.setQtdProduto(domain.getQtdProduto());
		entity.setStSolicitacaoLoteScp(domain.getStSolicitacaoLoteSCP());
		entity.setDtCadastro(toDate(domain.getDtCadastro()));
		entity.setDtManutencao(toDate(domain.getDtManutencao()));
		entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro() != null ? domain.getIdUsuarioCadastro().getIdUsuario() : null);
		entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao() != null ? domain.getIdUsuarioManutencao().getIdUsuario() : null);
		return entity;
	}

	private static RequestLotSCPKey toDomainKey(RequestLotSCPEntityJpaKey entityKey) {
		if (entityKey == null) return null;
		return new RequestLotSCPKey(
			entityKey.getCodTipoCanal(),
			entityKey.getCodCanal(),
			entityKey.getNrSolicitacao(),
			entityKey.getIdLote(),
			entityKey.getFlgFaseSolicitacao()
		);
	}

	public static RequestLotSCPEntityJpaKey toEntityKey(RequestLotSCPKey domainKey) {
		if (domainKey == null) return null;
		return new RequestLotSCPEntityJpaKey(
			domainKey.getCodTipoCanal(),
			domainKey.getCodCanal(),
			domainKey.getNrSolicitacao(),
			domainKey.getIdLote(),
			domainKey.getFlgFaseSolicitacao()
		);
	}

	private static LocalDateTime toLocalDateTime(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	private static Date toDate(LocalDateTime ldt) {
		if (ldt == null) return null;
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	// Cria UserEntityJpa apenas com o id para o UserMapper
	private static UserEntityJpa toUserEntity(Long idUsuario) {
		if (idUsuario == null) return null;
		UserEntityJpa entity = new UserEntityJpa();
		entity.setIdUsuario(idUsuario);
		return entity;
	}
}

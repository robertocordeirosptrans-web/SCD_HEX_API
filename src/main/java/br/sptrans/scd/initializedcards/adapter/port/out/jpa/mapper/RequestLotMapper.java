package br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.UserMapper;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.RequestLotSCPEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.RequestLotSCPEntityJpaKey;
import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.RequestLotSCPKey;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestLotMapper {

	private final UserMapper userMapper;

	public RequestLotSCP toDomain(RequestLotSCPEntityJpa entity) {
		if (entity == null) return null;
		RequestLotSCP domain = new RequestLotSCP();
		domain.setId(toDomainKey(entity.getId()));
		domain.setQtdProduto(entity.getQtdProduto());
		domain.setStSolicitacaoLoteSCP(entity.getStSolicitacaoLoteScp());
		domain.setDtCadastro(toLocalDateTime(entity.getDtCadastro()));
		domain.setDtManutencao(toLocalDateTime(entity.getDtManutencao()));
		domain.setIdUsuarioCadastro(userMapper.toDomain(toUserEntity(entity.getIdUsuarioCadastro())));
		domain.setIdUsuarioManutencao(userMapper.toDomain(toUserEntity(entity.getIdUsuarioManutencao())));
		return domain;
	}

	public RequestLotSCPEntityJpa toEntity(RequestLotSCP domain) {
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

	private RequestLotSCPKey toDomainKey(RequestLotSCPEntityJpaKey entityKey) {
		if (entityKey == null) return null;
		return new RequestLotSCPKey(
			entityKey.getCodTipoCanal(),
			entityKey.getCodCanal(),
			entityKey.getNrSolicitacao(),
			entityKey.getIdLote(),
			entityKey.getFlgFaseSolicitacao()
		);
	}

	public RequestLotSCPEntityJpaKey toEntityKey(RequestLotSCPKey domainKey) {
		if (domainKey == null) return null;
		return new RequestLotSCPEntityJpaKey(
			domainKey.getCodTipoCanal(),
			domainKey.getCodCanal(),
			domainKey.getNrSolicitacao(),
			domainKey.getIdLote(),
			domainKey.getFlgFaseSolicitacao()
		);
	}

	private LocalDateTime toLocalDateTime(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	private Date toDate(LocalDateTime ldt) {
		if (ldt == null) return null;
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	// Cria UserEntityJpa apenas com o id para o UserMapper
	private UserEntityJpa toUserEntity(Long idUsuario) {
		if (idUsuario == null) return null;
		UserEntityJpa entity = new UserEntityJpa();
		entity.setIdUsuario(idUsuario);
		return entity;
	}
}

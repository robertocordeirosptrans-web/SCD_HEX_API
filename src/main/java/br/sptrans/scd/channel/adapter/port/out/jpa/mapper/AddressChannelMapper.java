package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AddressChannelEntityJpa;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.SalesChannel;

/**
 * Mapper JPA para AddressChannel.
 * Converte entre Domain Entity e JPA Entity.
 */
@Component
public class AddressChannelMapper {

	public AddressChannelEntityJpa toEntity(AddressChannel domain) {
		if (domain == null) {
			return null;
		}
		AddressChannelEntityJpa entity = new AddressChannelEntityJpa();
		entity.setCodEndereco(domain.getCodEndereco());
		entity.setCodEmpregador(domain.getCodEmpregador());
		entity.setDesLogradouro(domain.getDesLogradouro());
		entity.setCodFornecedor(domain.getCodFornecedor());
		entity.setCodTipoEndereco(domain.getCodTipoEndereco());
		entity.setCodCEP(domain.getCodCEP());
		entity.setDesBairro(domain.getDesBairro());
		entity.setDesCidade(domain.getDesCidade());
		entity.setDesUF(domain.getDesUF());
		entity.setNumDDD(domain.getNumDDD());
		entity.setNumFone(domain.getNumFone());
		entity.setNumFax(domain.getNumFax());
		entity.setDesObs(domain.getDesObs());
		entity.setDtCadastro(domain.getDtCadastro());
		entity.setDtManutencao(domain.getDtManutencao());
		entity.setStEnderecos(domain.getStEnderecos());
		entity.setDtValidade(domain.getDtValidade());
		entity.setCodSeq(domain.getCodSeq());
		entity.setDesNumero(domain.getDesNumero());
		if (domain.getCodCanal() != null) {
			entity.setCodCanal(domain.getCodCanal().getCodCanal());
		}
		if (domain.getIdUsuarioCadastro() != null) {
			entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro().getIdUsuario());
		}
		if (domain.getIdUsuarioManutencao() != null) {
			entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao().getIdUsuario());
		}
		return entity;
	}

	public AddressChannel toDomain(AddressChannelEntityJpa entity) {
		if (entity == null) {
			return null;
		}
		User manutencao = null;
		if (entity.getIdUsuarioManutencao() != null) {
			manutencao = new User();
			manutencao.setIdUsuario(entity.getIdUsuarioManutencao());
		}
		User cadastro = null;
		if (entity.getIdUsuarioCadastro() != null) {
			cadastro = new User();
			cadastro.setIdUsuario(entity.getIdUsuarioCadastro());
		}
		SalesChannel canal = null;
		if (entity.getCodCanal() != null) {
			canal = new SalesChannel();
			canal.setCodCanal(entity.getCodCanal());
		}
		return new AddressChannel(
				entity.getCodEndereco(),
				entity.getCodEmpregador(),
				entity.getDesLogradouro(),
				entity.getCodFornecedor(),
				entity.getCodTipoEndereco(),
				entity.getCodCEP(),
				entity.getDesBairro(),
				entity.getDesCidade(),
				entity.getDesUF(),
				entity.getNumDDD(),
				entity.getNumFone(),
				entity.getNumFax(),
				entity.getDesObs(),
				entity.getDtCadastro(),
				entity.getDtManutencao(),
				entity.getStEnderecos(),
				entity.getDtValidade(),
				entity.getCodSeq(),
				entity.getDesNumero(),
				manutencao,
				cadastro,
				canal
		);
	}
}

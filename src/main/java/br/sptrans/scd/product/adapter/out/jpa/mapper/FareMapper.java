package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.out.persistence.entity.FareEntityJpa;
import br.sptrans.scd.product.domain.Fare;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.auth.domain.User;

public interface FareMapper {

	static Fare toDomain(FareEntityJpa entity) {
		if (entity == null) {
			return null;
		}
		Fare fare = new Fare();
		fare.setCodTarifa(entity.getCodTarifa());
		fare.setDtVigenciaInicio(entity.getDtVigenciaInicio());
		fare.setDtVigenciaFim(entity.getDtVigenciaFim());
		fare.setDtCadastro(entity.getDtCadastro());
		fare.setDtManutencao(entity.getDtManutencao());
		fare.setDesTarifa(entity.getDesTarifa());
		fare.setCodStatus(entity.getCodStatus());
		if (entity.getValTarifa() != null) {
			fare.setValTarifa(entity.getValTarifa().intValue());
		}
		if (entity.getIdUsuarioCadastro() != null) {
			User user = new User();
			user.setIdUsuario(entity.getIdUsuarioCadastro());
			fare.setIdUsuarioCadastro(user);
		}
		if (entity.getIdUsuarioManutencao() != null) {
			User user = new User();
			user.setIdUsuario(entity.getIdUsuarioManutencao());
			fare.setIdUsuarioManutencao(user);
		}
		if (entity.getCodProduto() != null) {
			Product product = new Product();
			product.setCodProduto(entity.getCodProduto());
			fare.setCodProduto(product);
		}
		return fare;
	}

	static FareEntityJpa toEntity(Fare fare) {
		if (fare == null) {
			return null;
		}
		FareEntityJpa entity = new FareEntityJpa();
		entity.setCodTarifa(fare.getCodTarifa());
		entity.setDtVigenciaInicio(fare.getDtVigenciaInicio());
		entity.setDtVigenciaFim(fare.getDtVigenciaFim());
		entity.setDtCadastro(fare.getDtCadastro());
		entity.setDtManutencao(fare.getDtManutencao());
		entity.setDesTarifa(fare.getDesTarifa());
		entity.setCodStatus(fare.getCodStatus());
		if (fare.getValTarifa() != null) {
			entity.setValTarifa(fare.getValTarifa().longValue());
		}
		if (fare.getIdUsuarioCadastro() != null) {
			entity.setIdUsuarioCadastro(fare.getIdUsuarioCadastro().getIdUsuario());
		}
		if (fare.getIdUsuarioManutencao() != null) {
			entity.setIdUsuarioManutencao(fare.getIdUsuarioManutencao().getIdUsuario());
		}
		if (fare.getCodProduto() != null) {
			entity.setCodProduto(fare.getCodProduto().getCodProduto());
		}
		return entity;
	}
}

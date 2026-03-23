package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.out.jpa.entity.FareEntityJpa;
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
		fare.setDtVigenciaIni(entity.getDtVigenciaIni());
		fare.setDtVigenciaFim(entity.getDtVigenciaFim());
		fare.setDtCadastro(entity.getDtCadastro());
		fare.setDtManutencao(entity.getDtManutencao());
		fare.setDesTarifa(entity.getDesTarifa());
		fare.setStTarifas(entity.getStTarifas());
		if (entity.getVlTarifa() != null) {
			fare.setVlTarifa(entity.getVlTarifa().intValue());
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
		entity.setDtVigenciaIni(fare.getDtVigenciaIni());
		entity.setDtVigenciaFim(fare.getDtVigenciaFim());
		entity.setDtCadastro(fare.getDtCadastro());
		entity.setDtManutencao(fare.getDtManutencao());
		entity.setDesTarifa(fare.getDesTarifa());
		entity.setStTarifas(fare.getStTarifas());
		if (fare.getVlTarifa() != null) {
			entity.setVlTarifa(fare.getVlTarifa().longValue());
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

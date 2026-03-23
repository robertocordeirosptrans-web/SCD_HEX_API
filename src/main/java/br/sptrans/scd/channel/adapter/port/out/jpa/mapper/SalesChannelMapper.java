package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.SalesChannelEntityJpa;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.auth.domain.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesChannelMapper {

	@Mappings({
		@Mapping(target = "codClassificacaoPessoa", ignore = true),
		@Mapping(target = "codAtividade", ignore = true),
		@Mapping(target = "idUsuarioCadastro", ignore = true),
		@Mapping(target = "idUsuarioManutencao", ignore = true)
	})
	SalesChannel toDomain(SalesChannelEntityJpa entity);

	@Mappings({
		@Mapping(target = "codClassificacaoPessoa", ignore = true),
		@Mapping(target = "codAtividade", ignore = true),
		@Mapping(target = "idUsuarioCadastro", ignore = true),
		@Mapping(target = "idUsuarioManutencao", ignore = true)
	})
	SalesChannelEntityJpa toEntity(SalesChannel domain);

	@Mappings({
		@Mapping(target = "codClassificacaoPessoa", ignore = true),
		@Mapping(target = "codAtividade", ignore = true),
		@Mapping(target = "idUsuarioCadastro", ignore = true),
		@Mapping(target = "idUsuarioManutencao", ignore = true)
	})
	void updateFromDomain(SalesChannel domain, @MappingTarget SalesChannelEntityJpa entity);

	// Métodos auxiliares para mapear campos complexos, se necessário
	default String mapClassificationPersonToCod(ClassificationPerson cp) {
		return cp != null ? cp.getCodClassificacaoPessoa() : null;
	}

	default String mapClassificationPersonToDes(ClassificationPerson cp) {
		return cp != null ? cp.getDesClassificacaoPessoa() : null;
	}

	default String mapTypesActivityToCod(TypesActivity ta) {
		return ta != null ? ta.getCodAtividade() : null;
	}

	default String mapTypesActivityToDes(TypesActivity ta) {
		return ta != null ? ta.getDesAtividade() : null;
	}

	default Long mapUserToId(User user) {
		return user != null ? user.getIdUsuario() : null;
	}

}

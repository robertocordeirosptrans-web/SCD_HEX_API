package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import java.time.LocalTime;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.auth.domain.vo.AccessPolicy;
import br.sptrans.scd.auth.domain.vo.Credentials;
import br.sptrans.scd.auth.domain.vo.DayPattern;
import br.sptrans.scd.auth.domain.vo.PersonalInfo;
import br.sptrans.scd.auth.domain.vo.TimeRange;
import br.sptrans.scd.auth.domain.vo.UserAudit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "credentials", expression = "java(toCredentials(entity))")
    @Mapping(target = "personalInfo", expression = "java(toPersonalInfo(entity))")
    @Mapping(target = "audit", expression = "java(toAudit(entity))")
    @Mapping(target = "accessPolicy", expression = "java(buildAccessPolicy(entity))")
    // delegate setters handled by the expression methods above — ignore to prevent auto-mapping conflicts
    @Mapping(target = "codLogin", ignore = true)
    @Mapping(target = "codSenha", ignore = true)
    @Mapping(target = "senhaAntiga", ignore = true)
    @Mapping(target = "numTentativasFalha", ignore = true)
    @Mapping(target = "dtExpiraSenha", ignore = true)
    @Mapping(target = "nomUsuario", ignore = true)
    @Mapping(target = "nomEmail", ignore = true)
    @Mapping(target = "codCpf", ignore = true)
    @Mapping(target = "codRg", ignore = true)
    @Mapping(target = "numTelefone", ignore = true)
    @Mapping(target = "desEndereco", ignore = true)
    @Mapping(target = "nomDepartamento", ignore = true)
    @Mapping(target = "nomCargo", ignore = true)
    @Mapping(target = "nomFuncao", ignore = true)
    @Mapping(target = "codEmpresa", ignore = true)
    @Mapping(target = "codClassificacaoPessoa", ignore = true)
    @Mapping(target = "codStatus", ignore = true)
    @Mapping(target = "dtCriacao", ignore = true)
    @Mapping(target = "dtModi", ignore = true)
    @Mapping(target = "dtUltimoAcesso", ignore = true)
    @Mapping(target = "numDiasSemanasPermitidos", ignore = true)
    @Mapping(target = "dtJornadaIni", ignore = true)
    @Mapping(target = "dtJornadaFim", ignore = true)
    @Mapping(target = "perfis", ignore = true)
    @Mapping(target = "grupos", ignore = true)
    @Mapping(target = "funcionalidadesDiretas", ignore = true)
    @Mapping(target = "gruposUsuario", ignore = true)
    @Mapping(target = "perfisUsuario", ignore = true)
    @Mapping(target = "funcionalidadesUsuario", ignore = true)
    User toDomain(UserEntityJpa entity);

    @Mapping(target = "oldSenha", source = "senhaAntiga")
    @Mapping(target = "codStatus", source = "codStatus", qualifiedByName = "statusToString")
    @Mapping(target = "codClassificacaoPessoa", expression = "java(user.getCodClassificacaoPessoa() != null ? user.getCodClassificacaoPessoa().getCodClassificacaoPessoa() : null)")
    @Mapping(target = "dt_jornada_ini", source = "dtJornadaIni")
    @Mapping(target = "dt_jornada_fim", source = "dtJornadaFim")
    @Mapping(target = "numTentativasFalha", expression = "java(java.util.Objects.requireNonNullElse(user.getNumTentativasFalha(), 0))")
    UserEntityJpa toEntity(User user);

    default Credentials toCredentials(UserEntityJpa entity) {
        return Credentials.builder()
                .codLogin(entity.getCodLogin())
                .codSenha(entity.getCodSenha())
                .senhaAntiga(entity.getOldSenha())
                .numTentativasFalha(Objects.requireNonNullElse(entity.getNumTentativasFalha(), 0))
                .dtExpiraSenha(entity.getDtExpiraSenha())
                .build();
    }

    default PersonalInfo toPersonalInfo(UserEntityJpa entity) {
        return PersonalInfo.builder()
                .nomUsuario(entity.getNomUsuario())
                .nomEmail(entity.getNomEmail())
                .codCpf(entity.getCodCpf())
                .codRg(entity.getCodRg())
                .numTelefone(entity.getNumTelefone())
                .desEndereco(entity.getDesEndereco())
                .nomDepartamento(entity.getNomDepartamento())
                .nomCargo(entity.getNomCargo())
                .nomFuncao(entity.getNomFuncao())
                .codEmpresa(entity.getCodEmpresa())
      
                .build();
    }

    default UserAudit toAudit(UserEntityJpa entity) {
        return UserAudit.builder()
                .codStatus(UserStatus.valueOfCode(entity.getCodStatus()))
                .dtCriacao(entity.getDtCriacao())
                .dtModi(entity.getDtModi())
                .dtUltimoAcesso(entity.getDtUltimoAcesso())
                .build();
    }

    default AccessPolicy buildAccessPolicy(UserEntityJpa entity) {
        String diasStr = entity.getNumDiasSemanasPermitidos();
        if (diasStr == null || diasStr.isBlank() || diasStr.equals("7")) {
            return AccessPolicy.semRestricao();
        }
        try {
            DayPattern dias = new DayPattern(diasStr);
            LocalTime inicio = entity.getDt_jornada_ini() != null ? entity.getDt_jornada_ini().toLocalTime() : null;
            LocalTime fim = entity.getDt_jornada_fim() != null ? entity.getDt_jornada_fim().toLocalTime() : null;
            return new AccessPolicy(dias, new TimeRange(inicio, fim));
        } catch (IllegalArgumentException e) {
            return AccessPolicy.semRestricao();
        }
    }

    @Named("statusToString")
    default String statusToString(UserStatus status) {
        return status != null ? status.getCode() : null;
    }
}

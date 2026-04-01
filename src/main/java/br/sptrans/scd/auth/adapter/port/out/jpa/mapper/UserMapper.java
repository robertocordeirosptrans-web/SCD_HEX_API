package br.sptrans.scd.auth.adapter.port.out.jpa.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.auth.domain.vo.AccessPolicy;
import br.sptrans.scd.auth.domain.vo.Credentials;
import br.sptrans.scd.auth.domain.vo.DayPattern;
import br.sptrans.scd.auth.domain.vo.PersonalInfo;
import br.sptrans.scd.auth.domain.vo.TimeRange;
import br.sptrans.scd.auth.domain.vo.UserAudit;

public class UserMapper {

    public static User toDomain(UserEntityJpa entity) {
        if (entity == null) return null;

        Credentials credentials = Credentials.builder()
                .codLogin(entity.getCodLogin())
                .codSenha(entity.getCodSenha())
                .senhaAntiga(entity.getOldSenha())
                .numTentativasFalha(Objects.requireNonNullElse(entity.getNumTentativasFalha(), 0))
                .dtExpiraSenha(entity.getDtExpiraSenha())
                .build();

        PersonalInfo personalInfo = PersonalInfo.builder()
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

        UserAudit audit = UserAudit.builder()
                .codStatus(UserStatus.valueOfCode(entity.getCodStatus()))
                .dtCriacao(entity.getDtCriacao())
                .dtModi(entity.getDtModi())
                .dtUltimoAcesso(entity.getDtUltimoAcesso())
                .build();

        AccessPolicy accessPolicy = buildAccessPolicy(entity);

        User user = new User();
        user.setIdUsuario(entity.getIdUsuario());
        user.setCredentials(credentials);
        user.setPersonalInfo(personalInfo);
        user.setAudit(audit);
        user.setAccessPolicy(accessPolicy);
        return user;
    }

    public static UserEntityJpa toEntity(User user) {
        if (user == null) return null;
        UserEntityJpa entity = new UserEntityJpa();
        entity.setIdUsuario(user.getIdUsuario());
        entity.setCodLogin(user.getCodLogin());
        entity.setCodSenha(user.getCodSenha());
        entity.setOldSenha(user.getSenhaAntiga());
        entity.setNumTentativasFalha(Objects.requireNonNullElse(user.getNumTentativasFalha(), 0));
        entity.setDtExpiraSenha(user.getDtExpiraSenha());
        entity.setNomUsuario(user.getNomUsuario());
        entity.setNomEmail(user.getNomEmail());
        entity.setCodCpf(user.getCodCpf());
        entity.setCodRg(user.getCodRg());
        entity.setNumTelefone(user.getNumTelefone());
        entity.setDesEndereco(user.getDesEndereco());
        entity.setNomDepartamento(user.getNomDepartamento());
        entity.setNomCargo(user.getNomCargo());
        entity.setNomFuncao(user.getNomFuncao());
        entity.setCodEmpresa(user.getCodEmpresa());
        entity.setCodStatus(user.getCodStatus() != null ? user.getCodStatus().getCode() : null);
        entity.setDtCriacao(user.getDtCriacao());
        entity.setDtModi(user.getDtModi());
        entity.setDtUltimoAcesso(user.getDtUltimoAcesso());
        if (user.getCodClassificacaoPessoa() != null) {
            entity.setCodClassificacaoPessoa(user.getCodClassificacaoPessoa().getCodClassificacaoPessoa());
        }
        if (user.getAccessPolicy() != null) {
            AccessPolicy ap = user.getAccessPolicy();
            entity.setNumDiasSemanasPermitidos(ap.getDiasPermitidos() != null ? ap.getDiasPermitidos().getPadrao() : null);
            if (ap.getJornadaHoraria() != null) {
                LocalTime ini = ap.getJornadaHoraria().getInicio();
                LocalTime fim = ap.getJornadaHoraria().getFim();
                entity.setDt_jornada_ini(ini != null ? LocalDate.now().atTime(ini) : null);
                entity.setDt_jornada_fim(fim != null ? LocalDate.now().atTime(fim) : null);
            }
        }
        return entity;
    }

    private static AccessPolicy buildAccessPolicy(UserEntityJpa entity) {
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
}

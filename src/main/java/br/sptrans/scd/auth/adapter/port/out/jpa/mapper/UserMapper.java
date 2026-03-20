package br.sptrans.scd.auth.adapter.port.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.UserEntityJpa;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;

public class UserMapper {
    public static User toDomain(UserEntityJpa entity) {
        if (entity == null) return null;
        User user = new User();
        user.setIdUsuario(entity.getIdUsuario());
        user.setCodSenha(entity.getCodSenha());
        user.setCodLogin(entity.getCodLogin());
        user.setNomUsuario(entity.getNomUsuario());
        user.setDesEndereco(entity.getDesEndereco());
        user.setNomDepartamento(entity.getNomDepartamento());
        user.setNomCargo(entity.getNomCargo());
        user.setNomFuncao(entity.getNomFuncao());
        user.setNumTelefone(entity.getNumTelefone());
        user.setDtCriacao(entity.getDtCriacao());
        user.setDtExpiraSenha(entity.getDtExpiraSenha());
        user.setDtUltimoAcesso(entity.getDtUltimoAcesso());
        user.setCodCpf(entity.getCodCpf());
        user.setCodRg(entity.getCodRg());
        user.setNomEmail(entity.getNomEmail());
        user.setCodEmpresa(entity.getCodEmpresa());
        user.setDt_jornada_ini(entity.getDt_jornada_ini());
        user.setDt_jornada_fim(entity.getDt_jornada_fim());
        user.setCodStatus(UserStatus.valueOfCode(entity.getCodStatus()));
        return user;
    }

    public static UserEntityJpa toEntity(User user) {
        if (user == null) return null;
        UserEntityJpa entity = new UserEntityJpa();
        entity.setIdUsuario(user.getIdUsuario());
        entity.setCodSenha(user.getCodSenha());
        entity.setCodLogin(user.getCodLogin());
        entity.setNomUsuario(user.getNomUsuario());
        entity.setDesEndereco(user.getDesEndereco());
        entity.setNomDepartamento(user.getNomDepartamento());
        entity.setNomCargo(user.getNomCargo());
        entity.setNomFuncao(user.getNomFuncao());
        entity.setNumTelefone(user.getNumTelefone());
        entity.setDtCriacao(user.getDtCriacao());
        entity.setDtExpiraSenha(user.getDtExpiraSenha());
        entity.setDtUltimoAcesso(user.getDtUltimoAcesso());
        entity.setCodCpf(user.getCodCpf());
        entity.setCodRg(user.getCodRg());
        entity.setNomEmail(user.getNomEmail());
        entity.setCodEmpresa(user.getCodEmpresa());
        entity.setDt_jornada_ini(user.getDt_jornada_ini());
        entity.setDt_jornada_fim(user.getDt_jornada_fim());
        entity.setCodClassificacaoPessoa(user.getCodClassificacaoPessoa().getCodClassificacaoPessoa());
        entity.setCodClassificacaoPessoa(user.getCodClassificacaoPessoa().getDesClassificacaoPessoa());
        return entity;
    }
}

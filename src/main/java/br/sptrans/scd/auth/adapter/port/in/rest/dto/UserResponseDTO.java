package br.sptrans.scd.auth.adapter.port.in.rest.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import br.sptrans.scd.auth.domain.User;

public record UserResponseDTO(
        String codLogin,
        String codStatus,
        LocalDateTime dtModi,
        String nomUsuario,
        String desEndereco,
        String nomDepartamento,
        String nomCargo,
        String nomFuncao,
        Long numTelefone,
        LocalDateTime dtCriacao,
        LocalDateTime dtExpiraSenha,
        LocalDateTime dtUltimoAcesso,
        LocalDateTime dt_jornada_ini,
        LocalDateTime dt_jornada_fim,
        String codCpf,
        String codRg,
        String nomEmail,
    String codEntidade,
        String codClassificacaoPessoa,
        String desClassificacaoPessoa,
        List<String> nomPerfis
        ) {

    public UserResponseDTO(User user, String desCanal) {
        this(
                user.getCodLogin(),
                user.getStatus() != null ? user.getStatus().getCode() : null,
                user.getDtModi(),
                user.getNomUsuario(),
                user.getDesEndereco(),
                user.getNomDepartamento(),
                user.getNomCargo(),
                user.getNomFuncao(),
                user.getNumTelefone(),
                user.getDtCriacao(),
                user.getDtExpiraSenha(),
                user.getDtUltimoAcesso(),
                user.getDt_jornada_ini() != null ? user.getDt_jornada_ini().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                user.getDt_jornada_fim() != null ? user.getDt_jornada_fim().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                user.getCodCpf(),
                user.getCodRg(),
                user.getNomEmail(),
                user.getCodEmpresa(),
                user.getCodClassificacaoPessoa() != null ? user.getCodClassificacaoPessoa().getCodClassificacaoPessoa() : null,
                user.getCodClassificacaoPessoa() != null ? user.getCodClassificacaoPessoa().getDesClassificacaoPessoa() : null,
                user.getPerfisUsuario() != null ? user.getPerfisUsuario().stream()
                        .filter(up -> "A".equals(up.getCodStatus()))
                        .map(up -> up.getPerfil().getNomPerfil())
                        .collect(Collectors.toList()) : List.of()
        );
    }
}

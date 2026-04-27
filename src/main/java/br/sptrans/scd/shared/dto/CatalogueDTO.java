package br.sptrans.scd.shared.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO;

public class CatalogueDTO {
    private String codigo;
    private String descricao;
    private String codStatus;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private UserSimpleDTO usuarioCadastro;
    private UserSimpleDTO usuarioManutencao;

    // Getters and Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCodStatus() { return codStatus; }
    public void setCodStatus(String codStatus) { this.codStatus = codStatus; }

    public LocalDateTime getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDateTime dtCadastro) { this.dtCadastro = dtCadastro; }

    public LocalDateTime getDtManutencao() { return dtManutencao; }
    public void setDtManutencao(LocalDateTime dtManutencao) { this.dtManutencao = dtManutencao; }

    public UserSimpleDTO getUsuarioCadastro() { return usuarioCadastro; }
    public void setUsuarioCadastro(UserSimpleDTO usuarioCadastro) { this.usuarioCadastro = usuarioCadastro; }

    public UserSimpleDTO getUsuarioManutencao() { return usuarioManutencao; }
    public void setUsuarioManutencao(UserSimpleDTO usuarioManutencao) { this.usuarioManutencao = usuarioManutencao; }
}

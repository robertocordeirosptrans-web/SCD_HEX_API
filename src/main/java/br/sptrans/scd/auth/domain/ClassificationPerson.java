package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codClassificacaoPessoa")
public class ClassificationPerson {

    private String codClassificacaoPessoa;
    private String desClassificacaoPessoa;
    private String flgVenda;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String stClassificacoesPessoa;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;

    public String getDesClassificacaoPessoa() {
        return desClassificacaoPessoa;
    }

    public void setDesClassificacaoPessoa(String desClassificacaoPessoa) {
        this.desClassificacaoPessoa = desClassificacaoPessoa;
    }

    public String getFlgVenda() {
        return flgVenda;
    }

    public void setFlgVenda(String flgVenda) {
        this.flgVenda = flgVenda;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public LocalDateTime getDtManutencao() {
        return dtManutencao;
    }

    public void setDtManutencao(LocalDateTime dtManutencao) {
        this.dtManutencao = dtManutencao;
    }

    public String getStClassificacoesPessoa() {
        return stClassificacoesPessoa;
    }

    public void setStClassificacoesPessoa(String stClassificacoesPessoa) {
        this.stClassificacoesPessoa = stClassificacoesPessoa;
    }

    public User getIdUsuarioCadastro() {
        return idUsuarioCadastro;
    }

    public void setIdUsuarioCadastro(User idUsuarioCadastro) {
        this.idUsuarioCadastro = idUsuarioCadastro;
    }

    public User getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public void setIdUsuarioManutencao(User idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public String getCodClassificacaoPessoa() {
        return codClassificacaoPessoa;
    }

    public void setCodClassificacaoPessoa(String codClassificacaoPessoa) {
        this.codClassificacaoPessoa = codClassificacaoPessoa;
    }



}
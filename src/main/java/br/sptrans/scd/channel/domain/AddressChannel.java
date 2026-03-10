package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressChannel {

    private String codEndereco;
    private String codEmpregador;
    private String desLogradouro;
    private String codFornecedor;
    private String codTipoEndereco;
    private String codCEP;
    private String desBairro;
    private String desCidade;
    private String desUF;
    private Integer numDDD;
    private Integer numFone;
    private Integer numFax;
    private String desObs;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String stEnderecos;
    private LocalDateTime dtValidade;
    private Integer codSeq;
    private String desNumero;
    private User idUsuarioManutencao;
    private User idUsuarioCadastro;
    private SalesChannel codCanal;
}

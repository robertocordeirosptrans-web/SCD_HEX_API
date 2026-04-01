package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactChannel {
    private String codContato;

    private String codFornecedor;

    private String codEmpregador;

    private String desContato;

    private String desEmailContato;

    private Integer numDDD;

    private Integer numFone;

    private Integer numFoneRamal;

    private Integer numFax;

    private Integer numFaxRamal;

    private String stEntidadeContato;

    private String desComentarios;

    private String codTipoDocumento;

    private String codDocumento;

    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;

    private User idUsuarioManutencao;

    private User idUsuarioCadastro;

    private SalesChannel codCanal;
}

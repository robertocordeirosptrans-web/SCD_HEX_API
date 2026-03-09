package br.sptrans.scd.channel.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesChannel {

    private String codCanal;

    private String codDocumento;

    private String codCanalSuperior;

    private String desCanal;

    private String codTipoDocumento;

    private LocalDateTime dtManutencao;

    private String desRazaoSocial;

    private String stCanais;

    private String desNomeFantasia;

    private LocalDateTime dtCadastro;

    private BigDecimal vlCaucao;

    private LocalDate dtInicioCaucao;

    private LocalDate dtFimCaucao;

    private Integer seqNivel;

    private String flgCriticaNumlote;

    private Integer flgLimiteDias;

    private String flgProcessamentoAutomatico;

    private String flgProcessamentoParcial;

    private String flgSaldoDevedor;

    private Integer numMinutoIniLibRecarga;

    private Integer numMinutoFimLibRecarga;

    private String flgEmiteReciboPedido;

    private String flgSupercanal;

    private String flgPagtoFuturo;

    private ClassificationPerson codClassificacaoPessoa;

    private TypesActivity codAtividade;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;
}

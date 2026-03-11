package br.sptrans.scd.creditrequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreditRequest {

    private Long numSolicitacao;

    private String codCanal;

    private Long idUsuarioCadastro;

    private String codTipoDocumento;

    private String codSituacao;

    private String codFormaPagto;

    private LocalDateTime dtSolicitacao;

    private LocalDateTime dtPrevLiberacao;

    private LocalDateTime dtAceite;

    private LocalDateTime dtConfirmaPagto;

    private LocalDateTime dtPagtoEconomica;

    private String codUsuarioPortador;

    private LocalDateTime dtLiberacaoEfetiva;

    private String codEnderecoEntrega;

    private String numLote;

    private LocalDateTime dtFinanceira;

    private BigDecimal vlTotal;

    private LocalDateTime dtCadastro;

    private String flgCanc;

    private LocalDateTime dtManutencao;

    private LocalDateTime dtEnvioHm;

    private Long idUsuarioManutencao;

    private String flgBloq;

    private BigDecimal vlPago;

    private Long sqPid;

    private LocalDateTime dtInicProcesso;

    private BigDecimal vlServicoRecarga;

    private BigDecimal vlServicoAdm;

    private String flgEvento;

    private BigDecimal vlEvento;

    private List<CreditRequestItems> itens = new ArrayList<>();
}

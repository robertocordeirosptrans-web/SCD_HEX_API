package br.sptrans.scd.creditrequest.application.port.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditRequestDTO {
    private Long numSolicitacao;
    private String codCanal;
    private Long idUsuarioCadastro;
    private String codTipoDocumento;
    private String codSituacao;
    private String codFormaPagto;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtSolicitacao;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtPrevLiberacao;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtAceite;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtConfirmaPagto;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtPagtoEconomica;
    private String codUsuarioPortador;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtLiberacaoEfetiva;
    private String codEnderecoEntrega;
    private String numLote;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtFinanceira;
    private BigDecimal vlTotal;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtCadastro;
    private String flgCanc;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtManutencao;
    private Long idUsuarioManutencao;
    private String flgBloq;
    private BigDecimal vlPago;
    // Lista de itens do pedido
    private List<CreditRequestItemsDTO> itens;
}
    
   

package br.sptrans.scd.creditrequest.application.port.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateRequestCredit(
        @NotBlank(message = "Código do canal é obrigatório")
        String codCanal,
        @NotBlank(message = "Número do lote é obrigatório")
        String numLote,
        @NotNull(message = "Data de geração é obrigatória")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
        LocalDateTime dataGeracao,
        @NotNull(message = "Data de liberação de crédito é obrigatória")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
        LocalDateTime dataLiberacaoCredito,
        @NotBlank(message = "Responsável é obrigatório")
        String responsavel,
        @NotNull(message = "Lista de pedidos é obrigatória")
        @Size(min = 1, message = "Ao menos um pedido deve ser informado")
        @Valid
        List<CreditRequest> pedidos,
        @NotNull(message = "Lista de itens é obrigatória")
        @Size(min = 1, message = "Ao menos um item deve ser informado")
        @Valid
        List<ItemRequest> itens
        ) {

    public record CreditRequest(
            @NotNull(message = "Número da solicitação é obrigatório")
            Long numSolicitacao,
            @NotBlank(message = "Canal de distribuição é obrigatório")
            String canaisDistribuicao
            ) {

    }

    public record ItemRequest(
            @NotNull(message = "ID do usuário cartão é obrigatório")
            Long idUsuarioCartao,
            @NotBlank(message = "Número lógico do cartão é obrigatório")
            String numLogicoCartao,
            @NotNull(message = "Código do produto é obrigatório")
            String codProduto,
            @NotNull(message = "Código da versão é obrigatório")
            String codVersao,
            @NotNull(message = "Valor unitário é obrigatório")
            @Positive(message = "Valor unitário deve ser positivo")
            BigDecimal vlUnitario,
            @NotNull(message = "Valor total é obrigatório")
            @Positive(message = "Valor total deve ser positivo")
            BigDecimal valorTotal,
            @NotNull(message = "Taxa de serviço é obrigatória")
            @DecimalMin(value = "0.0", message = "Taxa de serviço não pode ser negativa")
            BigDecimal vlTxserv,
            @NotNull(message = "Taxa administrativa é obrigatória")
            @DecimalMin(value = "0.0", message = "Taxa administrativa não pode ser negativa")
            BigDecimal vlTxadm
            ) {

    }

}

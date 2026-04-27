package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVersionDetailResponseDTO {
    private String codVersao;
    private String codProduto;
    private LocalDateTime dtValidade;
    private LocalDateTime dtVidaInicio;
    private LocalDateTime dtVidaFim;
    private LocalDateTime dtLiberacao;
    private LocalDateTime dtLancamento;
    private LocalDateTime dtVendaInicio;
    private LocalDateTime dtVendaFim;
    private LocalDateTime dtUsoIni;
    private LocalDateTime dtUsoFim;
    private LocalDateTime dtTrocaIni;
    private LocalDateTime dtTrocaFim;
    private String flgBloqFabricacao;
    private String flgBloqVenda;
    private String flgBloqDistribuicao;
    private String flgBloqTroca;
    private String flgBloqAquisicao;
    private String flgBloqPedido;
    private String flgBloqDevolucao;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String stProdutosVersoes;
    private String desProdutoVersoes;
    private UserSimpleDTO usuarioCadastro;
    private UserSimpleDTO usuarioManutencao;
}

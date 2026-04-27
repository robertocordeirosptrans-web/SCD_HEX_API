package br.sptrans.scd.initializedcards.adapter.in.rest.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CriarSolicitacaoRequest {

    @NotBlank @Size(max = 20)
    private String codTipoCanal;

    @NotBlank @Size(max = 20)
    private String codCanal;

    @Size(max = 20)
    private String codAdquirente;

    @Size(max = 20)
    private String codProduto;

    @NotNull
    private Long qtdSolicitada;

    @NotBlank @Size(max = 1)
    private String flgTipoSaida;

    @Size(max = 1)
    private String flgTipoVolume;

    @Size(max = 1)
    private String flgAssociacaoUsuario;

    @Size(max = 1)
    private String flgGeraArquivo;

    @Size(max = 1)
    private String flgRespEntregaRetirada;

    @Size(max = 60)
    private String desNomeRespEntrega;

    @Size(max = 3)
    private String codTipoDoctoRespEntrega;

    @Size(max = 20)
    private String codDoctoRespEntrega;

    @Size(max = 20)
    private String codEnderecoEntrega;

    @NotNull
    private LocalDateTime dtPrevistaEntrega;
}

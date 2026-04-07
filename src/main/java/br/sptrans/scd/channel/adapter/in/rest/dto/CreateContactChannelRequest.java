package br.sptrans.scd.channel.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContactChannelRequest(
        @NotBlank @Size(max = 20) String codContato,
        @Size(max = 20) String codFornecedor,
        @Size(max = 20) String codEmpregador,
        @NotBlank @Size(max = 60) String desContato,
        @Size(max = 60) String desEmailContato,
        Integer numDDD,
        Integer numFone,
        Integer numFoneRamal,
        Integer numFax,
        Integer numFaxRamal,
        @Size(max = 1) String stEntidadeContato,
        @Size(max = 60) String desComentarios,
        @Size(max = 4) String codTipoDocumento,
        @Size(max = 20) String codDocumento,
        @NotBlank @Size(max = 20) String codCanal
) {}
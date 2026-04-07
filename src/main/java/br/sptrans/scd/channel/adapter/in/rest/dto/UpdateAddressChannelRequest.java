package br.sptrans.scd.channel.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAddressChannelRequest(
        @Size(max = 20) String codEmpregador,
        @NotBlank @Size(max = 60) String desLogradouro,
        @Size(max = 20) String codFornecedor,
        @Size(max = 20) String codTipoEndereco,
        @Size(max = 20) String codCEP,
        @Size(max = 60) String desBairro,
        @Size(max = 60) String desCidade,
        @Size(max = 60) String desUF,
        Integer numDDD,
        Integer numFone,
        Integer numFax,
        @Size(max = 1024) String desObs,
        @Size(max = 1) String stEnderecos,
        @Size(max = 10) String desNumero,
        @NotBlank @Size(max = 20) String codCanal
) {}
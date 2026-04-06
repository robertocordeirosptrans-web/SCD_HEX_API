package br.sptrans.scd.channel.adapter.port.in.rest.dto;

public record UpdateAddressChannelRequest(
        String codEmpregador,
        String desLogradouro,
        String codFornecedor,
        String codTipoEndereco,
        String codCEP,
        String desBairro,
        String desCidade,
        String desUF,
        Integer numDDD,
        Integer numFone,
        Integer numFax,
        String desObs,
        String stEnderecos,
        String desNumero,
        String codCanal) {
}
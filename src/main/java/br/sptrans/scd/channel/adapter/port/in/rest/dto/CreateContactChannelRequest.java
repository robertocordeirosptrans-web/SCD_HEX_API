package br.sptrans.scd.channel.adapter.port.in.rest.dto;

public record CreateContactChannelRequest(
        String codContato,
        String codFornecedor,
        String codEmpregador,
        String desContato,
        String desEmailContato,
        Integer numDDD,
        Integer numFone,
        Integer numFoneRamal,
        Integer numFax,
        Integer numFaxRamal,
        String stEntidadeContato,
        String desComentarios,
        String codTipoDocumento,
        String codDocumento,
        String codCanal) {
}
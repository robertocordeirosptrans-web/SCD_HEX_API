package br.sptrans.scd.channel.adapter.in.rest.dto;

public record SalesChannelFilterRequest(
        String codDocumento,
        String stCanais,
        String vlCaucao,
        String dtInicioCaucao,
        String dtFimCaucao,
        String codCanalSuperior) {

    public SalesChannelFilterRequest {
        // Valores padrão para nulos
        codDocumento = codDocumento != null ? codDocumento : "";
        stCanais = stCanais != null ? stCanais : "";
        vlCaucao = vlCaucao != null ? vlCaucao : "";
        dtInicioCaucao = dtInicioCaucao != null ? dtInicioCaucao : "";
        dtFimCaucao = dtFimCaucao != null ? dtFimCaucao : "";
        codCanalSuperior = codCanalSuperior != null ? codCanalSuperior : "";
    }
}
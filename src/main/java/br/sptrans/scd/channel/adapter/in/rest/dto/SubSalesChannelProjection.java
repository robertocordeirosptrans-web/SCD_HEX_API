package br.sptrans.scd.channel.adapter.in.rest.dto;

public interface SubSalesChannelProjection {
    String getCodCanal();
    String getDesCanal();
    String getCodCanalSuperior();
    String getStCanal();
    // Adicione outros getters conforme os campos necessários da tabela CANAIS
}

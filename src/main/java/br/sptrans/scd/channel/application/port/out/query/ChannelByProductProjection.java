package br.sptrans.scd.channel.application.port.out.query;

import java.time.LocalDateTime;

public interface ChannelByProductProjection {
    String getCodCanal();
    String getDesCanal();
    LocalDateTime getDtInicioValidade();
    LocalDateTime getDtFimValidade();
    String getCodStatus();
}

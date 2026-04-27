package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ChannelByProductDTO(
        String codCanal,
        String desCanal,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dtInicioValidade,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dtFimValidade,
        String codStatus) {
}

package br.sptrans.scd.channel.adapter.port.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SalesChannelReponseDTO(
        String codCanal,
        String codDocumento,
        String codCanalSuperior,
        String desCanal,
        String codTipoDocumento,
        LocalDateTime dtManutencao,
        String desRazaoSocial,
        String stCanais,
        String desNomeFantasia,
        LocalDateTime dtCadastro,
        BigDecimal vlCaucao,
        LocalDate dtInicioCaucao,
        LocalDate dtFimCaucao,
        Integer seqNivel,
        String flgCriticaNumlote,
        Integer flgLimiteDias,
        String flgProcessamentoAutomatico,
        String flgProcessamentoParcial,
        String flgSaldoDevedor,
        Integer numMinutoIniLibRecarga,
        Integer numMinutoFimLibRecarga,
        String flgEmiteReciboPedido,
        String flgSupercanal,
        String flgPagtoFuturo,
        // Atividade relacionada (descrição)
        String codAtividade,
        String desAtividade,
        // Dados relacionados
        String codClassificacaoPessoa,
        String desClassificacaoPessoa,
        // Usuários
        String usuarioCadastro,
        String usuarioManutencao,
        // Usuários detalhados (apenas id, login e nome)
        UserSimpleDTO usuarioCadastroInfo,
        UserSimpleDTO usuarioManutencaoInfo
        ) {

}

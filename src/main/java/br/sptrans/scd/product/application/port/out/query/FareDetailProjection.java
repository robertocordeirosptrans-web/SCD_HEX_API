package br.sptrans.scd.product.application.port.out.query;

import java.time.LocalDateTime;

public interface FareDetailProjection {
    String getCodTarifa();
    String getCodProduto();
    String getNomProduto();
    String getCodVersao();
    LocalDateTime getDtVigenciaIni();
    LocalDateTime getDtVigenciaFim();
    LocalDateTime getDtCadastro();
    LocalDateTime getDtManutencao();
    String getDesTarifa();
    String getStTarifas();
    Integer getVlTarifa();
    Long getIdUsuarioCadastro();
    String getLoginCadastro();
    String getNomeCadastro();
    Long getIdUsuarioManutencao();
    String getLoginManutencao();
    String getNomeManutencao();
}

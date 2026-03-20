package br.sptrans.scd.channel.adapter.port.out.jpa.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProductChannelProjection {
    // Informações básicas
    Integer getCodCanal();
    String getCodProduto();
    String getDesProduto();
    String getStatusProduto();
    String getStatusCanalProduto();

    // Informações do canal-produto
    Integer getCodConvenio();
    Integer getCodOrgaoEmissor();
    Integer getQtdLimiteComercializacao();
    Integer getQtdMinimaEstoque();
    Integer getQtdMaximaEstoque();
    Integer getQtdMinimaRessuprimento();
    Integer getQtdMaximaRessuprimento();
    Integer getVlFace();
    Integer getTipoOperHM();
    String getFlgCarac();

    // Canais de Destino
    String getCanaisDestino();

    // Limites de recarga
    LocalDateTime getDtInicioValidadeLimite();
    LocalDateTime getDtFimValidadeLimite();
    BigDecimal getVlMinimoRecarga();
    BigDecimal getVlMaximoRecarga();
    BigDecimal getVlMaximoSaldo();
    String getStatusLimite();

    // Taxas
    Integer getIdTaxa();
    LocalDateTime getTaxaInicio();
    LocalDateTime getTaxaFim();
    String getDscTaxa();

    // Taxa administrativa
    BigDecimal getTaxaAdmRecInicial();
    BigDecimal getTaxaAdmRecFinal();
    BigDecimal getTaxaAdmValFixo();
    BigDecimal getTaxaAdmPercentual();

    // Taxa de serviço
    BigDecimal getTaxaServRecInicial();
    BigDecimal getTaxaServRecFinal();
    BigDecimal getTaxaServValFixo();
    BigDecimal getTaxaServPercentual();
    BigDecimal getTaxaServValMinimo();

    // Taxa por canal
    LocalDateTime getTaxaCanalInicio();
    LocalDateTime getTaxaCanalFim();
    BigDecimal getTaxaCanalVlInicio();
    BigDecimal getTaxaCanalVlFinal();
    BigDecimal getTaxaCanalPercentual();

    // Convênio vigência
    LocalDateTime getInicioValidade();
    LocalDateTime getFimValidade();
    String getStatusVigencia();
}

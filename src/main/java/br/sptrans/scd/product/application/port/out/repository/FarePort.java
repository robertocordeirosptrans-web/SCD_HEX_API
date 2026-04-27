package br.sptrans.scd.product.application.port.out.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.application.port.out.query.FareDetailProjection;
import br.sptrans.scd.product.domain.Fare;

public interface FarePort {
    Optional<Fare> findById(String codTarifa);
    Fare save(Fare tarifa);
    void extendsValidity(String codTarifa, LocalDateTime dtFim, Long idUsuario);
    List<Fare> listByProductChannel(String codProduto, String codCanal);
    boolean isConflictValidity(String codProduto, String codCanal, LocalDateTime dtInicio, LocalDateTime dtFim, Long excluirIdTaxa);
    Optional<Fare> searchCurrent(String codProduto, String codCanal, LocalDateTime dataOperacao);
    Page<FareDetailProjection> listDetailByProduct(String codProduto, Pageable pageable);
}

package br.sptrans.scd.product.application.port.out.repository;

import java.util.Optional;

import br.sptrans.scd.product.domain.ServiceFee;

public interface ServiceFeePort {
    Optional<ServiceFee> findSrvById(Long codTaxaSrv);
    ServiceFee save(ServiceFee taxasServico);
    boolean existsSrvById(Long codTaxaSrv);
}

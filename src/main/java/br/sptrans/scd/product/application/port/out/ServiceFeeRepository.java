package br.sptrans.scd.product.application.port.out;

import java.util.Optional;

import br.sptrans.scd.product.domain.ServiceFee;

public interface ServiceFeeRepository {

    /**
     * Busca uma taxa de serviço pelo ID.
     *
     * @param codTaxaSrv ID da taxa de serviço
     * @return Optional contendo a taxa ou vazio se não encontrada
     */
    Optional<ServiceFee> findSrvById(Long codTaxaSrv);

    /**
     * Salva ou atualiza uma taxa de serviço.
     *
     * @param taxasServico Taxa de serviço a ser salva
     * @return Taxa de serviço salva
     */
    ServiceFee save(ServiceFee taxasServico);

    /**
     * Verifica se uma taxa de serviço existe pelo ID.
     *
     * @param codTaxaSrv ID da taxa de serviço
     * @return true se existe, false caso contrário
     */
    boolean existsSrvById(Long codTaxaSrv);
}

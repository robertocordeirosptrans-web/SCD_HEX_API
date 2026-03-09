package br.sptrans.scd.product.application.port.out;

import java.util.Optional;

import br.sptrans.scd.product.domain.AdministrativeFee;

public interface  AdministrativeFeeRepository {
     /**
     * Busca uma taxa administrativa pelo ID.
     *
     * @param codTaxaAdm ID da taxa administrativa
     * @return Optional contendo a taxa ou vazio se não encontrada
     */
    Optional<AdministrativeFee> findAdmById(Long codTaxaAdm);

    /**
     * Salva ou atualiza uma taxa administrativa.
     *
     * @param taxasAdm Taxa administrativa a ser salva
     * @return Taxa administrativa salva
     */
    AdministrativeFee save(AdministrativeFee taxasAdm);


}

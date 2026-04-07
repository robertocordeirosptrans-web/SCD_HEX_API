package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.PaymentMethod;

public interface PaymentMethodPort {
    /**
     * Retorna todas as formas de pagamento cadastradas.
     */
    List<PaymentMethod> findAll();

    /**
     * Busca uma única forma de pagamento pelo código.
     */
    Optional<PaymentMethod> findById(String codFormaPagto);
}

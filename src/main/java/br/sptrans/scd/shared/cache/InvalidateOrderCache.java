package br.sptrans.scd.shared.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.CacheEvict;

/**
 * Meta-annotation para garantir invalidação consistente de cache de pedidos de crédito.
 * 
 * Invalida todos os caches relacionados a pedidos quando um método de escrita
 * é executado, prevenindo dados desatualizados de listas e contagens.
 * 
 * Uso:
 * <pre>
 * @InvalidateOrderCache
 * public CreateRequestResponse createCreditRequest(CreateRequestCredit request, ...) { ... }
 * 
 * @InvalidateOrderCache
 * public void liberarRecarga(ReleaseRechargeCommand comando) { ... }
 * </pre>
 * 
 * Caches invalidados:
 * - "pedidos": Cache de pedidos de crédito (listagem, paginação)
 * - "order-list": Cache de listagem de pedidos
 * 
 * @author SCD Architecture Team
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CacheEvict(value = {"pedidos", "order-list"}, allEntries = true)
public @interface InvalidateOrderCache {
}

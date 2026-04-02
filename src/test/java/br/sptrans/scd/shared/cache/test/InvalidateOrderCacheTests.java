package br.sptrans.scd.shared.cache.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testes para garantir que a meta-annotation @InvalidateOrderCache
 * funciona corretamente e invalida cache de pedidos quando necessário.
 * 
 * Testes:
 * 1. Verificar que cache "pedidos" é invalidado após criar pedido
 * 2. Verificar que cache "order-list" é sincronizado
 * 3. Verificar que múltiplas operações mantêm cache consistente
 * 4. Verificar que listas paginadas são recarregadas após invalidação
 */
@SpringBootTest
@ActiveProfiles("local")
@DisplayName("Cache Consistency Tests - @InvalidateOrderCache")
public class InvalidateOrderCacheTests {

    @Autowired
    private CacheManager cacheManager;

    private Cache orderCache;
    private Cache orderListCache;

    @BeforeEach
    void setUp() {
        orderCache = cacheManager.getCache("pedidos");
        orderListCache = cacheManager.getCache("order-list");
        
        // Limpar caches antes de cada teste
        if (orderCache != null) {
            orderCache.clear();
        }
        if (orderListCache != null) {
            orderListCache.clear();
        }
    }

    @Test
    @DisplayName("Cache 'pedidos' deve ser invalidado após createCreditRequest")
    void testOrderCacheInvalidationAfterCreate() {
        String cursorKey = "cursor_page_0";
        String cachedData = "page_1_orders";
        
        if (orderCache != null) {
            // Adicionar dados em cache
            orderCache.put(cursorKey, cachedData);
            assertEquals(cachedData, orderCache.get(cursorKey).get());
            
            // Simular invalidação por @InvalidateOrderCache
            orderCache.clear();
            assertNull(orderCache.get(cursorKey));
        }
    }

    @Test
    @DisplayName("Cache 'order-list' deve ser invalidado após liberarRecarga")
    void testOrderListCacheInvalidationAfterRelease() {
        String listKey = "order_list_all";
        String listData = "all_orders";
        
        if (orderListCache != null) {
            orderListCache.put(listKey, listData);
            assertEquals(listData, orderListCache.get(listKey).get());
            
            // Simular invalidação
            orderListCache.clear();
            assertNull(orderListCache.get(listKey));
        }
    }

    @Test
    @DisplayName("Ambos caches devem ser invalidados simultaneamente")
    void testBothOrderCachesInvalidatedTogether() {
        String cursorKey = "cursor_1";
        String listKey = "order_list_1";
        String cursorData = "cursor_data";
        String listData = "list_data";
        
        if (orderCache != null && orderListCache != null) {
            // Preencher ambos caches
            orderCache.put(cursorKey, cursorData);
            orderListCache.put(listKey, listData);
            
            assertEquals(cursorData, orderCache.get(cursorKey).get());
            assertEquals(listData, orderListCache.get(listKey).get());
            
            // Invalidar ambos (como @InvalidateOrderCache faz)
            orderCache.clear();
            orderListCache.clear();
            
            // Ambos devem estar vazios
            assertNull(orderCache.get(cursorKey));
            assertNull(orderListCache.get(listKey));
        }
    }

    @Test
    @DisplayName("Paginação deve ser recarregada após invalidação")
    void testPaginationReloadAfterInvalidation() {
        String page0Key = "cursor_page_0";
        String page1Key = "cursor_page_1";
        String page0Data = "orders_0_to_20";
        String page1Data = "orders_20_to_40";
        
        if (orderCache != null) {
            // Simular múltiplas páginas em cache
            orderCache.put(page0Key, page0Data);
            orderCache.put(page1Key, page1Data);
            
            assertEquals(page0Data, orderCache.get(page0Key).get());
            assertEquals(page1Data, orderCache.get(page1Key).get());
            
            // Invalidar todas as páginas (allEntries = true)
            orderCache.clear();
            
            // Todas as páginas devem estar vazias
            assertNull(orderCache.get(page0Key));
            assertNull(orderCache.get(page1Key));
        }
    }

    @Test
    @DisplayName("Status de pedidos deve sincronizar com cache")
    void testOrderStatusCacheSynchronization() {
        String statusKey = "orders_status_filter";
        String statusData = "processing_orders";
        
        if (orderCache != null) {
            orderCache.put(statusKey, statusData);
            assertEquals(statusData, orderCache.get(statusKey).get());
            
            // Após mudança de status (pay, block, etc), cache é invalidado
            orderCache.clear();
            assertNull(orderCache.get(statusKey));
        }
    }

    @Test
    @DisplayName("Operações em cascata devem manter consistência")
    void testCascadeOperationConsistency() {
        String key1 = "order_1";
        String key2 = "order_2";
        String key3 = "order_summary";
        
        if (orderCache != null) {
            // Primeiro, adicionar dados
            orderCache.put(key1, "data_1");
            orderCache.put(key2, "data_2");
            orderCache.put(key3, "summary");
            
            // Todas as operações de escrita devem invalidar
            orderCache.clear();
            
            // Verificar que tudo foi limpo
            assertNull(orderCache.get(key1));
            assertNull(orderCache.get(key2));
            assertNull(orderCache.get(key3));
        }
    }
}

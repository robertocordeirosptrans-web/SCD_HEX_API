package br.sptrans.scd.creditrequest.adapter.port.out.jpa.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;

@ExtendWith(MockitoExtension.class)
class LiminarGatewayJpaTest {

    @InjectMocks
    private LiminarGatewayJpa liminarGatewayJpa;

    @Mock
    private EntityManager em;

    @Mock
    private StoredProcedureQuery query;

    @Test
    void empresaPossuiIsencaoTaxa_retornaTrue_quandoResultado1() {
        when(em.createStoredProcedureQuery(anyString())).thenReturn(query);
        when(query.registerStoredProcedureParameter(anyInt(), any(), any())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        when(query.execute()).thenReturn(true);
        when(query.getOutputParameterValue(2)).thenReturn(1);

        boolean result = liminarGatewayJpa.empresaPossuiIsencaoTaxa("2200191");
        assertTrue(result);
    }

    @Test
    void empresaPossuiIsencaoTaxa_retornaFalse_quandoResultado0() {
        when(em.createStoredProcedureQuery(anyString())).thenReturn(query);
        when(query.registerStoredProcedureParameter(anyInt(), any(), any())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        when(query.execute()).thenReturn(true);
        when(query.getOutputParameterValue(2)).thenReturn(0);

        boolean result = liminarGatewayJpa.empresaPossuiIsencaoTaxa("2200191");
        assertFalse(result);
    }

    @Test
    void empresaPossuiIsencaoTaxa_retornaFalse_quandoExcecao() {
        when(em.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("erro"));

        boolean result = liminarGatewayJpa.empresaPossuiIsencaoTaxa("2200191");
        assertFalse(result);
    }

    @Test
    void verificarLiminarCartao_retornaZero_quandoLiminarEmpresaZero() {
        int result = liminarGatewayJpa.verificarLiminarCartao(0, "230894958");
        // Quando liminarEmpresa == 0, deve retornar 0 sem consultar o banco
        assertTrue(result == 0);
    }

    @Test
    void verificarLiminarCartao_retornaValorCorreto_quandoSucesso() {
        when(em.createStoredProcedureQuery(anyString())).thenReturn(query);
        when(query.registerStoredProcedureParameter(anyInt(), any(), any())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        when(query.execute()).thenReturn(true);
        when(query.getOutputParameterValue(2)).thenReturn(1);

        int result = liminarGatewayJpa.verificarLiminarCartao(1, "230894958");
        assertTrue(result == 1);
    }

    @Test
    void verificarLiminarCartao_retornaZero_quandoExcecao() {
        when(em.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("erro"));
        int result = liminarGatewayJpa.verificarLiminarCartao(1, "230894958");
        assertTrue(result == 0);
    }

    @Test
    void existeLiminar_retornaTrue_quandoCartaoTemLiminar() {
        LiminarGatewayJpa spyGateway = org.mockito.Mockito.spy(liminarGatewayJpa);
        org.mockito.Mockito.doReturn(1).when(spyGateway).verificarLiminarEmpresa("115167");
        org.mockito.Mockito.doReturn(1).when(spyGateway).verificarLiminarCartao(1, "230894958");
        boolean result = spyGateway.existeLiminar("152", "230894958");
        assertTrue(result);
    }

    @Test
    void existeLiminar_retornaFalse_quandoCartaoNaoTemLiminar() {
        LiminarGatewayJpa spyGateway = org.mockito.Mockito.spy(liminarGatewayJpa);
        org.mockito.Mockito.doReturn(1).when(spyGateway).verificarLiminarEmpresa("115167");
        org.mockito.Mockito.doReturn(0).when(spyGateway).verificarLiminarCartao(1, "230894958");
        boolean result = spyGateway.existeLiminar("152", "230894958");
        assertFalse(result);
    }

    @Test
    void existeLiminar_retornaFalse_quandoExcecao() {
        LiminarGatewayJpa spyGateway = org.mockito.Mockito.spy(liminarGatewayJpa);
        org.mockito.Mockito.doThrow(new RuntimeException("erro")).when(spyGateway).verificarLiminarEmpresa("115167");
        boolean result = spyGateway.existeLiminar("152", "230894958");
        assertFalse(result);
    }
}

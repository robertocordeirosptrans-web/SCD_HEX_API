package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelPersistencePort;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRDPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.EventoFinanceiroPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.HmPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.CreditRequestRD;
import br.sptrans.scd.product.application.port.out.gateway.LiminarGateway;
import br.sptrans.scd.shared.helper.StatusConsolidationHelper;

@ExtendWith(MockitoExtension.class)
class ProcessRechargeServiceTest {

    @Mock private CreditRequestItemsPort itemRepository;
    @Mock private MarketingDistribuitionChannelPersistencePort marketingDistribuitionChannelPersistencePort;
    @Mock private CreditRequestRDPort creditRequestRDPort;
    @Mock private HistCreditRequestService historyService;
    @Mock private StatusConsolidationHelper statusConsolidationHelper;
    @Mock private LiminarGateway liminarGateway;
    @Mock private EventoFinanceiroPort eventoFinanceiroPort;
    @Mock private HmPort hmPort;

    @InjectMocks
    private ProcessRechargeService processRechargeService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CreditRequestItems buildItem(Long numSolicitacao, String codCanal) {
        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(numSolicitacao);
        key.setCodCanal(codCanal);
        key.setNumSolicitacaoItem(1L);

        CreditRequest solicitacao = new CreditRequest();
        solicitacao.setNumSolicitacao(numSolicitacao);
        solicitacao.setCodCanal(codCanal);

        CreditRequestItems item = new CreditRequestItems();
        item.setId(key);
        item.setCodCanal(codCanal);
        item.setVlItem(new BigDecimal("10.00"));
        item.setFlgEvento("0");
        item.setNumLogicoCartao("123456");
        item.setCodAssinaturaHsm("HSM");
        item.setDtPagtoEconomica(LocalDateTime.now());
        item.setSeqRecarga(1);
        item.setSolicitacao(solicitacao);
        return item;
    }

    private CreditRequestRD buildRD(Long numSolicitacao, String codCanal, String codCanalDistribuicao) {
        CreditRequestRD rd = new CreditRequestRD();
        rd.setNumSolicitacao(numSolicitacao);
        rd.setCodCanal(codCanal);
        rd.setCodCanalDistribuicao(codCanalDistribuicao);
        return rd;
    }

    // ── Testes: resolverCanaisDistribuicao via SOL_RD_DISTRIBUICOES ───────────

    @Test
    @DisplayName("✓ Registra cada terminal em TB_REDE quando SOL_RD_DISTRIBUICOES retorna múltiplos canais")
    void processarLote_registraMultiplosTerminaisNoHm() {
        CreditRequestItems item = buildItem(100L, "CANAL_A");

        List<CreditRequestRD> distribuicoes = List.of(
                buildRD(100L, "CANAL_A", "TERMINAL_01"),
                buildRD(100L, "CANAL_A", "TERMINAL_02"),
                buildRD(100L, "CANAL_A", "TERMINAL_03"));

        when(creditRequestRDPort.findByNumSolicitacaoAndCodCanal(100L, "CANAL_A"))
                .thenReturn(distribuicoes);
        when(liminarGateway.verificarLiminarEmpresa(anyString())).thenReturn(0);

        processRechargeService.processarLoteRecarga(List.of(item));

        // Deve registrar cada terminal por item
        verify(hmPort, times(1)).registrarAutorizacaoRecarga(100L, "CANAL_A", "TERMINAL_01");
        verify(hmPort, times(1)).registrarAutorizacaoRecarga(100L, "CANAL_A", "TERMINAL_02");
        verify(hmPort, times(1)).registrarAutorizacaoRecarga(100L, "CANAL_A", "TERMINAL_03");
        verify(marketingDistribuitionChannelPersistencePort, never()).findByAssocied(anyString());
    }

    @Test
    @DisplayName("✓ Usa fallback para MarketingDistribuitionChannel quando SOL_RD_DISTRIBUICOES está vazio")
    void processarLote_usaFallbackQuandoSolRdDistribuicoesSemRegistros() {
        CreditRequestItems item = buildItem(200L, "CANAL_B");

        when(creditRequestRDPort.findByNumSolicitacaoAndCodCanal(200L, "CANAL_B"))
                .thenReturn(List.of());

        var mktKey = new MarketingDistribuitionChannelKey("CANAL_B", "TERMINAL_FB");
        var mktDistrib = new MarketingDistribuitionChannel(mktKey, null, null, null, null, null);
        when(marketingDistribuitionChannelPersistencePort.findByAssocied("CANAL_B"))
                .thenReturn(java.util.Optional.of(mktDistrib));
        when(liminarGateway.verificarLiminarEmpresa(anyString())).thenReturn(0);

        processRechargeService.processarLoteRecarga(List.of(item));

        verify(hmPort, times(1)).registrarAutorizacaoRecarga(200L, "CANAL_B", "TERMINAL_FB");
    }

    @Test
    @DisplayName("✓ Usa o próprio codCanal como fallback quando nem MarketingDistribuition nem SOL_RD existem")
    void processarLote_usaCodCanalQuandoFallbackTambemVazio() {
        CreditRequestItems item = buildItem(300L, "CANAL_C");

        when(creditRequestRDPort.findByNumSolicitacaoAndCodCanal(300L, "CANAL_C"))
                .thenReturn(List.of());
        when(marketingDistribuitionChannelPersistencePort.findByAssocied("CANAL_C"))
                .thenReturn(java.util.Optional.empty());
        when(liminarGateway.verificarLiminarEmpresa(anyString())).thenReturn(0);

        processRechargeService.processarLoteRecarga(List.of(item));

        verify(hmPort, times(1)).registrarAutorizacaoRecarga(300L, "CANAL_C", "CANAL_C");
    }

    @Test
    @DisplayName("✓ Lista vazia não processa nenhum item")
    void processarLote_listaVazia_naoProcessaNada() {
        processRechargeService.processarLoteRecarga(List.of());

        verify(creditRequestRDPort, never()).findByNumSolicitacaoAndCodCanal(anyLong(), anyString());
        verify(hmPort, never()).registrarAutorizacaoRecarga(anyLong(), anyString(), anyString());
    }
}

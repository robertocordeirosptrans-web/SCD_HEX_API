package br.sptrans.scd.creditrequest.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.creditrequest.application.port.in.HistCreditRequestManagementUseCase;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/hist/pedidos")
@Tag(name = "Histórico de Pedidos", description = "Historico de pedidos de crédito")
public class HistCreditRequestController {

    private final HistCreditRequestManagementUseCase histUseCase;
    private static final Logger log = LoggerFactory.getLogger(HistCreditRequestController.class);

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getCreditRequestHistory(
            @RequestParam("numSolicitacao") Long numSolicitacao,
            @RequestParam("numSolicitacaoItem") Long numSolicitacaoItem,
            @RequestParam(value = "codCanal", required = false, defaultValue = "152") String codCanal) {
        log.info("[HistCreditRequestController] Recebida requisição: numSolicitacao={}, numSolicitacaoItem={}, codCanal={}", numSolicitacao, numSolicitacaoItem, codCanal);
        var historico = histUseCase.findItemStatusHistory(numSolicitacao, numSolicitacaoItem, codCanal);
        return ResponseEntity.ok(historico);
    }
}

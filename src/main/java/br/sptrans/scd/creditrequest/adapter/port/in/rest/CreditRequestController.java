package br.sptrans.scd.creditrequest.adapter.port.in.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageResponse;
import br.sptrans.scd.creditrequest.application.service.CursorPaginationService;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/pedidos")
@Tag(name = "Pedidos", description = "API para gestão de pedidos de crédito")
public class CreditRequestController {

    private final CursorPaginationService cursorPaginationService;

    public CreditRequestController(CursorPaginationService cursorPaginationService) {
        this.cursorPaginationService = cursorPaginationService;
    }

    /**
     * Busca pedidos com paginação por cursor.
     * Classificação automática do modo de busca.
     */
    @PostMapping("/buscar")
    @ResponseBody
    @Operation(
        summary = "Busca pedidos com paginação por cursor",
        description = "Busca pedidos usando cursor-based pagination. O backend classifica automaticamente o modo de busca baseado nos filtros fornecidos."
    )
    public CursorPageResponse<CreditRequestDTO> buscarPedidos(@RequestBody CursorPageRequest request) {
        return cursorPaginationService.findWithCursor(request);
    }
}

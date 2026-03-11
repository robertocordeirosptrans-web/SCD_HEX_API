package br.sptrans.scd.creditrequest.adapter.port.in.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/pedidos")
@Tag(name = "Pedidos", description = "API para gestão de pedidos de crédito")
public class CreditRequestController {

    // private final CursorPaginationService cursorPaginationService;
}

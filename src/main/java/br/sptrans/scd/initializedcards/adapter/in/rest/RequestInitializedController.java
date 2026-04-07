
package br.sptrans.scd.initializedcards.adapter.in.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/initialized-cards")
@Tag(name = "Cartões", description = "API para gestão de solicitações de cartões inicializados")
public class RequestInitializedController {


}

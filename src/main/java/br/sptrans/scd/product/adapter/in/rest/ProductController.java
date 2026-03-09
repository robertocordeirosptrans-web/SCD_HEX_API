package br.sptrans.scd.product.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/product")
@Tag(name = "Produtos v1", description = "Endpoints para gerenciamento de produtos - Versão 1")
public class ProductController {
     private static final Logger log = LoggerFactory.getLogger(ProductController.class);
}

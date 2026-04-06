package br.sptrans.scd.channel.adapter.port.in.rest;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.MarketingDistribuitionChannelResponseDTO;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.MarketingDistribuitionChannelMapper;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase.CreateMarketingDistribuitionChannelCommand;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase.UpdateMarketingDistribuitionChannelCommand;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/marketing-distribuition-channels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais Comercialização/Distribuição v1", description = "Endpoints para gerenciamento de canais de comercialização e distribuição")
public class MarketingDistribuitionChannelController {

    private final MarketingDistribuitionChannelUseCase marketingUseCase;
    private final UserResolverHelper userResolverHelper;
    private final MarketingDistribuitionChannelMapper marketingMapper;

    @PostMapping
    @Operation(summary = "Cadastra um novo canal de comercialização/distribuição")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal de comercialização/distribuição cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<MarketingDistribuitionChannelResponseDTO> createMarketingDistribuitionChannel(
            @RequestBody CreateMarketingDistribuitionChannelRequest request) {
        User usuario = userResolverHelper.getCurrentUser();
        MarketingDistribuitionChannel result = marketingUseCase.createMarketingDistribuitionChannel(
                new CreateMarketingDistribuitionChannelCommand(
                        request.codCanalComercializacao(),
                        request.codCanalDistribuicao(),
                        request.codStatus(),
                        usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(marketingMapper.toResponseDTO(result));
    }

    @PutMapping("/{codCanalComercializacao}/{codCanalDistribuicao}")
    @Operation(summary = "Atualiza um canal de comercialização/distribuição")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal de comercialização/distribuição atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<MarketingDistribuitionChannelResponseDTO> updateMarketingDistribuitionChannel(
            @PathVariable String codCanalComercializacao,
            @PathVariable String codCanalDistribuicao,
            @RequestBody UpdateMarketingDistribuitionChannelRequest request) {
        User usuario = userResolverHelper.getCurrentUser();
        MarketingDistribuitionChannel result = marketingUseCase.updateMarketingDistribuitionChannel(
                codCanalComercializacao, codCanalDistribuicao,
                new UpdateMarketingDistribuitionChannelCommand(
                        request.codStatus(),
                        usuario));
        return ResponseEntity.ok(marketingMapper.toResponseDTO(result));
    }

    @GetMapping("/{codCanalComercializacao}/{codCanalDistribuicao}")
    @Operation(summary = "Busca canal de comercialização/distribuição por chave composta")
    public ResponseEntity<MarketingDistribuitionChannelResponseDTO> findMarketingDistribuitionChannel(
            @PathVariable String codCanalComercializacao,
            @PathVariable String codCanalDistribuicao) {
        return ResponseEntity.ok(
                marketingMapper.toResponseDTO(marketingUseCase.findMarketingDistribuitionChannel(codCanalComercializacao, codCanalDistribuicao)));
    }

    @GetMapping
    @Operation(summary = "Lista canais de comercialização/distribuição com filtro opcional")
    public ResponseEntity<PageResponse<MarketingDistribuitionChannelResponseDTO>> findMarketingDistribuitionChannels(
            @RequestParam(required = false) String codCanalComercializacao,
            @RequestParam(required = false) String codCanalDistribuicao,
            Pageable pageable) {
        Page<MarketingDistribuitionChannel> page = marketingUseCase.findAllMarketingDistribuitionChannels(pageable);
        return ResponseEntity.ok(PageResponse.fromPage(page.map(marketingMapper::toResponseDTO)));

    }

    @DeleteMapping("/{codCanalComercializacao}/{codCanalDistribuicao}")
    @Operation(summary = "Remove um canal de comercialização/distribuição")
    public ResponseEntity<Void> deleteMarketingDistribuitionChannel(
            @PathVariable String codCanalComercializacao,
            @PathVariable String codCanalDistribuicao) {
        marketingUseCase.deleteMarketingDistribuitionChannel(codCanalComercializacao, codCanalDistribuicao);
        return ResponseEntity.noContent().build();
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateMarketingDistribuitionChannelRequest(
            String codCanalComercializacao,
            String codCanalDistribuicao,
            String codStatus) {

    }

    public record UpdateMarketingDistribuitionChannelRequest(
            String codStatus) {

    }
}

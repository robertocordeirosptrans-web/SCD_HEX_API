package br.sptrans.scd.channel.adapter.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase.CreateMarketingDistribuitionChannelCommand;
import br.sptrans.scd.channel.application.port.in.MarketingDistribuitionChannelUseCase.UpdateMarketingDistribuitionChannelCommand;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/marketing-distribuition-channels")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais Comercialização/Distribuição v1", description = "Endpoints para gerenciamento de canais de comercialização e distribuição")
public class MarketingDistribuitionChannelController {

    private final MarketingDistribuitionChannelUseCase marketingUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo canal de comercialização/distribuição")
    public ResponseEntity<MarketingDistribuitionChannel> createMarketingDistribuitionChannel(
            @RequestBody CreateMarketingDistribuitionChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        MarketingDistribuitionChannel result = marketingUseCase.createMarketingDistribuitionChannel(
                new CreateMarketingDistribuitionChannelCommand(
                        request.codCanalComercializacao(),
                        request.codCanalDistribuicao(),
                        request.codStatus(),
                        idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codCanalComercializacao}/{codCanalDistribuicao}")
    @Operation(summary = "Atualiza um canal de comercialização/distribuição")
    public ResponseEntity<MarketingDistribuitionChannel> updateMarketingDistribuitionChannel(
            @PathVariable String codCanalComercializacao,
            @PathVariable String codCanalDistribuicao,
            @RequestBody UpdateMarketingDistribuitionChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        MarketingDistribuitionChannel result = marketingUseCase.updateMarketingDistribuitionChannel(
                codCanalComercializacao, codCanalDistribuicao,
                new UpdateMarketingDistribuitionChannelCommand(
                        request.codStatus(),
                        idUsuario));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codCanalComercializacao}/{codCanalDistribuicao}")
    @Operation(summary = "Busca canal de comercialização/distribuição por chave composta")
    public ResponseEntity<MarketingDistribuitionChannel> findMarketingDistribuitionChannel(
            @PathVariable String codCanalComercializacao,
            @PathVariable String codCanalDistribuicao) {
        return ResponseEntity.ok(
                marketingUseCase.findMarketingDistribuitionChannel(codCanalComercializacao, codCanalDistribuicao));
    }

    @GetMapping
    @Operation(summary = "Lista canais de comercialização/distribuição com filtro opcional")
    public ResponseEntity<List<MarketingDistribuitionChannel>> findMarketingDistribuitionChannels(
            @RequestParam(required = false) String codCanalComercializacao,
            @RequestParam(required = false) String codCanalDistribuicao) {
        if (codCanalComercializacao != null) {
            return ResponseEntity.ok(marketingUseCase.findByCodCanalComercializacao(codCanalComercializacao));
        }
        if (codCanalDistribuicao != null) {
            return ResponseEntity.ok(marketingUseCase.findByCodCanalDistribuicao(codCanalDistribuicao));
        }
        return ResponseEntity.ok(marketingUseCase.findAllMarketingDistribuitionChannels());
    }

    @DeleteMapping("/{codCanalComercializacao}/{codCanalDistribuicao}")
    @Operation(summary = "Remove um canal de comercialização/distribuição")
    public ResponseEntity<Void> deleteMarketingDistribuitionChannel(
            @PathVariable String codCanalComercializacao,
            @PathVariable String codCanalDistribuicao) {
        marketingUseCase.deleteMarketingDistribuitionChannel(codCanalComercializacao, codCanalDistribuicao);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────

    public record CreateMarketingDistribuitionChannelRequest(
            String codCanalComercializacao,
            String codCanalDistribuicao,
            String codStatus) {}

    public record UpdateMarketingDistribuitionChannelRequest(
            String codStatus) {}
}

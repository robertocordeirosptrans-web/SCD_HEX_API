package br.sptrans.scd.channel.adapter.in.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase.CreateSalesChannelCommand;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase.UpdateSalesChannelCommand;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/sales-channels")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais de Venda v1", description = "Endpoints para gerenciamento de canais de venda")
public class SalesChannelController {

    private final SalesChannelUseCase salesChannelUseCase;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Cadastra um novo canal de venda")
    public ResponseEntity<SalesChannel> createSalesChannel(
            @RequestBody CreateSalesChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        SalesChannel result = salesChannelUseCase.createSalesChannel(new CreateSalesChannelCommand(
                request.codCanal(),
                request.codDocumento(),
                request.codCanalSuperior(),
                request.desCanal(),
                request.codTipoDocumento(),
                request.desRazaoSocial(),
                request.desNomeFantasia(),
                request.vlCaucao(),
                request.dtInicioCaucao(),
                request.dtFimCaucao(),
                request.seqNivel(),
                request.flgCriticaNumlote(),
                request.flgLimiteDias(),
                request.flgProcessamentoAutomatico(),
                request.flgProcessamentoParcial(),
                request.flgSaldoDevedor(),
                request.numMinutoIniLibRecarga(),
                request.numMinutoFimLibRecarga(),
                request.flgEmiteReciboPedido(),
                request.flgSupercanal(),
                request.flgPagtoFuturo(),
                request.codClassificacaoPessoa(),
                request.codAtividade(),
                idUsuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{codCanal}")
    @Operation(summary = "Atualiza dados de um canal de venda")
    public ResponseEntity<SalesChannel> updateSalesChannel(
            @PathVariable String codCanal,
            @RequestBody UpdateSalesChannelRequest request,
            Authentication authentication) {
        Long idUsuario = resolveUserId(authentication);
        SalesChannel result = salesChannelUseCase.updateSalesChannel(codCanal, new UpdateSalesChannelCommand(
                request.codCanalSuperior(),
                request.desCanal(),
                request.desRazaoSocial(),
                request.desNomeFantasia(),
                request.vlCaucao(),
                request.dtInicioCaucao(),
                request.dtFimCaucao(),
                request.seqNivel(),
                request.flgCriticaNumlote(),
                request.flgLimiteDias(),
                request.flgProcessamentoAutomatico(),
                request.flgProcessamentoParcial(),
                request.flgSaldoDevedor(),
                request.numMinutoIniLibRecarga(),
                request.numMinutoFimLibRecarga(),
                request.flgEmiteReciboPedido(),
                request.flgSupercanal(),
                request.flgPagtoFuturo(),
                request.codClassificacaoPessoa(),
                request.codAtividade(),
                idUsuario));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{codCanal}")
    @Operation(summary = "Busca canal de venda por código")
    public ResponseEntity<SalesChannel> findBySalesChannel(@PathVariable String codCanal) {
        return ResponseEntity.ok(salesChannelUseCase.findBySalesChannel(codCanal));
    }

    @GetMapping
    @Operation(summary = "Lista todos os canais de venda, com filtro opcional de status")
    public ResponseEntity<List<SalesChannel>> findAllSalesChannels(
            @RequestParam(required = false) String stCanais) {
        return ResponseEntity.ok(salesChannelUseCase.findAllSalesChannels(stCanais));
    }

    @PatchMapping("/{codCanal}/activate")
    @Operation(summary = "Ativa um canal de venda")
    public ResponseEntity<Void> activateSalesChannel(
            @PathVariable String codCanal,
            Authentication authentication) {
        salesChannelUseCase.activateSalesChannel(codCanal, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codCanal}/inactivate")
    @Operation(summary = "Inativa um canal de venda")
    public ResponseEntity<Void> inactivateSalesChannel(
            @PathVariable String codCanal,
            Authentication authentication) {
        salesChannelUseCase.inactivateSalesChannel(codCanal, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codCanal}")
    @Operation(summary = "Remove um canal de venda")
    public ResponseEntity<Void> deleteSalesChannel(@PathVariable String codCanal) {
        salesChannelUseCase.deleteSalesChannel(codCanal);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        return userRepository.findByCodLogin(authentication.getName())
                .map(u -> u.getIdUsuario())
                .orElse(null);
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────
    public record CreateSalesChannelRequest(
            String codCanal,
            String codDocumento,
            String codCanalSuperior,
            String desCanal,
            String codTipoDocumento,
            String desRazaoSocial,
            String desNomeFantasia,
            BigDecimal vlCaucao,
            LocalDate dtInicioCaucao,
            LocalDate dtFimCaucao,
            Integer seqNivel,
            String flgCriticaNumlote,
            Integer flgLimiteDias,
            String flgProcessamentoAutomatico,
            String flgProcessamentoParcial,
            String flgSaldoDevedor,
            Integer numMinutoIniLibRecarga,
            Integer numMinutoFimLibRecarga,
            String flgEmiteReciboPedido,
            String flgSupercanal,
            String flgPagtoFuturo,
            String codClassificacaoPessoa,
            String codAtividade) {}

    public record UpdateSalesChannelRequest(
            String codCanalSuperior,
            String desCanal,
            String desRazaoSocial,
            String desNomeFantasia,
            BigDecimal vlCaucao,
            LocalDate dtInicioCaucao,
            LocalDate dtFimCaucao,
            Integer seqNivel,
            String flgCriticaNumlote,
            Integer flgLimiteDias,
            String flgProcessamentoAutomatico,
            String flgProcessamentoParcial,
            String flgSaldoDevedor,
            Integer numMinutoIniLibRecarga,
            Integer numMinutoFimLibRecarga,
            String flgEmiteReciboPedido,
            String flgSupercanal,
            String flgPagtoFuturo,
            String codClassificacaoPessoa,
            String codAtividade) {}
}

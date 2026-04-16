
package br.sptrans.scd.channel.adapter.in.rest;

// ...
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.sptrans.scd.channel.adapter.in.rest.dto.CreateSalesChannelRequest;
import br.sptrans.scd.channel.adapter.in.rest.dto.SalesChannelResponseDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.UpdateSalesChannelRequest;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase.CreateSalesChannelCommand;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase.UpdateSalesChannelCommand;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.security.CadPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/sales-channels")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais de Venda v1", description = "Endpoints para gerenciamento de canais de venda")

public class SalesChannelController {

    private static final Logger log = LoggerFactory.getLogger(SalesChannelController.class);

    private final SalesChannelUseCase salesChannelUseCase;
    private final UserResolverHelper userResolverHelper;
    private final SalesChannelMapper salesChannelMapper;

    @PostMapping
    @Operation(summary = "Cadastra um novo canal de venda")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal de venda cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_CADCANDEVEN + "')")
    public ResponseEntity<SalesChannelResponseDTO> createSalesChannel(
            @Valid @RequestBody CreateSalesChannelRequest request) {
        log.info("REST POST /sales-channels — Criando canal: {}", request.codCanal());
        var usuario = userResolverHelper.getCurrentUser();
        SalesChannel result = salesChannelUseCase.createSalesChannel(new CreateSalesChannelCommand(
                request.codCanal(),
                request.codDocumento(),
                request.codCanalSuperior(),
                request.desCanal(),
                request.codTipoDocumento(),
                request.desRazaoSocial(),
                request.desNomeFantasia(),
                request.vlCaucao(),
                request.dtInicioCaucao() != null ? request.dtInicioCaucao().toLocalDate() : null,
                request.dtFimCaucao() != null ? request.dtFimCaucao().toLocalDate() : null,
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
                usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(salesChannelMapper.toResponseDTO(result));
    }

    @PutMapping("/{codCanal}")
    @Operation(summary = "Atualiza dados de um canal de venda")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_ATUCANDEVEN + "')")
    public ResponseEntity<SalesChannelResponseDTO> updateSalesChannel(
            @PathVariable String codCanal,
            @Valid @RequestBody UpdateSalesChannelRequest request) {
        log.info("REST PUT /sales-channels/{} — Atualizando canal", codCanal);
        var usuario = userResolverHelper.getCurrentUser();
        SalesChannel result = salesChannelUseCase.updateSalesChannel(codCanal, new UpdateSalesChannelCommand(
                request.codCanalSuperior(),
                request.desCanal(),
                request.desRazaoSocial(),
                request.desNomeFantasia(),
                request.vlCaucao(),
                request.dtInicioCaucao() != null ? request.dtInicioCaucao().toLocalDate() : null,
                request.dtFimCaucao() != null ? request.dtFimCaucao().toLocalDate() : null,
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
                usuario));
        return ResponseEntity.ok(salesChannelMapper.toResponseDTO(result));
    }

    @GetMapping("/{codCanal}")
    @Operation(summary = "Busca canal de venda por código")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_BUSCANDEVENPORCOD + "')")
    public ResponseEntity<SalesChannelResponseDTO> findBySalesChannel(@PathVariable String codCanal) {
        SalesChannel channel = salesChannelUseCase.findBySalesChannel(codCanal);
        return ResponseEntity.ok(salesChannelMapper.toResponseDTO(channel));
    }

    @GetMapping("/subcanal/{codCanalSuperior}")
    @Operation(summary = "Lista subcanais de um canal superior, paginado, com projeção customizada")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_LISCANDEVEN + "')")
    public ResponseEntity<PageResponse<br.sptrans.scd.channel.adapter.in.rest.dto.SubSalesChannelProjection>> findSubChannelsByCodCanalSuperior(
            @PathVariable String codCanalSuperior,
            Pageable pageable) {
        var page = salesChannelUseCase.findSubChannelsByCodCanalSuperior(codCanalSuperior, pageable);
        if (page.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(PageResponse.fromPage(page));
    }

    @GetMapping
    @Operation(summary = "Lista todos os canais de venda, com filtro opcional de status")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_LISCANDEVEN + "')")
    public ResponseEntity<PageResponse<SalesChannelResponseDTO>> findAllSalesChannels(
            @RequestParam(required = false) String stCanais,
            Pageable pageable) {
        ChannelDomainStatus statusEnum = null;
        if (stCanais != null) {
            try {
                statusEnum = ChannelDomainStatus.fromCode(stCanais);
            } catch (Exception e) {
                return ResponseEntity.ok(PageResponse.fromPage(Page.empty(pageable)));
            }
        }
        Page<SalesChannel> page = salesChannelUseCase.findAllSalesChannels(statusEnum, pageable);
        Page<SalesChannelResponseDTO> dtoPage = page.map(salesChannelMapper::toResponseDTO);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @PatchMapping("/{codCanal}/activate")
    @Operation(summary = "Ativa um canal de venda")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_ATICANDEVEN + "')")
    public ResponseEntity<Void> activateSalesChannel(
            @PathVariable String codCanal) {
        log.info("REST PATCH /sales-channels/{}/activate", codCanal);
        salesChannelUseCase.activateSalesChannel(codCanal, userResolverHelper.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codCanal}/inactivate")
    @Operation(summary = "Inativa um canal de venda")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_INACANDEVEN + "')")
    public ResponseEntity<Void> inactivateSalesChannel(
            @PathVariable String codCanal) {
        log.info("REST PATCH /sales-channels/{}/inactivate", codCanal);
        salesChannelUseCase.inactivateSalesChannel(codCanal, userResolverHelper.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codCanal}")
    @Operation(summary = "Remove um canal de venda")
    @PreAuthorize("hasAuthority('" + CadPermissions.SAL_REMCANDEVEN + "')")
    public ResponseEntity<Void> deleteSalesChannel(@PathVariable String codCanal) {
        log.info("REST DELETE /sales-channels/{}", codCanal);
        salesChannelUseCase.deleteSalesChannel(codCanal);
        return ResponseEntity.noContent().build();
    }

}

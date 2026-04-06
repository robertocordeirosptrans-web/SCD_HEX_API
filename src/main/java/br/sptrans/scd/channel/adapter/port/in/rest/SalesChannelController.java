package br.sptrans.scd.channel.adapter.port.in.rest;

// ...
import java.util.List;

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

import br.sptrans.scd.channel.adapter.port.in.rest.dto.CanalResponseDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.CreateSalesChannelRequest;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UpdateSalesChannelRequest;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase.CreateSalesChannelCommand;
import br.sptrans.scd.channel.application.port.in.SalesChannelUseCase.UpdateSalesChannelCommand;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
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
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/sales-channels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Canais de Venda v1", description = "Endpoints para gerenciamento de canais de venda")

public class SalesChannelController {

    private final SalesChannelUseCase salesChannelUseCase;
    private final UserResolverHelper userResolverHelper;
    private final SalesChannelMapper salesChannelMapper;

    @PostMapping
        @Operation(summary = "Cadastra um novo canal de venda")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canal de venda cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        })
    public ResponseEntity<CanalResponseDTO> createSalesChannel(
            @RequestBody CreateSalesChannelRequest request) {
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
    public ResponseEntity<CanalResponseDTO> updateSalesChannel(
            @PathVariable String codCanal,
            @RequestBody UpdateSalesChannelRequest request) {
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
    public ResponseEntity<CanalResponseDTO> findBySalesChannel(@PathVariable String codCanal) {
        SalesChannel channel = salesChannelUseCase.findBySalesChannel(codCanal);
        return ResponseEntity.ok(salesChannelMapper.toResponseDTO(channel));
    }

    @GetMapping
    @Operation(summary = "Lista todos os canais de venda, com filtro opcional de status")
    public ResponseEntity<PageResponse<CanalResponseDTO>> findAllSalesChannels(
            @RequestParam(required = false) String stCanais,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ChannelDomainStatus statusEnum = null;
        if (stCanais != null) {
            try {
                statusEnum = ChannelDomainStatus.fromCode(stCanais);
            } catch (Exception e) {
                // Se valor inválido, retorna lista vazia ou erro, conforme política do sistema
                return ResponseEntity.ok(PageResponse.fromList(List.of(), page, size));
            }
        }
        List<SalesChannel> all = salesChannelUseCase.findAllSalesChannels(statusEnum);
        List<CanalResponseDTO> dtos = all.stream().map(salesChannelMapper::toResponseDTO).toList();
        return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
    }


    @PatchMapping("/{codCanal}/activate")
    @Operation(summary = "Ativa um canal de venda")
    public ResponseEntity<Void> activateSalesChannel(
            @PathVariable String codCanal) {
        salesChannelUseCase.activateSalesChannel(codCanal, userResolverHelper.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codCanal}/inactivate")
    @Operation(summary = "Inativa um canal de venda")
    public ResponseEntity<Void> inactivateSalesChannel(
            @PathVariable String codCanal) {
        salesChannelUseCase.inactivateSalesChannel(codCanal, userResolverHelper.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codCanal}")
    @Operation(summary = "Remove um canal de venda")
    public ResponseEntity<Void> deleteSalesChannel(@PathVariable String codCanal) {
        salesChannelUseCase.deleteSalesChannel(codCanal);
        return ResponseEntity.noContent().build();
    }


 
}

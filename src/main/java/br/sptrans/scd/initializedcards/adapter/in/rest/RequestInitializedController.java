package br.sptrans.scd.initializedcards.adapter.in.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.initializedcards.adapter.in.rest.request.AssociarLotesRequest;
import br.sptrans.scd.initializedcards.adapter.in.rest.request.CriarSolicitacaoRequest;
import br.sptrans.scd.initializedcards.adapter.in.rest.response.HistoricoResponse;
import br.sptrans.scd.initializedcards.adapter.in.rest.response.LoteDisponivelResponse;
import br.sptrans.scd.initializedcards.adapter.in.rest.response.SolicitacaoDetalheResponse;
import br.sptrans.scd.initializedcards.adapter.in.rest.response.SolicitacaoResponse;
import br.sptrans.scd.initializedcards.application.port.in.HistRequestInitializedUseCase;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase.AssociarLotesCommand;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase.CancelarCommand;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase.CriarSolicitacaoCommand;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase.DesassociarLotesCommand;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase.SolicitacaoDetalhe;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/initialized-cards")
@Tag(name = "Cartões Inicializados", description = "API para gestão de solicitações de cartões inicializados")
@RequiredArgsConstructor
public class RequestInitializedController {

    private final RequestInitializedUseCase useCase;
    private final HistRequestInitializedUseCase histUseCase;
    private final UserResolverHelper userResolverHelper;

    // ── Criar ──────────────────────────────────────────────────────────────────

    @Operation(summary = "Criar solicitação de cartões inicializados")
    @PostMapping
    public ResponseEntity<SolicitacaoResponse> criarSolicitacao(
            @Valid @RequestBody CriarSolicitacaoRequest request) {

        Long idUsuario = userResolverHelper.getCurrentUserId();

        CriarSolicitacaoCommand command = new CriarSolicitacaoCommand(
                request.getCodTipoCanal(),
                request.getCodCanal(),
                request.getCodAdquirente(),
                request.getCodProduto(),
                request.getQtdSolicitada(),
                request.getFlgTipoSaida(),
                request.getFlgTipoVolume(),
                request.getFlgAssociacaoUsuario(),
                request.getFlgGeraArquivo(),
                request.getFlgRespEntregaRetirada(),
                request.getDesNomeRespEntrega(),
                request.getCodTipoDoctoRespEntrega(),
                request.getCodDoctoRespEntrega(),
                request.getCodEnderecoEntrega(),
                request.getDtPrevistaEntrega(),
                idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SolicitacaoResponse.fromDomain(useCase.criarSolicitacao(command)));
    }

    // ── Listar ─────────────────────────────────────────────────────────────────

    @Operation(summary = "Listar solicitações de cartões inicializados")
    @GetMapping
    public ResponseEntity<PageResponse<SolicitacaoResponse>> listarSolicitacoes(
            @RequestParam(required = false) String codCanal,
            @RequestParam(required = false) Long nrSolicitacao,
            @RequestParam(required = false) String codAdquirente,
            @RequestParam(required = false) String codProduto,
            @RequestParam(required = false) String flgFaseSolicitacao,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<SolicitacaoResponse> page = useCase
                .listarSolicitacoes(codCanal, nrSolicitacao, codAdquirente, codProduto, flgFaseSolicitacao, pageable)
                .map(SolicitacaoResponse::fromDomain);

        return ResponseEntity.ok(PageResponse.fromPage(page));
    }

    // ── Detalhar ───────────────────────────────────────────────────────────────

    @Operation(summary = "Consultar detalhes de uma solicitação")
    @GetMapping("/{codCanal}/{nrSolicitacao}")
    public ResponseEntity<SolicitacaoDetalheResponse> detalharSolicitacao(
            @PathVariable String codCanal,
            @PathVariable Long nrSolicitacao) {

        SolicitacaoDetalhe detalhe = useCase.detalharSolicitacao(codCanal, nrSolicitacao);
        return ResponseEntity.ok(SolicitacaoDetalheResponse.fromDomain(detalhe, detalhe.tbLotes()));
    }

    // ── Associar Lotes ─────────────────────────────────────────────────────────

    @Operation(summary = "Associar lotes a uma solicitação")
    @PostMapping("/{codCanal}/{nrSolicitacao}/lotes")
    public ResponseEntity<Void> associarLotes(
            @PathVariable String codCanal,
            @PathVariable Long nrSolicitacao,
            @Valid @RequestBody AssociarLotesRequest request) {

        Long idUsuario = userResolverHelper.getCurrentUserId();
        useCase.associarLotes(new AssociarLotesCommand(codCanal, nrSolicitacao,
                request.getIdsLotes(), idUsuario));
        return ResponseEntity.noContent().build();
    }

    // ── Desassociar Lotes ──────────────────────────────────────────────────────

    @Operation(summary = "Desassociar lotes de uma solicitação")
    @DeleteMapping("/{codCanal}/{nrSolicitacao}/lotes")
    public ResponseEntity<Void> desassociarLotes(
            @PathVariable String codCanal,
            @PathVariable Long nrSolicitacao) {

        Long idUsuario = userResolverHelper.getCurrentUserId();
        useCase.desassociarLotes(new DesassociarLotesCommand(codCanal, nrSolicitacao, idUsuario));
        return ResponseEntity.noContent().build();
    }

    // ── Cancelar ───────────────────────────────────────────────────────────────

    @Operation(summary = "Cancelar uma solicitação")
    @PatchMapping("/{codCanal}/{nrSolicitacao}/cancelar")
    public ResponseEntity<Void> cancelarSolicitacao(
            @PathVariable String codCanal,
            @PathVariable Long nrSolicitacao) {

        Long idUsuario = userResolverHelper.getCurrentUserId();
        useCase.cancelarSolicitacao(new CancelarCommand(codCanal, nrSolicitacao, idUsuario));
        return ResponseEntity.noContent().build();
    }

    // ── Histórico ──────────────────────────────────────────────────────────────

    @Operation(summary = "Listar histórico de uma solicitação")
    @GetMapping("/{codCanal}/{nrSolicitacao}/historico")
    public ResponseEntity<List<HistoricoResponse>> listarHistorico(
            @PathVariable String codCanal,
            @PathVariable Long nrSolicitacao) {

        List<HistoricoResponse> historico = histUseCase.listarHistorico(codCanal, nrSolicitacao)
                .stream()
                .map(HistoricoResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(historico);
    }

    // ── Lotes Disponíveis ──────────────────────────────────────────────────────

    @Operation(summary = "Buscar lotes disponíveis para associação")
    @GetMapping("/lotes/disponiveis")
    public ResponseEntity<List<LoteDisponivelResponse>> buscarLotesDisponiveis(
            @RequestParam(required = false, defaultValue = "idLote") String sortBy) {

        List<LoteDisponivelResponse> lotes = useCase.buscarLotesDisponiveis(sortBy)
                .stream()
                .map(LoteDisponivelResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lotes);
    }
}


package br.sptrans.scd.initializedcards.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.initializedcards.application.port.in.HistRequestInitializedUseCase;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase;
import br.sptrans.scd.initializedcards.application.port.out.HistRequestInitializedRepository;
import br.sptrans.scd.initializedcards.application.port.out.RequestInitializedRepository;
import br.sptrans.scd.initializedcards.application.port.out.RequestLotSCPRepository;
import br.sptrans.scd.initializedcards.application.port.out.TbLotRepository;
import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.RequestLotSCPKey;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import br.sptrans.scd.initializedcards.domain.enums.FaseSolicitacao;
import br.sptrans.scd.initializedcards.domain.enums.StatusSolicitacao;
import br.sptrans.scd.initializedcards.domain.enums.TipoSaida;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestInitializedService implements RequestInitializedUseCase, HistRequestInitializedUseCase {

    private final RequestInitializedRepository requestInitializedRepository;
    private final HistRequestInitializedRepository histRequestInitializedRepository;
    private final RequestLotSCPRepository requestLotSCPRepository;
    private final TbLotRepository tbLotRepository;

    // ── Criar ──────────────────────────────────────────────────────────────────

    @Override
    public RequestInitializedCards criarSolicitacao(CriarSolicitacaoCommand command) {
        // Regra 2: data prevista deve ser futura
        if (!command.dtPrevistaEntrega().isAfter(LocalDateTime.now())) {
            throw new ValidationException("A data prevista de entrega deve ser uma data futura.");
        }

        // Regra 3: tipo de saída ENTREGA exige endereço do canal
        if (TipoSaida.ENTREGA.getCode().equals(command.flgTipoSaida())) {
            if (command.codEnderecoEntrega() == null || command.codEnderecoEntrega().isBlank()) {
                throw new ValidationException(
                        "Para tipo de saída ENTREGA, o código de endereço de entrega é obrigatório.");
            }
        }

        Long nrSolicitacao = requestInitializedRepository.nextNrSolicitacao(
                command.codTipoCanal(), command.codCanal());

        RequestInitializedCards solicitacao = new RequestInitializedCards();
        solicitacao.setCodTipoCanal(command.codTipoCanal());
        SalesChannel sc = new SalesChannel();
        sc.setCodCanal(command.codCanal());
        solicitacao.setCodCanal(sc);
        solicitacao.setNrSolicitacao(nrSolicitacao);
        solicitacao.setCodAdquirente(command.codAdquirente());
        solicitacao.setQtdSolicitada(command.qtdSolicitada());
        solicitacao.setFlgTipoSaida(command.flgTipoSaida());
        solicitacao.setFlgTipoVolume(command.flgTipoVolume());
        solicitacao.setFlgAssociaUsuario(command.flgAssociacaoUsuario());
        solicitacao.setFlgGerarArquivo(command.flgGeraArquivo());
        solicitacao.setFlgRespEntregaRetirada(command.flgRespEntregaRetirada());
        solicitacao.setDesRespEntrega(command.desNomeRespEntrega());
        solicitacao.setCodTipoRespEntrega(command.codTipoDoctoRespEntrega());
        solicitacao.setCodDoctoRespEntrega(command.codDoctoRespEntrega());
        solicitacao.setCodEnderecoEntrega(command.codEnderecoEntrega());
        solicitacao.setDtPrevistaEntrega(command.dtPrevistaEntrega());
        solicitacao.setDtSolicitacao(LocalDateTime.now());
        solicitacao.setDtCadastro(LocalDateTime.now());
        solicitacao.setStSolicitaCartaoInicializad(StatusSolicitacao.ATIVA.getCode());
        solicitacao.setFlgFaseSolicitacao(FaseSolicitacao.CADASTRADA.getCode());

        if (command.idUsuarioCadastro() != null) {
            br.sptrans.scd.auth.domain.User usuario = new br.sptrans.scd.auth.domain.User();
            usuario.setIdUsuario(command.idUsuarioCadastro());
            solicitacao.setIdUsuarioCadastro(usuario);
        }

        RequestInitializedCards saved = requestInitializedRepository.save(solicitacao);
        salvarHistorico(saved);
        return saved;
    }

    // ── Listar ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<RequestInitializedCards> listarSolicitacoes(
            String codCanal, String codAdquirente, Pageable pageable) {
        return requestInitializedRepository.findAll(codCanal, codAdquirente, pageable);
    }

    // ── Buscar por ID ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public RequestInitializedCards buscarPorId(String codCanal, Long nrSolicitacao) {
        RequestInitializedCards result = requestInitializedRepository.findById(codCanal, nrSolicitacao);
        if (result == null) {
            throw new ResourceNotFoundException("Solicitação", "codCanal/nrSolicitacao",
                    codCanal + "/" + nrSolicitacao);
        }
        return result;
    }

    // ── Detalhar ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public SolicitacaoDetalhe detalharSolicitacao(String codCanal, Long nrSolicitacao) {
        RequestInitializedCards solicitacao = buscarPorId(codCanal, nrSolicitacao);
        List<RequestLotSCP> lotes = requestLotSCPRepository.findAllBySolicitacao(codCanal, nrSolicitacao);
        List<HistRequestInitializedCards> historico =
                histRequestInitializedRepository.findAllHist(codCanal, nrSolicitacao);
        List<Long> idsLotes = lotes.stream()
                .filter(l -> l.getId() != null)
                .map(l -> l.getId().getIdLote())
                .collect(Collectors.toList());
        List<TbLotSCD> tbLotes = idsLotes.isEmpty()
                ? List.of()
                : tbLotRepository.findAllByIds(idsLotes);
        return new SolicitacaoDetalhe(solicitacao, lotes, historico, tbLotes);
    }

    // ── Associar Lotes ─────────────────────────────────────────────────────────

    @Override
    public void associarLotes(AssociarLotesCommand command) {
        RequestInitializedCards solicitacao = buscarPorId(command.codCanal(), command.nrSolicitacao());

        // Validar status ATIVA
        validarStatusAtiva(solicitacao);

        // Regra 1: soma dos cartões dos lotes == qtdSolicitada
        List<TbLotSCD> lotes = tbLotRepository.findAllByIds(command.idsLotes());
        if (lotes.size() != command.idsLotes().size()) {
            throw new ResourceNotFoundException("Um ou mais lotes informados não foram encontrados.");
        }

        long somaCartoes = lotes.stream().mapToLong(TbLotSCD::getQtdCartoesLote).sum();
        if (!solicitacao.getQtdSolicitada().equals(somaCartoes)) {
            throw new ValidationException(
                    String.format(
                            "A soma dos cartões dos lotes (%d) deve ser igual à quantidade solicitada (%d).",
                            somaCartoes, solicitacao.getQtdSolicitada()));
        }

        String codTipoCanal = solicitacao.getCodTipoCanal();
        String codCanal = solicitacao.getCodCanal().getCodCanal();
        LocalDateTime agora = LocalDateTime.now();

        // Criar uma entrada de lote por cada lote informado
        for (TbLotSCD lote : lotes) {
            RequestLotSCPKey key = new RequestLotSCPKey(
                    codTipoCanal,
                    codCanal,
                    solicitacao.getNrSolicitacao(),
                    lote.getIdLote(),
                    solicitacao.getFlgFaseSolicitacao());

            RequestLotSCP lotSCP = new RequestLotSCP();
            lotSCP.setId(key);
            lotSCP.setQtdProduto(lote.getQtdCartoesLote());
            lotSCP.setStSolicitacaoLoteSCP(StatusSolicitacao.ATIVA.getCode());
            lotSCP.setDtCadastro(agora);

            if (command.idUsuario() != null) {
                br.sptrans.scd.auth.domain.User usuario = new br.sptrans.scd.auth.domain.User();
                usuario.setIdUsuario(command.idUsuario());
                lotSCP.setIdUsuarioCadastro(usuario);
            }
            requestLotSCPRepository.save(lotSCP);
        }

        // Atualizar fase da solicitação
        solicitacao.setFlgFaseSolicitacao(FaseSolicitacao.ASSOCIADA.getCode());
        solicitacao.setDtAssociacaoLoteSCP(agora);
        solicitacao.setDtManutencao(agora);
        if (command.idUsuario() != null) {
            br.sptrans.scd.auth.domain.User usuario = new br.sptrans.scd.auth.domain.User();
            usuario.setIdUsuario(command.idUsuario());
            solicitacao.setIdUsuarioManutencao(usuario);
        }

        RequestInitializedCards saved = requestInitializedRepository.save(solicitacao);
        salvarHistorico(saved);
    }

    // ── Desassociar Lotes ──────────────────────────────────────────────────────

    @Override
    public void desassociarLotes(DesassociarLotesCommand command) {
        RequestInitializedCards solicitacao = buscarPorId(command.codCanal(), command.nrSolicitacao());
        validarStatusAtiva(solicitacao);

        requestLotSCPRepository.deleteAllBySolicitacao(command.codCanal(), command.nrSolicitacao());

        LocalDateTime agora = LocalDateTime.now();
        solicitacao.setFlgFaseSolicitacao(FaseSolicitacao.CADASTRADA.getCode());
        solicitacao.setDtAssociacaoLoteSCP(null);
        solicitacao.setDtManutencao(agora);
        if (command.idUsuario() != null) {
            br.sptrans.scd.auth.domain.User usuario = new br.sptrans.scd.auth.domain.User();
            usuario.setIdUsuario(command.idUsuario());
            solicitacao.setIdUsuarioManutencao(usuario);
        }

        RequestInitializedCards saved = requestInitializedRepository.save(solicitacao);
        salvarHistorico(saved);
    }

    // ── Cancelar ───────────────────────────────────────────────────────────────

    @Override
    public void cancelarSolicitacao(CancelarCommand command) {
        RequestInitializedCards solicitacao = buscarPorId(command.codCanal(), command.nrSolicitacao());
        validarStatusAtiva(solicitacao);

        // Regra 4: não pode cancelar se houver lotes associados
        List<RequestLotSCP> lotes =
                requestLotSCPRepository.findAllBySolicitacao(command.codCanal(), command.nrSolicitacao());
        if (!lotes.isEmpty()) {
            throw new ValidationException(
                    "Não é possível cancelar uma solicitação que possui lotes associados.");
        }

        LocalDateTime agora = LocalDateTime.now();
        solicitacao.setStSolicitaCartaoInicializad(StatusSolicitacao.CANCELADA.getCode());
        solicitacao.setDtCancelamento(agora);
        solicitacao.setDtManutencao(agora);
        if (command.idUsuario() != null) {
            br.sptrans.scd.auth.domain.User usuario = new br.sptrans.scd.auth.domain.User();
            usuario.setIdUsuario(command.idUsuario());
            solicitacao.setIdUsuarioManutencao(usuario);
        }

        RequestInitializedCards saved = requestInitializedRepository.save(solicitacao);
        salvarHistorico(saved);
    }

    // ── Lotes Disponíveis ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<TbLotSCD> buscarLotesDisponiveis(String sortBy) {
        return tbLotRepository.findDisponiveis(sortBy);
    }

    // ── Histórico (HistRequestInitializedUseCase) ─────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<HistRequestInitializedCards> listarHistorico(String codCanal, Long nrSolicitacao) {
        return histRequestInitializedRepository.findAllHist(codCanal, nrSolicitacao);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void validarStatusAtiva(RequestInitializedCards solicitacao) {
        if (!StatusSolicitacao.ATIVA.getCode().equals(solicitacao.getStSolicitaCartaoInicializad())) {
            throw new ValidationException(
                    "Operação não permitida: a solicitação não está com status ATIVA.");
        }
    }

    private void salvarHistorico(RequestInitializedCards solicitacao) {
        String codCanal = solicitacao.getCodCanal() != null
                ? solicitacao.getCodCanal().getCodCanal()
                : null;
        Long nrSolicitacao = solicitacao.getNrSolicitacao();
        Long nextSeq = histRequestInitializedRepository.nextSeqHist(codCanal, nrSolicitacao);

        HistRequestInitializedCards hist = new HistRequestInitializedCards();
        hist.setCodTipoCanal(solicitacao.getCodTipoCanal());
        hist.setCodCanal(solicitacao.getCodCanal());
        hist.setNrSolicitacao(nrSolicitacao);
        hist.setSeqHistSolicCartaoIni(nextSeq);
        hist.setCodAdquirente(solicitacao.getCodAdquirente());
        hist.setCodProduto(solicitacao.getCodProduto());
        hist.setQtdSolicitada(solicitacao.getQtdSolicitada());
        hist.setQtdAtendida(solicitacao.getQtdAtendida());
        hist.setQtdRecebida(solicitacao.getQtdRecebida());
        hist.setFlgTipoSaida(solicitacao.getFlgTipoSaida());
        hist.setFlgTipoVolume(solicitacao.getFlgTipoVolume());
        hist.setFlgAssociaUsuario(solicitacao.getFlgAssociaUsuario());
        hist.setFlgGerarArquivo(solicitacao.getFlgGerarArquivo());
        hist.setFlgRespEntregaRetirada(solicitacao.getFlgRespEntregaRetirada());
        hist.setDesRespEntrega(solicitacao.getDesRespEntrega());
        hist.setCodTipoRespEntrega(solicitacao.getCodTipoRespEntrega());
        hist.setCodDoctoRespEntrega(solicitacao.getCodDoctoRespEntrega());
        hist.setCodEnderecoEntrega(solicitacao.getCodEnderecoEntrega());
        hist.setFlgAprovado(solicitacao.getFlgAprovado());
        hist.setDtPrevistaEntrega(solicitacao.getDtPrevistaEntrega());
        hist.setDtSolicitacao(solicitacao.getDtSolicitacao());
        hist.setDtAprovacao(solicitacao.getDtAprovacao());
        hist.setDtAssociacaoLoteSCP(solicitacao.getDtAssociacaoLoteSCP());
        hist.setDtGeracaoArquivo(solicitacao.getDtGeracaoArquivo());
        hist.setFlgFaseSolicitacao(solicitacao.getFlgFaseSolicitacao());
        hist.setStSolicitaCartaoInicializad(solicitacao.getStSolicitaCartaoInicializad());
        hist.setDtCadastro(LocalDateTime.now());
        hist.setDtCancelamento(solicitacao.getDtCancelamento());
        hist.setIdUsuarioCadastro(solicitacao.getIdUsuarioCadastro());
        hist.setIdUsuarioManutencao(solicitacao.getIdUsuarioManutencao());

        histRequestInitializedRepository.save(hist);
    }
}


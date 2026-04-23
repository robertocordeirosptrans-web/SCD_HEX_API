package br.sptrans.scd.initializedcards.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface RequestInitializedUseCase {

    RequestInitializedCards criarSolicitacao(CriarSolicitacaoCommand command);

    Page<RequestInitializedCards> listarSolicitacoes(String codCanal, String codAdquirente, Pageable pageable);

    RequestInitializedCards buscarPorId(String codCanal, Long nrSolicitacao);

    SolicitacaoDetalhe detalharSolicitacao(String codCanal, Long nrSolicitacao);

    void associarLotes(AssociarLotesCommand command);

    void desassociarLotes(DesassociarLotesCommand command);

    void cancelarSolicitacao(CancelarCommand command);

    List<TbLotSCD> buscarLotesDisponiveis(String sortBy);

    // ── Comandos ──────────────────────────────────────────────────────────────

    record CriarSolicitacaoCommand(
            @NotBlank @Size(max = 20) String codTipoCanal,
            @NotBlank @Size(max = 20) String codCanal,
            String codAdquirente,
            String codProduto,
            @NotNull Long qtdSolicitada,
            @NotBlank @Size(max = 1) String flgTipoSaida,
            @Size(max = 1) String flgTipoVolume,
            @Size(max = 1) String flgAssociacaoUsuario,
            @Size(max = 1) String flgGeraArquivo,
            @Size(max = 1) String flgRespEntregaRetirada,
            @Size(max = 60) String desNomeRespEntrega,
            @Size(max = 3) String codTipoDoctoRespEntrega,
            @Size(max = 20) String codDoctoRespEntrega,
            @Size(max = 20) String codEnderecoEntrega,
            @NotNull LocalDateTime dtPrevistaEntrega,
            Long idUsuarioCadastro) {
    }

    record AssociarLotesCommand(
            @NotBlank String codCanal,
            @NotNull Long nrSolicitacao,
            @NotEmpty List<Long> idsLotes,
            Long idUsuario) {
    }

    record DesassociarLotesCommand(
            @NotBlank String codCanal,
            @NotNull Long nrSolicitacao,
            Long idUsuario) {
    }

    record CancelarCommand(
            @NotBlank String codCanal,
            @NotNull Long nrSolicitacao,
            Long idUsuario) {
    }

    // ── Agregado de detalhe ────────────────────────────────────────────────────

    record SolicitacaoDetalhe(
            RequestInitializedCards solicitacao,
            List<RequestLotSCP> lotes,
            List<HistRequestInitializedCards> historico,
            List<TbLotSCD> tbLotes) {
    }
}


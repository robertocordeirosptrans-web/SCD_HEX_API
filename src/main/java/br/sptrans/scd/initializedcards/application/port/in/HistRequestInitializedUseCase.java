package br.sptrans.scd.initializedcards.application.port.in;

import java.util.List;

import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;

public interface HistRequestInitializedUseCase {

    HistRequestInitializedCards createRequestInitialized(HistRequestInitializedCardCommand command);

    HistRequestInitializedCards findById(String codEndereco);

    List<HistRequestInitializedCards> findAllRequestInitialized(String codCanal);

    public record HistRequestInitializedCardCommand(
            String cod_tipo_canal,
            String cod_Canal,
            Long Num_Solicitacao,
            Long seqHistSolicCartaoIni,
            String Cod_Adquirente,
            String Cod_Produto,
            Long qtdsolicitada,
            String Tipo_Saida,
            String Tipo_Volume,
            String Associacao_Usuario,
            String Gerar_Arquivo,
            String Resp_Entrega,
            String Nome_Responsavel,
            String RG_Responsavel,
            String Endereco,
            String Data_Prevista,
            String data_solicitacao
            ) {

    }
}

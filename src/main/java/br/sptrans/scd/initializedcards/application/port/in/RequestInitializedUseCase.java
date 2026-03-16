package br.sptrans.scd.initializedcards.application.port.in;

import java.util.List;

import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;

public interface RequestInitializedUseCase {

    RequestInitializedCards createRequestInitialized(RequestInitializedCardCommand command);

    RequestInitializedCards findById(String codEndereco);

    List<RequestInitializedCards> findAllRequestInitialized(String codCanal);

    public record RequestInitializedCardCommand(
            String cod_tipo_canal,
            String cod_Canal,
            Long Num_Solicitacao,
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

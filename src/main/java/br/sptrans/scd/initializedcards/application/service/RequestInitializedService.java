
package br.sptrans.scd.initializedcards.application.service;


import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.initializedcards.application.port.in.HistRequestInitializedUseCase;
import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase;
import br.sptrans.scd.initializedcards.application.port.out.HistRequestInitializedRepository;
import br.sptrans.scd.initializedcards.application.port.out.RequestInitializedRepository;
import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class RequestInitializedService implements RequestInitializedUseCase, HistRequestInitializedUseCase {

    @Autowired
    private RequestInitializedRepository requestInitializedRepository;

    @Autowired
    private HistRequestInitializedRepository histRequestInitializedRepository;

    @Override

    public RequestInitializedCards createRequestInitialized(RequestInitializedCardCommand command) {
        RequestInitializedCards entity = new RequestInitializedCards();
        // Mapear os campos do command para entity
        entity.setCodTipoCanal(command.cod_tipo_canal());
        // ... mapear os outros campos conforme necessário ...
        return requestInitializedRepository.save(entity);
    }

    @Override

    public RequestInitializedCards findById(String codCanal, Long nrSolicitacao) {
        return requestInitializedRepository.findById(codCanal, nrSolicitacao);
    }

    @Override

    public List<RequestInitializedCards> findAllRequestInitialized(String codCanal, Long nrSolicitacao, String codAdquirente) {
        RequestInitializedCards result = requestInitializedRepository.findAll(codCanal, nrSolicitacao, codAdquirente);
        if (result != null) {
            return Collections.singletonList(result);
        }
        return Collections.emptyList();
    }

    @Override

    public HistRequestInitializedCards createRequestInitialized(HistRequestInitializedCardCommand command) {
        HistRequestInitializedCards entity = new HistRequestInitializedCards();
        // Mapear os campos do command para entity
        entity.setCodTipoCanal(command.cod_tipo_canal());
        // ... mapear os outros campos conforme necessário ...
        return histRequestInitializedRepository.save(entity);
    }

    @Override
    public HistRequestInitializedCards findById(String codEndereco) {
        // Implementação fictícia, pois não há método correspondente no repositório
        // Retorne null ou lance uma exceção se necessário
        return null;
    }

    @Override
    public List<HistRequestInitializedCards> findAllRequestInitialized(String codCanal) {
        // Implementação fictícia, pois não há método correspondente no repositório
        // Retorne lista vazia ou implemente conforme necessário
        return Collections.emptyList();
    }
}

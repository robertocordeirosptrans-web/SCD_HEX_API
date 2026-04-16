package br.sptrans.scd.auth.application.service;

import br.sptrans.scd.auth.adapter.in.rest.request.CreateClassificationPersonRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.UpdateClassificationPersonRequest;
import br.sptrans.scd.auth.application.port.in.ClassificationPersonCase;
import br.sptrans.scd.auth.application.port.out.ClassificationPersonPort;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassificationPersonService implements ClassificationPersonCase {
    private final ClassificationPersonPort port;

    @Override
    public ClassificationPerson create(CreateClassificationPersonRequest request) {
        ClassificationPerson person = new ClassificationPerson();
        person.setCodClassificacaoPessoa(request.codClassificacaoPessoa());
        person.setDesClassificacaoPessoa(request.desClassificacaoPessoa());
        person.setFlgVenda(request.flgVenda());
        person.setDtCadastro(request.dtCadastro());
        person.setStClassificacoesPessoa(request.stClassificacoesPessoa());
        // idUsuarioCadastro: precisa buscar User pelo id se necessário
        return port.save(person);
    }

    @Override
    public ClassificationPerson update(UpdateClassificationPersonRequest request) {
        ClassificationPerson person = port.findById(request.codClassificacaoPessoa())
                .orElseThrow(() -> new RuntimeException("ClassificationPerson not found"));
        person.setDesClassificacaoPessoa(request.desClassificacaoPessoa());
        person.setFlgVenda(request.flgVenda());
        person.setDtManutencao(request.dtManutencao());
        person.setStClassificacoesPessoa(request.stClassificacoesPessoa());
        // idUsuarioManutencao: precisa buscar User pelo id se necessário
        return port.update(person);
    }

    @Override
    public void delete(String codClassificacaoPessoa) {
        port.delete(codClassificacaoPessoa);
    }

    @Override
    public Optional<ClassificationPerson> findById(String codClassificacaoPessoa) {
        return port.findById(codClassificacaoPessoa);
    }

    @Override
    public Page<ClassificationPerson> list(Pageable pageable) {
        return port.findAll(pageable);
    }
}

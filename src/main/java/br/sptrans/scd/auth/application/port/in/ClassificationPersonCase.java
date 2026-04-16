package br.sptrans.scd.auth.application.port.in;

import br.sptrans.scd.auth.adapter.in.rest.request.CreateClassificationPersonRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.UpdateClassificationPersonRequest;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClassificationPersonCase {
    ClassificationPerson create(CreateClassificationPersonRequest request);
    ClassificationPerson update(UpdateClassificationPersonRequest request);
    void delete(String codClassificacaoPessoa);
    Optional<ClassificationPerson> findById(String codClassificacaoPessoa);
    Page<ClassificationPerson> list(Pageable pageable);
}

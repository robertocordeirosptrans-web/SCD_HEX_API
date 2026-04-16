package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClassificationPersonPort {
    ClassificationPerson save(ClassificationPerson person);
    ClassificationPerson update(ClassificationPerson person);
    void delete(String codClassificacaoPessoa);
    Optional<ClassificationPerson> findById(String codClassificacaoPessoa);
    Page<ClassificationPerson> findAll(Pageable pageable);
}

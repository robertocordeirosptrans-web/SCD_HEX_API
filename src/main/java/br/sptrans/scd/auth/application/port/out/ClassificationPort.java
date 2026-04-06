package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import br.sptrans.scd.auth.domain.ClassificationPerson;

public interface ClassificationPort {

    Optional<ClassificationPerson> findById(String codClassificacaoPessoa);
}

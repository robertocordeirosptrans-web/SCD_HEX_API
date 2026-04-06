package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.ClassificationPersonMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.ClassificationPersonRepository;
import br.sptrans.scd.auth.application.port.out.ClassificationPort;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClassificationAdapterJpa implements ClassificationPort {

    private final ClassificationPersonRepository classificationPersonRepository;



    @Override
    public Optional<ClassificationPerson> findById(String codClassificacaoPessoa) {
        return classificationPersonRepository.findById(codClassificacaoPessoa)
                .map(ClassificationPersonMapper::toDomain);
    }
}

package br.sptrans.scd.auth.adapter.out.jpa;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.ClassificationPersonMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.ClassificationPersonRepository;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ClassificationPersonEntity;
import br.sptrans.scd.auth.application.port.out.ClassificationPersonPort;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClassificationPersonAdapter implements ClassificationPersonPort {
    private final ClassificationPersonRepository repository;
    private final ClassificationPersonMapper mapper;

    @Override
    public ClassificationPerson save(ClassificationPerson person) {
        ClassificationPersonEntity entity = mapper.toEntity(person);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public ClassificationPerson update(ClassificationPerson person) {
        ClassificationPersonEntity entity = mapper.toEntity(person);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void delete(String codClassificacaoPessoa) {
        repository.deleteById(codClassificacaoPessoa);
    }

    @Override
    public Optional<ClassificationPerson> findById(String codClassificacaoPessoa) {
        return repository.findById(codClassificacaoPessoa).map(mapper::toDomain);
    }

    @Override
    public Page<ClassificationPerson> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }
}

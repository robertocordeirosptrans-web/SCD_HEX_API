package br.sptrans.scd.auth.adapter.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.ClassificationPersonResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.mapper.ClassificationPersonRestMapper;
import br.sptrans.scd.auth.adapter.in.rest.request.CreateClassificationPersonRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.UpdateClassificationPersonRequest;
import br.sptrans.scd.auth.application.port.in.ClassificationPersonCase;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/classification-person")
@RequiredArgsConstructor
public class ClassificationPersonController {
    private final ClassificationPersonCase useCase;
    private final ClassificationPersonRestMapper mapper;

    @PostMapping
    public ResponseEntity<ClassificationPersonResponseDTO> create(@Valid @RequestBody CreateClassificationPersonRequest request) {
        var created = useCase.create(request);
        return ResponseEntity.ok(mapper.toDto(created));
    }

    @PutMapping
    public ResponseEntity<ClassificationPersonResponseDTO> update(@Valid @RequestBody UpdateClassificationPersonRequest request) {
        var updated = useCase.update(request);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{codClassificacaoPessoa}")
    public ResponseEntity<Void> delete(@PathVariable String codClassificacaoPessoa) {
        useCase.delete(codClassificacaoPessoa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codClassificacaoPessoa}")
    public ResponseEntity<ClassificationPersonResponseDTO> findById(@PathVariable String codClassificacaoPessoa) {
        return useCase.findById(codClassificacaoPessoa)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<PageResponse<ClassificationPersonResponseDTO>> list(Pageable pageable) {
        Page<ClassificationPersonResponseDTO> dtoPage = useCase.list(pageable).map(mapper::toDto);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }
}

package br.sptrans.scd.auth.adapter.port.in.rest;

import java.time.LocalDate;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.service.ManageProfileGroupService;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/group-profile")
@Tag(name = "Grupos - Perfis", description = "Endpoints para associação de Grupos com Perfis - Versão 1")
public class GroupProfileController {

    @Autowired
    private ManageProfileGroupService manageProfileGroupService;

    @GetMapping
    public ResponseEntity<PageResponse<GroupProfileResponseDTO>> getAll(
            Pageable pageable
    ) {
        Page<GroupProfileResponseDTO> dtoPage = manageProfileGroupService.findAllGroupProfile(pageable)
                .map(GroupProfileResponseDTO::new);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{codGrupo}/{codPerfil}")
    public ResponseEntity<GroupProfile> getById(@PathVariable String codGrupo, @PathVariable String codPerfil) {
        Optional<GroupProfile> groupProfile = manageProfileGroupService.findByCodGrupoAndCodPerfil(codGrupo, codPerfil);
        return groupProfile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GroupProfile> create(@RequestBody GroupProfile groupProfile) {
        GroupProfile created = manageProfileGroupService.saveGroupProfile(groupProfile);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{codGrupo}/{codPerfil}")
    public ResponseEntity<GroupProfile> update(@PathVariable String codGrupo, @PathVariable String codPerfil, @RequestBody GroupProfile groupProfile) {
        groupProfile.setId(new GroupProfileKey(codGrupo, codPerfil));
        GroupProfile updated = manageProfileGroupService.saveGroupProfile(groupProfile);
        return ResponseEntity.ok(updated);
    }

    public record GroupProfileResponseDTO(
            String codGrupo,
            String codPerfil,
            Long idUsuarioManutencao,
            String codStatus,
            LocalDate dtModi
            ) {

        public GroupProfileResponseDTO(GroupProfile grupoPerfil) {
            this(
                    grupoPerfil.getGrupo() != null ? grupoPerfil.getGrupo().getCodGrupo() : null,
                    grupoPerfil.getPerfil() != null ? grupoPerfil.getPerfil().getCodPerfil() : null,
                    grupoPerfil.getIdUsuarioManutencao(),
                    grupoPerfil.getCodStatus(),
                    grupoPerfil.getDtModi()
            );
        }
    }

}

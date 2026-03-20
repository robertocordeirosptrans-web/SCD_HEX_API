package br.sptrans.scd.auth.adapter.port.in.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<GroupProfile> grupos = manageProfileGroupService.findAllGroupProfile();
        List<GroupProfileResponseDTO> grupoDTOs = grupos.stream()
                .map(GroupProfileResponseDTO::new)
                .toList();
        return ResponseEntity.ok(PageResponse.fromList(grupoDTOs, page, size));
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
                    grupoPerfil.getGroup() != null ? grupoPerfil.getGroup().getCodGrupo() : null,
                    grupoPerfil.getProfile() != null ? grupoPerfil.getProfile().getCodPerfil() : null,
                    grupoPerfil.getIdUsuarioManutencao(),
                    grupoPerfil.getCodStatus(),
                    grupoPerfil.getDtModi()
            );
        }
    }

}

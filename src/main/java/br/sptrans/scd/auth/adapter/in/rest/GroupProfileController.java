package br.sptrans.scd.auth.adapter.in.rest;

import br.sptrans.scd.auth.application.service.ManageProfileGroupService;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/group-profile")
@Tag(name = "Grupos - Perfis", description = "Endpoints para associação de Grupos com Perfis - Versão 1")
public class GroupProfileController {

    @Autowired
    private ManageProfileGroupService manageProfileGroupService;

    @GetMapping
    public ResponseEntity<List<GroupProfile>> getAll() {
        return ResponseEntity.ok(manageProfileGroupService.findAllGroupProfile());
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
}

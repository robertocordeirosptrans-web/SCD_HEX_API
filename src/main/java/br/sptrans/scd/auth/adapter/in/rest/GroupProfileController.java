package br.sptrans.scd.auth.adapter.in.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.GroupProfileRequestDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.GroupProfileResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.mapper.GroupProfileRestMapper;
import br.sptrans.scd.auth.application.service.ManageProfileGroupService;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.security.CacPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/group-profile")
@Tag(name = "Grupos - Perfis", description = "Endpoints para associação de Grupos com Perfis - Versão 1")
public class GroupProfileController {

    @Autowired
    private ManageProfileGroupService manageProfileGroupService;

    @Autowired
    private GroupProfileRestMapper groupProfileRestMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
    public ResponseEntity<PageResponse<GroupProfileResponseDTO>> getAll(
            Pageable pageable
    ) {
        Page<GroupProfileResponseDTO> dtoPage = manageProfileGroupService.findAllGroupProfile(pageable)
                .map(groupProfileRestMapper::toDto);
        return ResponseEntity.ok(PageResponse.fromPage(dtoPage));
    }

    @GetMapping("/{codGrupo}/{codPerfil}")
    @PreAuthorize("hasAuthority('" + CacPermissions.LISASSUSUPER + "')")
    public ResponseEntity<GroupProfileResponseDTO> getById(@PathVariable String codGrupo, @PathVariable String codPerfil) {
        Optional<GroupProfile> groupProfile = manageProfileGroupService.findByCodGrupoAndCodPerfil(codGrupo, codPerfil);
        return groupProfile.map(groupProfileRestMapper::toDto).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    public ResponseEntity<GroupProfileResponseDTO> create(@Valid @RequestBody GroupProfileRequestDTO request) {
        GroupProfile groupProfile = new GroupProfile();
        groupProfile.setId(new GroupProfileKey(request.codGrupo(), request.codPerfil()));
        groupProfile.setCodStatus(request.codStatus());
        GroupProfile created = manageProfileGroupService.saveGroupProfile(groupProfile);
        return ResponseEntity.ok(groupProfileRestMapper.toDto(created));
    }

    @PutMapping("/{codGrupo}/{codPerfil}")
    @PreAuthorize("hasAuthority('" + CacPermissions.ASSPERAOUSU + "')")
    public ResponseEntity<GroupProfileResponseDTO> update(@PathVariable String codGrupo, @PathVariable String codPerfil, @Valid @RequestBody GroupProfileRequestDTO request) {
        GroupProfile groupProfile = new GroupProfile();
        groupProfile.setId(new GroupProfileKey(codGrupo, codPerfil));
        groupProfile.setCodStatus(request.codStatus());
        GroupProfile updated = manageProfileGroupService.saveGroupProfile(groupProfile);
        return ResponseEntity.ok(groupProfileRestMapper.toDto(updated));
    }

}

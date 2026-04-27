package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCustomResponseDTO {
    private String codGrupo;
    private String nomGrupo;
    private LocalDateTime dtModi;
    private String codStatus;
}

package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserProfile {

    private UserProfileId id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDateTime dtModi;
    private User user;
    private Profile perfil;

  


}
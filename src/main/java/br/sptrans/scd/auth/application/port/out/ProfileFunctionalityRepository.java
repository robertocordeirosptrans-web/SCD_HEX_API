package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.ProfileFunctionalityKey;

public interface ProfileFunctionalityRepository {

    Optional<ProfileFunctionality> findById_CodPerfilAndId_CodFuncionalidadeAndId_CodSistema(String codPerfil, String codFuncionalidade, String codSistema);

    List<ProfileFunctionality> findById_CodPerfil(String codPerfil);

    Optional<ProfileFunctionality> findById(ProfileFunctionalityKey id);

    List<ProfileFunctionality> findAll();

    ProfileFunctionality save(ProfileFunctionality entity);

    void delete(ProfileFunctionality entity);

    void deleteById(ProfileFunctionalityKey id);

    long count();
}

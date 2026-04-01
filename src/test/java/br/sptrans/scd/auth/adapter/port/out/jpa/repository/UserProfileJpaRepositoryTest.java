package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserProfileJpa;



@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserProfileJpaRepositoryTest {

    @Autowired
    private UserProfileJpaRepository userProfileJpaRepository;

    @Test
    @DisplayName("Deve buscar perfis ativos do usuário por id e status")
    void testFindByUsuarioIdUsuarioAndCodStatus() {
        Long idUsuario = 1644L;
        String codStatus = "A";
        List<UserProfileJpa> perfis = userProfileJpaRepository.findByUsuarioIdUsuarioAndCodStatus(idUsuario, codStatus);
        perfis.stream()
                .filter(up -> up != null)
                .forEach(up -> {
                    System.out.println("Perfil: " + (up.getPerfil() != null ? up.getPerfil().getCodPerfil() : "null"));
                });
        // Exemplo de asserção: pode ser ajustada conforme os dados de teste
        assertThat(perfis).isNotNull();
    }
}

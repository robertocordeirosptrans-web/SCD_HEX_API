package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "ROTINAS", schema = "SPTRANSDBA")
public class SystemsEntityJpa {

    @Id
    @Column(name = "COD_SISTEMA", length = 10)
    private String codSistema;

    @Column(name = "NOM_SISTEMA", length = 10)
    private String nomSistema;

    @Column(name = "NOM_CAMINHO_DIRETORIO", length = 1)
    private String nomCaminhoDiretorio;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

}

package br.sptrans.scd.product.adapter.out.persistence.entity;


import java.time.LocalDateTime;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ESPECIES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesEntityJpa {

    @Id
    @NotNull
    @Column(name = "COD_ESPECIE", nullable = false, length = 20)
    private String codEspecie;

    @Column(name = "DES_ESPECIE", length = 60)
    private String desEspecie;

    @NotNull
    @Column(name = "ST_ESPECIES", length = 1)
    private String codStatus;

    @NotNull
    @Column(name = "DT_CADASTRO", length = 20)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO", length = 20)
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private UserEntityJpa usuarioCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_MANUTENCAO")
    private UserEntityJpa usuarioManutencao;
}

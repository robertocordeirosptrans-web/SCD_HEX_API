package br.sptrans.scd.channel.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ENTIDADE_CONTATO", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactChannelEntityJpa {
    @Id
    @Column(name = "COD_CONTATO", nullable = false, length = 20)
    private String codContato;

    @Column(name = "COD_FORNECEDOR", length = 20)
    private String codFornecedor;

    @Column(name = "COD_EMPREGADOR", length = 20)
    private String codEmpregador;

    @Column(name = "DES_CONTATO", length = 60)
    private String desContato;

    @Column(name = "DES_EMAIL_CONTATO", length = 60)
    private String desEmailContato;

    @Column(name = "NUM_DDD")
    private Integer numDDD;

    @Column(name = "NUM_FONE")
    private Integer numFone;

    @Column(name = "NUM_FONE_RAMAL")
    private Integer numFoneRamal;

    @Column(name = "NUM_FAX")
    private Integer numFax;

    @Column(name = "NUM_FAX_RAMAL")
    private Integer numFaxRamal;

    @Column(name = "ST_ENTIDADE_CONTATO", length = 1)
    private String stEntidadeContato;

    @Column(name = "DES_COMENTARIOS", length = 60)
    private String desComentarios;

    @Column(name = "COD_TIPO_DOCUMENTO", length = 4)
    private String codTipoDocumento;

    @Column(name = "COD_DOCUMENTO", length = 20)
    private String codDocumento;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "COD_CANAL", length = 20)
    private String codCanal;
}

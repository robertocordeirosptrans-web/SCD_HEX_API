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
@Table(name = "ENDERECOS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressChannelEntityJpa {

    @Id
    @Column(name = "COD_ENDERECO", nullable = false, length = 20)
    private String codEndereco;

    @Column(name = "COD_EMPREGADOR", length = 20)
    private String codEmpregador;

    @Column(name = "DES_LOGRADOURO", length = 100)
    private String desLogradouro;

    @Column(name = "COD_FORNECEDOR", length = 20)
    private String codFornecedor;

    @Column(name = "COD_TIPO_ENDERECO", length = 4)
    private String codTipoEndereco;

    @Column(name = "COD_CEP", length = 10)
    private String codCEP;

    @Column(name = "DES_BAIRRO", length = 60)
    private String desBairro;

    @Column(name = "DES_CIDADE", length = 60)
    private String desCidade;

    @Column(name = "DES_UF", length = 2)
    private String desUF;

    @Column(name = "NUM_DDD")
    private Integer numDDD;

    @Column(name = "NUM_FONE")
    private Integer numFone;

    @Column(name = "NUM_FAX")
    private Integer numFax;

    @Column(name = "DES_OBS", length = 200)
    private String desObs;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ST_ENDERECOS", length = 1)
    private String stEnderecos;

    @Column(name = "DT_VALIDADE")
    private LocalDateTime dtValidade;

    @Column(name = "COD_SEQ")
    private Integer codSeq;

    @Column(name = "DES_NUMERO", length = 10)
    private String desNumero;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "COD_CANAL", length = 20)
    private String codCanal;
}

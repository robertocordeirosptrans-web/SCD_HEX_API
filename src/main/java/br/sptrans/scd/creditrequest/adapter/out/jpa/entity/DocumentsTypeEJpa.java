package br.sptrans.scd.creditrequest.adapter.out.jpa.entity;

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
@Table(name = "TP_DOCUMENTOS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsTypeEJpa {
    @Id
    @Column(name = "COD_TIPO_DOCUMENTO", length = 20)
    private String codTipoDocumento;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "DT_INICIO_VIGENCIA")
    private LocalDateTime dtInicioVigencia;

    @Column(name = "DT_TERMINO_VIGENCIA")
    private LocalDateTime dtTerminoVigencia;

    @Column(name = "ST_TIPOS_DOCUMENTOS", length = 1)
    private String codSituacao;

}

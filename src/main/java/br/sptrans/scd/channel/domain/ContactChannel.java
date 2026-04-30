package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ContactChannel {
    private String codContato;
    private String codFornecedor;
    private String codEmpregador;
    private String desContato;
    private String desEmailContato;
    private Integer numDDD;
    private Integer numFone;
    private Integer numFoneRamal;
    private Integer numFax;
    private Integer numFaxRamal;
    private String stEntidadeContato;
    private String desComentarios;
    private String codTipoDocumento;
    private String codDocumento;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioManutencao;
    private User idUsuarioCadastro;
    private SalesChannel codCanal;

    // --- Métodos de Fábrica e Atualização (DDD) ---

    /**
     * Cria uma nova instância de ContactChannel, encapsulando validações obrigatórias.
     */
    public static ContactChannel criar(
            String codContato,
            String codFornecedor,
            String codEmpregador,
            String desContato,
            String desEmailContato,
            Integer numDDD,
            Integer numFone,
            Integer numFoneRamal,
            Integer numFax,
            Integer numFaxRamal,
            String stEntidadeContato,
            String desComentarios,
            String codTipoDocumento,
            String codDocumento,
            LocalDateTime dtCadastro,
            LocalDateTime dtManutencao,
            User idUsuarioManutencao,
            User idUsuarioCadastro,
            SalesChannel codCanal
    ) {
        if (codContato == null || codContato.isEmpty()) {
            throw new IllegalArgumentException("Código do contato é obrigatório");
        }
        if (desContato == null || desContato.isEmpty()) {
            throw new IllegalArgumentException("Descrição do contato é obrigatória");
        }
        // Outras validações podem ser adicionadas aqui
        return new ContactChannel(
                codContato, codFornecedor, codEmpregador, desContato, desEmailContato, numDDD, numFone, numFoneRamal,
                numFax, numFaxRamal, stEntidadeContato, desComentarios, codTipoDocumento, codDocumento,
                dtCadastro, dtManutencao, idUsuarioManutencao, idUsuarioCadastro, codCanal
        );
    }

    /**
     * Atualiza os dados do contato, encapsulando regras de negócio.
     */
    public void atualizar(
            String codFornecedor,
            String codEmpregador,
            String desContato,
            String desEmailContato,
            Integer numDDD,
            Integer numFone,
            Integer numFoneRamal,
            Integer numFax,
            Integer numFaxRamal,
            String stEntidadeContato,
            String desComentarios,
            String codTipoDocumento,
            String codDocumento,
            LocalDateTime dtManutencao,
            User idUsuarioManutencao,
            SalesChannel codCanal
    ) {
        if (desContato == null || desContato.isEmpty()) {
            throw new IllegalArgumentException("Descrição do contato é obrigatória");
        }
        this.codFornecedor = codFornecedor;
        this.codEmpregador = codEmpregador;
        this.desContato = desContato;
        this.desEmailContato = desEmailContato;
        this.numDDD = numDDD;
        this.numFone = numFone;
        this.numFoneRamal = numFoneRamal;
        this.numFax = numFax;
        this.numFaxRamal = numFaxRamal;
        this.stEntidadeContato = stEntidadeContato;
        this.desComentarios = desComentarios;
        this.codTipoDocumento = codTipoDocumento;
        this.codDocumento = codDocumento;
        this.dtManutencao = dtManutencao;
        this.idUsuarioManutencao = idUsuarioManutencao;
        this.codCanal = codCanal;
    }
}

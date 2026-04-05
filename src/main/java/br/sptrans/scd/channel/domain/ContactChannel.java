package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ContactChannel {
    private final String codContato;

    @Setter private String codFornecedor;

    @Setter private String codEmpregador;

    @Setter private String desContato;

    @Setter private String desEmailContato;

    @Setter private Integer numDDD;

    @Setter private Integer numFone;

    @Setter private Integer numFoneRamal;

    @Setter private Integer numFax;

    @Setter private Integer numFaxRamal;

    @Setter private String stEntidadeContato;

    @Setter private String desComentarios;

    @Setter private String codTipoDocumento;

    @Setter private String codDocumento;

    private final LocalDateTime dtCadastro;
    @Setter private LocalDateTime dtManutencao;

    @Setter private User idUsuarioManutencao;

    private final User idUsuarioCadastro;

    @Setter private SalesChannel codCanal;

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

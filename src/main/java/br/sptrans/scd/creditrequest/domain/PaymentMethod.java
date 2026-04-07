package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethod {

    private String codFormaPagto;
    private String desFormaPagto;
    private Long idUsuarioCadastro;
    private Long idUsuarioManutencao;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;

    /**
     * Exemplo: Verifica se o método de pagamento está ativo.
     */
    public boolean isAtivo() {
        return "A".equalsIgnoreCase(this.desFormaPagto); // Ajuste conforme regra real
    }

    /**
     * Valida se o método de pagamento pode ser aceito.
     */
    public boolean validarAceitacao() {
        // Exemplo: só aceita se ativo e código não for nulo
        return isAtivo() && this.codFormaPagto != null && !this.codFormaPagto.isBlank();
    }
}

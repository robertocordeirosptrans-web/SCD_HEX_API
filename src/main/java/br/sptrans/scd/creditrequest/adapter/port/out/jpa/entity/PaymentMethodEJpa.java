package br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity para tabela FM_PAGAMENTOS (Formas de Pagamento).
 * 
 * Esta é uma entidade de referência/lookup (não um agregado raiz).
 * Armazenada no adapter, não no domínio.
 */
@Table(name = "FM_PAGAMENTOS", schema = "SPTRANSDBA")
@Entity(name = "fm_pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodEJpa {
    
    @Id
    @Column(name = "COD_FORMA_PAGTO", length = 15, nullable = false)
    private String codFormaPagto;

    @Column(name = "DES_FORMA_PAGTO", length = 15, nullable = false)
    private String desFormaPagto;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @PrePersist
    protected void onCreate() {
        dtCadastro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dtManutencao = LocalDateTime.now();
    }
}

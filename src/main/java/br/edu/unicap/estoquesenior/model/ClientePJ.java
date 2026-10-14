package br.edu.unicap.estoquesenior.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clientes_pj")
@PrimaryKeyJoinColumn(name = "cliente_id")
public class ClientePJ extends Cliente {

    @NotBlank
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @NotBlank
    @Column(nullable = false)
    private String razaoSocial;
}
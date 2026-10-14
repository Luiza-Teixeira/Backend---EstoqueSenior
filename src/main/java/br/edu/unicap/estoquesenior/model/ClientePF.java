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
@Table(name = "clientes_pf")
@PrimaryKeyJoinColumn(name = "cliente_id")
public class ClientePF extends Cliente {

    @NotBlank
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
}
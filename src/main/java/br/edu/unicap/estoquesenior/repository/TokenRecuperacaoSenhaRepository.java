package br.edu.unicap.estoquesenior.repository;

import br.edu.unicap.estoquesenior.model.TokenRecuperacaoSenha;
import br.edu.unicap.estoquesenior.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperacaoSenhaRepository
        extends JpaRepository<TokenRecuperacaoSenha, Long> {

    Optional<TokenRecuperacaoSenha> findByTokenHash(String tokenHash);

    void deleteByUsuario(Usuario usuario);
}
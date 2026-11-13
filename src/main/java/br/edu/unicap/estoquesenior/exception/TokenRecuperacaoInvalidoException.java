package br.edu.unicap.estoquesenior.exception;

public class TokenRecuperacaoInvalidoException
        extends RuntimeException {

    public TokenRecuperacaoInvalidoException() {
        super("Token de recuperação inválido ou expirado.");
    }
}
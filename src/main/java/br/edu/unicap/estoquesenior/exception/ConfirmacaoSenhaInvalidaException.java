package br.edu.unicap.estoquesenior.exception;

public class ConfirmacaoSenhaInvalidaException
        extends RuntimeException {

    public ConfirmacaoSenhaInvalidaException() {
        super("A confirmação da senha não corresponde à nova senha.");
    }
}
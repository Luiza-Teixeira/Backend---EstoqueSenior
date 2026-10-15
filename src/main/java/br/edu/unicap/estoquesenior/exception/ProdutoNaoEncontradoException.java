package br.edu.unicap.estoquesenior.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

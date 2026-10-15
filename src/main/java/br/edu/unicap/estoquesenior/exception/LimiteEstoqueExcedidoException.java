package br.edu.unicap.estoquesenior.exception;

public class LimiteEstoqueExcedidoException extends RuntimeException {

    public LimiteEstoqueExcedidoException(String mensagem) {
        super(mensagem);
    }
}

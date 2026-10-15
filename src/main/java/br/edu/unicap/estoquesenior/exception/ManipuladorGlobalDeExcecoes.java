package br.edu.unicap.estoquesenior.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<RespostaDeErro> tratarProdutoNaoEncontrado(
            ProdutoNaoEncontradoException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<RespostaDeErro> tratarEstoqueInsuficiente(
            EstoqueInsuficienteException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(QuantidadeVendaInvalidaException.class)
    public ResponseEntity<RespostaDeErro> tratarQuantidadeVendaInvalida(
            QuantidadeVendaInvalidaException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(QuantidadeEntradaInvalidaException.class)
    public ResponseEntity<RespostaDeErro> tratarQuantidadeEntradaInvalida(
            QuantidadeEntradaInvalidaException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(LimiteEstoqueExcedidoException.class)
    public ResponseEntity<RespostaDeErro> tratarLimiteEstoqueExcedido(
            LimiteEstoqueExcedidoException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaDeErro> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .distinct()
                .map(campo -> campo + ": " + mensagemDoCampo(ex, campo))
                .toList();

        RespostaDeErro corpo = new RespostaDeErro(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Um ou mais campos estão inválidos.",
                request.getRequestURI(),
                detalhes
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(corpo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaDeErro> tratarGenerico(
            Exception ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request
        );
    }

    private ResponseEntity<RespostaDeErro> construirResposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request) {

        RespostaDeErro corpo = new RespostaDeErro(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(corpo);
    }

    private String mensagemDoCampo(
            MethodArgumentNotValidException ex,
            String campo) {

        FieldError erro =
                ex.getBindingResult().getFieldError(campo);

        return erro != null && erro.getDefaultMessage() != null
                ? erro.getDefaultMessage()
                : "valor inválido";
    }
}

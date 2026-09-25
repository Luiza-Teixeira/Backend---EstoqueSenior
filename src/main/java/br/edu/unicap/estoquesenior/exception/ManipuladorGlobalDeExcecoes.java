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
import java.util.Locale;

/**
 * Centraliza o tratamento de exceções de toda a API, convertendo cada uma
 * em uma resposta HTTP padronizada (RespostaDeErro), com o status code correto.
 *
 * Este handler NÃO exige nenhuma alteração no VendaService, Produto, Venda
 * ou nos Repositories já existentes — ele apenas intercepta as exceções que
 * esses arquivos já lançam (IllegalArgumentException) e as bean validations
 * (@NotBlank, @Positive etc. já presentes nas entidades).
 *
 * Como o VendaService atual lança sempre IllegalArgumentException (para
 * quantidade inválida, produto não encontrado e estoque insuficiente), o
 * status HTTP mais específico (404 / 409) é inferido pelo texto da mensagem.
 * Se um dia o service passar a lançar exceções próprias, basta adicionar
 * novos métodos @ExceptionHandler aqui — nada mais muda.
 */
@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    // --- IllegalArgumentException: é o que VendaService.registrarVenda() já lança ---

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespostaDeErro> tratarIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        String mensagem = ex.getMessage() != null ? ex.getMessage() : "Requisição inválida.";
        HttpStatus status = inferirStatus(mensagem);
        return construirResposta(status, mensagem, request);
    }

    /**
     * Infere o status HTTP mais adequado a partir do texto da mensagem lançada
     * pelo VendaService, sem precisar alterar o service para lançar tipos distintos.
     * "Produto não encontrado."      -> 404 NOT_FOUND
     * "Estoque insuficiente."        -> 409 CONFLICT
     * qualquer outra (ex: quantidade)-> 400 BAD_REQUEST
     */
    private HttpStatus inferirStatus(String mensagem) {
        String msg = mensagem.toLowerCase(Locale.ROOT);
        if (msg.contains("não encontrado") || msg.contains("nao encontrado")) {
            return HttpStatus.NOT_FOUND;
        }
        if (msg.contains("insuficiente")) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    // --- Validação de argumentos (@Valid em @RequestBody, ex: campos do Produto) ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaDeErro> tratarValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    // --- Qualquer exceção não prevista: nunca deixar vazar stacktrace pro cliente ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaDeErro> tratarGenerico(
            Exception ex, HttpServletRequest request) {
        return construirResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request
        );
    }

    // --- Utilitários privados ---

    private ResponseEntity<RespostaDeErro> construirResposta(
            HttpStatus status, String mensagem, HttpServletRequest request) {

        RespostaDeErro corpo = new RespostaDeErro(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(corpo);
    }

    private String mensagemDoCampo(MethodArgumentNotValidException ex, String campo) {
        FieldError erro = ex.getBindingResult().getFieldError(campo);
        return erro != null && erro.getDefaultMessage() != null
                ? erro.getDefaultMessage()
                : "valor inválido";
    }
}

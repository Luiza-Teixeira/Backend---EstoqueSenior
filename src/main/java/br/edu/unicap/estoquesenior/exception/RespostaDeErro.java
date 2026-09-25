package br.edu.unicap.estoquesenior.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padronizado de resposta de erro devolvido pela API.
 * Todo erro tratado pelo ManipuladorGlobalDeExcecoes é convertido para este
 * formato, garantindo consistência para quem consome a API (front-end Padrão
 * e Simplificado).
 */
public class RespostaDeErro {

    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String caminho;
    private List<String> detalhes;

    public RespostaDeErro() {
    }

    public RespostaDeErro(LocalDateTime timestamp, int status, String erro, String mensagem, String caminho) {
        this.timestamp = timestamp;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
    }

    public RespostaDeErro(LocalDateTime timestamp, int status, String erro, String mensagem, String caminho, List<String> detalhes) {
        this(timestamp, status, erro, mensagem, caminho);
        this.detalhes = detalhes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<String> detalhes) {
        this.detalhes = detalhes;
    }
}

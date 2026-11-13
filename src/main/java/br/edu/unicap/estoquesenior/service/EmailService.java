package br.edu.unicap.estoquesenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${APP_FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Value("${MAIL_FROM:no-reply@estoquesenior.local}")
    private String remetente;

    public void enviarRecuperacaoSenha(
            String destinatario,
            String token) {

        String link = frontendUrl
                + "/redefinir-senha?token="
                + token;

        SimpleMailMessage mensagem =
                new SimpleMailMessage();

        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject(
                "Redefinição de senha - Estoque Sênior"
        );

        mensagem.setText(
                """
                Olá!

                Recebemos uma solicitação para redefinir a senha da sua conta no Estoque Sênior.

                Acesse o link abaixo para criar uma nova senha:

                %s

                Este link é válido por 30 minutos e só pode ser utilizado uma vez.

                Se você não solicitou a redefinição, ignore este e-mail.
                """.formatted(link)
        );

        mailSender.send(mensagem);
    }
}
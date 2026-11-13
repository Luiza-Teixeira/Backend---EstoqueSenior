package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.RedefinirSenhaRequestDTO;
import br.edu.unicap.estoquesenior.exception.ConfirmacaoSenhaInvalidaException;
import br.edu.unicap.estoquesenior.exception.TokenRecuperacaoInvalidoException;
import br.edu.unicap.estoquesenior.model.TokenRecuperacaoSenha;
import br.edu.unicap.estoquesenior.model.Usuario;
import br.edu.unicap.estoquesenior.repository.TokenRecuperacaoSenhaRepository;
import br.edu.unicap.estoquesenior.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private static final int TEMPO_EXPIRACAO_MINUTOS = 30;

    private final UsuarioRepository usuarioRepository;

    private final TokenRecuperacaoSenhaRepository
            tokenRecuperacaoSenhaRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    @Transactional
    public void solicitarRecuperacao(String email) {

        String emailNormalizado =
                email.trim().toLowerCase();

        Optional<Usuario> usuarioOptional =
                usuarioRepository.findByEmail(
                        emailNormalizado
                );

        if (usuarioOptional.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOptional.get();

        tokenRecuperacaoSenhaRepository
                .deleteByUsuario(usuario);

        String token = gerarTokenSeguro();

        TokenRecuperacaoSenha recuperacao =
                new TokenRecuperacaoSenha();

        recuperacao.setUsuario(usuario);

        recuperacao.setTokenHash(
                gerarHashToken(token)
        );

        recuperacao.setExpiraEm(
                LocalDateTime.now()
                        .plusMinutes(
                                TEMPO_EXPIRACAO_MINUTOS
                        )
        );

        tokenRecuperacaoSenhaRepository
                .save(recuperacao);

        emailService.enviarRecuperacaoSenha(
                usuario.getEmail(),
                token
        );
    }

    @Transactional(
            noRollbackFor = TokenRecuperacaoInvalidoException.class
    )
    public void redefinirSenha(
            RedefinirSenhaRequestDTO dto) {

        if (!dto.novaSenha()
                .equals(dto.confirmacaoSenha())) {

            throw new ConfirmacaoSenhaInvalidaException();
        }

        String tokenHash =
                gerarHashToken(dto.token());

        TokenRecuperacaoSenha recuperacao =
                tokenRecuperacaoSenhaRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(
                                TokenRecuperacaoInvalidoException::new
                        );

        if (recuperacao.getExpiraEm()
                .isBefore(LocalDateTime.now())) {

            tokenRecuperacaoSenhaRepository
                    .delete(recuperacao);

            throw new TokenRecuperacaoInvalidoException();
        }

        Usuario usuario =
                recuperacao.getUsuario();

        usuario.setSenha(
                passwordEncoder.encode(
                        dto.novaSenha()
                )
        );

        usuarioRepository.save(usuario);

        tokenRecuperacaoSenhaRepository
                .delete(recuperacao);
    }

    private String gerarTokenSeguro() {

        byte[] bytes = new byte[32];

        new SecureRandom()
                .nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String gerarHashToken(
            String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash = digest.digest(
                    token.getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {

            throw new IllegalStateException(
                    "Não foi possível processar o token.",
                    ex
            );
        }
    }
}
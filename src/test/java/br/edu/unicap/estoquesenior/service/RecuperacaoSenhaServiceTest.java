package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.RedefinirSenhaRequestDTO;
import br.edu.unicap.estoquesenior.exception.ConfirmacaoSenhaInvalidaException;
import br.edu.unicap.estoquesenior.exception.TokenRecuperacaoInvalidoException;
import br.edu.unicap.estoquesenior.model.TokenRecuperacaoSenha;
import br.edu.unicap.estoquesenior.model.Usuario;
import br.edu.unicap.estoquesenior.repository.TokenRecuperacaoSenhaRepository;
import br.edu.unicap.estoquesenior.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperacaoSenhaRepository
            tokenRecuperacaoSenhaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @Test
    void deveCriarTokenEEnviarEmail() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(
                "luiza@estoquesenior.com"
        );

        when(usuarioRepository.findByEmail(
                "luiza@estoquesenior.com"
        )).thenReturn(Optional.of(usuario));

        recuperacaoSenhaService.solicitarRecuperacao(
                "luiza@estoquesenior.com"
        );

        ArgumentCaptor<TokenRecuperacaoSenha> captor =
                ArgumentCaptor.forClass(
                        TokenRecuperacaoSenha.class
                );

        verify(tokenRecuperacaoSenhaRepository)
                .deleteByUsuario(usuario);

        verify(tokenRecuperacaoSenhaRepository)
                .save(captor.capture());

        TokenRecuperacaoSenha tokenSalvo =
                captor.getValue();

        assertEquals(
                usuario,
                tokenSalvo.getUsuario()
        );

        assertNotNull(
                tokenSalvo.getTokenHash()
        );

        assertEquals(
                64,
                tokenSalvo.getTokenHash().length()
        );

        assertTrue(
                tokenSalvo.getExpiraEm()
                        .isAfter(LocalDateTime.now())
        );

        verify(emailService)
                .enviarRecuperacaoSenha(
                        eq("luiza@estoquesenior.com"),
                        anyString()
                );
    }

    @Test
    void naoDeveRevelarEmailInexistente() {

        when(usuarioRepository.findByEmail(
                "inexistente@email.com"
        )).thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> recuperacaoSenhaService
                        .solicitarRecuperacao(
                                "inexistente@email.com"
                        )
        );

        verifyNoInteractions(
                tokenRecuperacaoSenhaRepository
        );

        verifyNoInteractions(emailService);
    }

    @Test
    void deveRedefinirSenhaComTokenValido() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setSenha("senha-antiga");

        TokenRecuperacaoSenha recuperacao =
                new TokenRecuperacaoSenha();

        recuperacao.setUsuario(usuario);

        recuperacao.setExpiraEm(
                LocalDateTime.now()
                        .plusMinutes(20)
        );

        RedefinirSenhaRequestDTO dto =
                new RedefinirSenhaRequestDTO(
                        "token-valido",
                        "novaSenha123",
                        "novaSenha123"
                );

        when(tokenRecuperacaoSenhaRepository
                .findByTokenHash(anyString()))
                .thenReturn(
                        Optional.of(recuperacao)
                );

        when(passwordEncoder.encode(
                "novaSenha123"
        )).thenReturn("novo-hash");

        recuperacaoSenhaService
                .redefinirSenha(dto);

        assertEquals(
                "novo-hash",
                usuario.getSenha()
        );

        verify(usuarioRepository)
                .save(usuario);

        verify(tokenRecuperacaoSenhaRepository)
                .delete(recuperacao);
    }

    @Test
    void naoDeveAceitarTokenExpirado() {

        Usuario usuario = new Usuario();

        TokenRecuperacaoSenha recuperacao =
                new TokenRecuperacaoSenha();

        recuperacao.setUsuario(usuario);

        recuperacao.setExpiraEm(
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        RedefinirSenhaRequestDTO dto =
                new RedefinirSenhaRequestDTO(
                        "token-expirado",
                        "novaSenha123",
                        "novaSenha123"
                );

        when(tokenRecuperacaoSenhaRepository
                .findByTokenHash(anyString()))
                .thenReturn(
                        Optional.of(recuperacao)
                );

        assertThrows(
                TokenRecuperacaoInvalidoException.class,
                () -> recuperacaoSenhaService
                        .redefinirSenha(dto)
        );

        verify(tokenRecuperacaoSenhaRepository)
                .delete(recuperacao);

        verify(usuarioRepository, never())
                .save(any());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void naoDeveAceitarConfirmacaoDiferente() {

        RedefinirSenhaRequestDTO dto =
                new RedefinirSenhaRequestDTO(
                        "token",
                        "novaSenha123",
                        "outraSenha123"
                );

        assertThrows(
                ConfirmacaoSenhaInvalidaException.class,
                () -> recuperacaoSenhaService
                        .redefinirSenha(dto)
        );

        verifyNoInteractions(
                tokenRecuperacaoSenhaRepository
        );

        verifyNoInteractions(passwordEncoder);
    }
}
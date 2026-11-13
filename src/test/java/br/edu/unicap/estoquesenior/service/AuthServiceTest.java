package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.LoginRequestDTO;
import br.edu.unicap.estoquesenior.dto.LoginResponseDTO;
import br.edu.unicap.estoquesenior.exception.CredenciaisInvalidasException;
import br.edu.unicap.estoquesenior.model.Usuario;
import br.edu.unicap.estoquesenior.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRealizarLoginComCredenciaisValidas() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Luiza");
        usuario.setEmail("luiza@email.com");
        usuario.setSenha("senha-hash");

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "luiza@email.com",
                        "123456"
                );

        when(usuarioRepository.findByEmail("luiza@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("123456", "senha-hash"))
                .thenReturn(true);

        when(tokenService.gerarToken(usuario))
                .thenReturn("jwt-token");

        LoginResponseDTO resultado = authService.login(dto);

        assertEquals("jwt-token", resultado.token());
        assertEquals("Bearer", resultado.tipo());
        assertEquals(1L, resultado.usuario().id());
        assertEquals("Luiza", resultado.usuario().nome());
        assertEquals("luiza@email.com", resultado.usuario().email());

        verify(tokenService).gerarToken(usuario);
    }

    @Test
    void naoDeveRealizarLoginComEmailInexistente() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "inexistente@email.com",
                        "123456"
                );

        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(dto)
        );

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(tokenService);
    }

    @Test
    void naoDeveRealizarLoginComSenhaIncorreta() {

        Usuario usuario = new Usuario();
        usuario.setEmail("luiza@email.com");
        usuario.setSenha("senha-hash");

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        "luiza@email.com",
                        "senha-errada"
                );

        when(usuarioRepository.findByEmail("luiza@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("senha-errada", "senha-hash"))
                .thenReturn(false);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(dto)
        );

        verifyNoInteractions(tokenService);
    }
}
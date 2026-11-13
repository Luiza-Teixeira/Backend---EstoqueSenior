package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.UsuarioAtualizacaoDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioCadastroDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioResponseDTO;
import br.edu.unicap.estoquesenior.exception.EmailJaCadastradoException;
import br.edu.unicap.estoquesenior.exception.UsuarioNaoEncontradoException;
import br.edu.unicap.estoquesenior.model.Usuario;
import br.edu.unicap.estoquesenior.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {

        UsuarioCadastroDTO dto =
                new UsuarioCadastroDTO(
                        "Luiza",
                        "luiza@email.com",
                        "123456"
                );

        when(usuarioRepository.existsByEmail("luiza@email.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("senha-hash");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> {
                    Usuario usuario = invocacao.getArgument(0);
                    usuario.setId(1L);
                    return usuario;
                });

        UsuarioResponseDTO resultado = usuarioService.cadastrar(dto);

        assertEquals(1L, resultado.id());
        assertEquals("Luiza", resultado.nome());
        assertEquals("luiza@email.com", resultado.email());

        verify(passwordEncoder).encode("123456");

        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getSenha().equals("senha-hash")
        ));
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {

        UsuarioCadastroDTO dto =
                new UsuarioCadastroDTO(
                        "Luiza",
                        "luiza@email.com",
                        "123456"
                );

        when(usuarioRepository.existsByEmail("luiza@email.com"))
                .thenReturn(true);

        assertThrows(
                EmailJaCadastradoException.class,
                () -> usuarioService.cadastrar(dto)
        );

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {

        when(usuarioRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(1L)
        );
    }

    @Test
    void deveBuscarUsuarioPorId() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Luiza");
        usuario.setEmail("luiza@estoquesenior.com");
        usuario.setSenha("senha-hash");

        when(usuarioRepository.findById(1L))
                .thenReturn(java.util.Optional.of(usuario));

        UsuarioResponseDTO resultado =
                usuarioService.buscarPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals("Luiza", resultado.nome());
        assertEquals(
                "luiza@estoquesenior.com",
                resultado.email()
        );
    }

    @Test
    void deveAtualizarPerfil() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Luiza");
        usuario.setEmail("luiza@estoquesenior.com");
        usuario.setSenha("senha-hash");

        UsuarioAtualizacaoDTO dto =
                new UsuarioAtualizacaoDTO(
                        "Luiza Teixeira",
                        "luiza.novo@estoquesenior.com"
                );

        when(usuarioRepository.findById(1L))
                .thenReturn(java.util.Optional.of(usuario));

        when(usuarioRepository.existsByEmail(
                "luiza.novo@estoquesenior.com"
        )).thenReturn(false);

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        UsuarioResponseDTO resultado =
                usuarioService.atualizarPerfil(1L, dto);

        assertEquals("Luiza Teixeira", resultado.nome());

        assertEquals(
                "luiza.novo@estoquesenior.com",
                resultado.email()
        );

        assertEquals(
                "senha-hash",
                usuario.getSenha()
        );

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveAtualizarParaEmailJaCadastrado() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Luiza");
        usuario.setEmail("luiza@estoquesenior.com");

        UsuarioAtualizacaoDTO dto =
                new UsuarioAtualizacaoDTO(
                        "Luiza",
                        "outro@email.com"
                );

        when(usuarioRepository.findById(1L))
                .thenReturn(java.util.Optional.of(usuario));

        when(usuarioRepository.existsByEmail("outro@email.com"))
                .thenReturn(true);

        assertThrows(
                EmailJaCadastradoException.class,
                () -> usuarioService.atualizarPerfil(1L, dto)
        );

        verify(usuarioRepository, never())
                .save(any());
    }
}
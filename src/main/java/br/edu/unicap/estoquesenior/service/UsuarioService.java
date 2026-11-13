package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.UsuarioAtualizacaoDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioCadastroDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioResponseDTO;
import br.edu.unicap.estoquesenior.exception.EmailJaCadastradoException;
import br.edu.unicap.estoquesenior.exception.UsuarioNaoEncontradoException;
import br.edu.unicap.estoquesenior.model.Usuario;
import br.edu.unicap.estoquesenior.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {

        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        Usuario salvo = usuarioRepository.save(usuario);

        return converterParaResponse(salvo);
    }

    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        return converterParaResponse(usuario);
    }

    public UsuarioResponseDTO atualizarPerfil(
            Long id,
            UsuarioAtualizacaoDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        String novoEmail = dto.email()
                .trim()
                .toLowerCase();

        boolean emailFoiAlterado =
                !usuario.getEmail().equalsIgnoreCase(novoEmail);

        if (emailFoiAlterado
                && usuarioRepository.existsByEmail(novoEmail)) {

            throw new EmailJaCadastradoException();
        }

        usuario.setNome(dto.nome().trim());
        usuario.setEmail(novoEmail);

        Usuario salvo = usuarioRepository.save(usuario);

        return converterParaResponse(salvo);
    }

    private UsuarioResponseDTO converterParaResponse(
            Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
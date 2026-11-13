package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.LoginRequestDTO;
import br.edu.unicap.estoquesenior.dto.LoginResponseDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioResponseDTO;
import br.edu.unicap.estoquesenior.exception.CredenciaisInvalidasException;
import br.edu.unicap.estoquesenior.model.Usuario;
import br.edu.unicap.estoquesenior.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginResponseDTO login(LoginRequestDTO dto) {

        String email = dto.email().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String token = tokenService.gerarToken(usuario);

        UsuarioResponseDTO usuarioResponse =
                new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail()
                );

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuarioResponse
        );
    }
}
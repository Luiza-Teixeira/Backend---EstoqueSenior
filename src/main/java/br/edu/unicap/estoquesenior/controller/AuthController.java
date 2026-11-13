package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.EsqueciSenhaRequestDTO;
import br.edu.unicap.estoquesenior.dto.LoginRequestDTO;
import br.edu.unicap.estoquesenior.dto.LoginResponseDTO;
import br.edu.unicap.estoquesenior.dto.MensagemResponseDTO;
import br.edu.unicap.estoquesenior.dto.RedefinirSenhaRequestDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioCadastroDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioResponseDTO;
import br.edu.unicap.estoquesenior.service.AuthService;
import br.edu.unicap.estoquesenior.service.RecuperacaoSenhaService;
import br.edu.unicap.estoquesenior.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        UsuarioResponseDTO usuario =
                usuarioService.cadastrar(dto);

        URI location = URI.create(
                "/usuarios/" + usuario.id()
        );

        return ResponseEntity
                .created(location)
                .body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(
                authService.login(dto)
        );
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<MensagemResponseDTO> esqueciSenha(
            @Valid @RequestBody EsqueciSenhaRequestDTO dto) {

        recuperacaoSenhaService
                .solicitarRecuperacao(dto.email());

        return ResponseEntity.ok(
                new MensagemResponseDTO(
                        "Se o e-mail estiver cadastrado, você receberá instruções para redefinir sua senha."
                )
        );
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<MensagemResponseDTO> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaRequestDTO dto) {

        recuperacaoSenhaService
                .redefinirSenha(dto);

        return ResponseEntity.ok(
                new MensagemResponseDTO(
                        "Senha redefinida com sucesso."
                )
        );
    }
}
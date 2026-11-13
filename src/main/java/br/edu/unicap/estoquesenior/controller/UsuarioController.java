package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.UsuarioAtualizacaoDTO;
import br.edu.unicap.estoquesenior.dto.UsuarioResponseDTO;
import br.edu.unicap.estoquesenior.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obterUsuarioAutenticado(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(
                        obterUsuarioId(jwt)
                )
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuarioAutenticado(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UsuarioAtualizacaoDTO dto) {

        return ResponseEntity.ok(
                usuarioService.atualizarPerfil(
                        obterUsuarioId(jwt),
                        dto
                )
        );
    }

    private Long obterUsuarioId(Jwt jwt) {

        Number usuarioId = jwt.getClaim("usuarioId");

        return usuarioId.longValue();
    }
}
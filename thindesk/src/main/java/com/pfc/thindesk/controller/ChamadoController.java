package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.ChamadoRequest;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.service.ChamadoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {
    private final ChamadoService service;
    public ChamadoController(ChamadoService service) { this.service = service; }

    @GetMapping
    public List<Chamado> listarChamados(@RequestParam(required=false) String busca) {
        return service.listarChamados().stream()
            .filter(c -> busca == null || busca.isBlank() || (c.getDescricao() != null && c.getDescricao().toLowerCase(Locale.ROOT).contains(busca.toLowerCase(Locale.ROOT))))
            .sorted(Comparator.comparing(Chamado::getDataAtualizacao, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    }
    @GetMapping("/{id}")
    public Chamado buscar(@PathVariable String id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<Chamado> criar(@Valid @RequestBody ChamadoRequest request) {
        Chamado criado = service.criarChamado(request);
        return ResponseEntity.created(URI.create("/api/chamados/" + criado.getId())).body(criado);
    }
    @PutMapping("/{id}")
    public Chamado atualizar(@PathVariable String id, @Valid @RequestBody ChamadoRequest request) {
        return service.atualizarChamado(id, request);
    }
}

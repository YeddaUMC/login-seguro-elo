package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.service.ClienteService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping public List<Cliente> listar() { return service.listarClientes(); }
    @PostMapping public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        Cliente novo = service.novoCliente(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + novo.getId())).body(novo);
    }
}

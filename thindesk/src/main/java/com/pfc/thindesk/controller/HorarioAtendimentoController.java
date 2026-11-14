package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ajustes-horarios")
public class HorarioAtendimentoController {
    private final HorarioAtendimentoService service;
    public HorarioAtendimentoController(HorarioAtendimentoService service) { this.service = service; }
    @GetMapping public List<HorarioAtendimento> listar() { return service.listarTodos(); }
    @PostMapping public HorarioAtendimento salvar(@RequestBody HorarioAtendimento horario) { return service.salvar(horario); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id); return ResponseEntity.noContent().build();
    }
}

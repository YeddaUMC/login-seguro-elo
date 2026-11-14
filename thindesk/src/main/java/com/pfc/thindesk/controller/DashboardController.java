package com.pfc.thindesk.controller;

import com.pfc.thindesk.service.DashboardService;
import com.pfc.thindesk.service.DashboardService.Resultado;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }
    @GetMapping
    public Resultado consultar(@RequestParam(required=false) String inicio,
        @RequestParam(required=false) String fim, @RequestParam(required=false) String status,
        @RequestParam(required=false) String tipo, @RequestParam(required=false) String tecnico,
        @RequestParam(required=false) String busca) {
        return service.consultar(inicio,fim,status,tipo,tecnico,busca);
    }
    @GetMapping("/indicadores")
    public DashboardService.Indicadores indicadores(@RequestParam(required=false) String inicio,
        @RequestParam(required=false) String fim, @RequestParam(required=false) String status,
        @RequestParam(required=false) String tipo, @RequestParam(required=false) String tecnico,
        @RequestParam(required=false) String busca) {
        return service.consultar(inicio,fim,status,tipo,tecnico,busca).indicadores();
    }
    @GetMapping("/evolucao")
    public Map<String,Object> evolucao(@RequestParam(required=false) String inicio,
        @RequestParam(required=false) String fim, @RequestParam(required=false) String status,
        @RequestParam(required=false) String tipo, @RequestParam(required=false) String tecnico,
        @RequestParam(required=false) String busca) {
        return Map.of("evolucao", service.consultar(inicio,fim,status,tipo,tecnico,busca).evolucao());
    }
    @GetMapping("/situacoes")
    public Map<String,Object> situacoes(@RequestParam(required=false) String inicio,
        @RequestParam(required=false) String fim, @RequestParam(required=false) String status,
        @RequestParam(required=false) String tipo, @RequestParam(required=false) String tecnico,
        @RequestParam(required=false) String busca) {
        return Map.of("situacoes",service.consultar(inicio,fim,status,tipo,tecnico,busca).situacoes());
    }
    @GetMapping("/tipos")
    public Map<String,Object> tipos(@RequestParam(required=false) String inicio,
        @RequestParam(required=false) String fim, @RequestParam(required=false) String status,
        @RequestParam(required=false) String tipo, @RequestParam(required=false) String tecnico,
        @RequestParam(required=false) String busca) {
        return Map.of("tipos", service.consultar(inicio,fim,status,tipo,tecnico,busca).tipos());
    }
}

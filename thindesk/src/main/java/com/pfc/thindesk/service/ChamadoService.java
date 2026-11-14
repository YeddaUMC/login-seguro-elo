package com.pfc.thindesk.service;

import com.pfc.thindesk.dto.ChamadoRequest;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class ChamadoService {
    private final ChamadoRepository repository;
    public ChamadoService(ChamadoRepository repository) { this.repository = repository; }
    public List<Chamado> listarChamados() { return repository.findAll(); }
    public Chamado buscar(String id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Chamado não encontrado"));
    }
    public Chamado criarChamado(ChamadoRequest request) {
        Chamado chamado = new Chamado();
        chamado.setDataAbertura(LocalDateTime.now());
        atribuir(chamado, request);
        return repository.save(chamado);
    }
    public Chamado atualizarChamado(String id, ChamadoRequest request) {
        Chamado chamado = buscar(id);
        atribuir(chamado, request);
        return repository.save(chamado);
    }
    private void atribuir(Chamado chamado, ChamadoRequest request) {
        String status = DashboardService.normalizarStatus(request.status());
        if (!List.of("ABERTO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO").contains(status)) {
            throw new IllegalArgumentException("Status inválido. Use ABERTO, EM_ANDAMENTO, CONCLUIDO ou CANCELADO.");
        }
        String prioridade = request.prioridade() == null || request.prioridade().isBlank()
            ? "NORMAL" : request.prioridade().strip().toUpperCase();
        if (!List.of("BAIXA", "NORMAL", "ALTA", "URGENTE").contains(prioridade)) {
            throw new IllegalArgumentException("Prioridade inválida. Use BAIXA, NORMAL, ALTA ou URGENTE.");
        }
        LocalDateTime agora = LocalDateTime.now();
        chamado.setDescricao(request.descricao().strip());
        chamado.setStatus(status);
        chamado.setTipo(request.tipo().strip());
        chamado.setTecnico(limpar(request.tecnico()));
        chamado.setUsuario(limpar(request.usuario()));
        chamado.setPrioridade(prioridade);
        chamado.setConsideracoesTecnicas(limpar(request.consideracoesTecnicas()));
        chamado.setDataAtualizacao(agora);
        if (status.equals("CONCLUIDO") && chamado.getDataConclusao() == null) chamado.setDataConclusao(agora);
        if (!status.equals("CONCLUIDO")) chamado.setDataConclusao(null);
    }
    private static String limpar(String valor) { return valor == null ? "" : valor.strip(); }
}

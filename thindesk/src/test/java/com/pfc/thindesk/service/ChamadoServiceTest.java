package com.pfc.thindesk.service;

import com.pfc.thindesk.dto.ChamadoRequest;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.util.Optional;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChamadoServiceTest {
    private ChamadoRepository repository;
    private ChamadoService service;
    @BeforeEach void configurar(){repository=mock(ChamadoRepository.class);service=new ChamadoService(repository);
        when(repository.save(any(Chamado.class))).thenAnswer(inv->inv.getArgument(0));}
    @Test void criarPreencheDatasEPrioridade() {
        var c=service.criarChamado(new ChamadoRequest("Internet indisponível", "ABERTO", "Rede", "Ana", "Paulo", null, ""));
        assertNotNull(c.getDataAbertura());assertEquals("NORMAL",c.getPrioridade());assertNull(c.getDataConclusao());
        verify(repository).save(c);
    }
    @Test void concluirEReabrirControlaDataConclusao() {
        Chamado existente=new Chamado();existente.setStatus("ABERTO");
        when(repository.findById("123")).thenReturn(Optional.of(existente));
        var pronto=service.atualizarChamado("123",new ChamadoRequest("Corrigido", "CONCLUIDO", "Rede", "Ana", "Paulo", "ALTA", "Resolvido"));
        assertNotNull(pronto.getDataConclusao());assertEquals("Resolvido",pronto.getConsideracoesTecnicas());
        var reaberto=service.atualizarChamado("123",new ChamadoRequest("Retornou", "ABERTO", "Rede", "Ana", "Paulo", "NORMAL", "Reaberto"));
        assertNull(reaberto.getDataConclusao());
    }
    @Test void rejeitarStatusEIdInvalido() {
        assertThrows(IllegalArgumentException.class,()->service.criarChamado(
            new ChamadoRequest("Falha","QUALQUER","Rede","Ana","Paulo",null,null)));
        assertThrows(NoSuchElementException.class,()->service.buscar("ausente"));
    }
}

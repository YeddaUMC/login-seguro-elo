package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {
    private ChamadoRepository repository;
    private DashboardService service;

    @BeforeEach void configurar() {
        repository = mock(ChamadoRepository.class);
        service = new DashboardService(repository);
        LocalDateTime hoje = LocalDateTime.now().withHour(10).withMinute(0);
        Chamado a = chamado("Aberto", "Rede", hoje.minusDays(1), null);
        Chamado b = chamado("CONCLUIDO", "Software", hoje.minusDays(10), hoje.minusDays(9));
        Chamado c = chamado("Concluído", "Software", hoje.minusDays(3), hoje.minusDays(2));
        when(repository.findAll()).thenReturn(List.of(a,b,c));
    }
    private Chamado chamado(String status, String tipo, LocalDateTime abertura, LocalDateTime conclusao) {
        Chamado c = new Chamado();c.setDescricao("Teste do chamado");c.setStatus(status);c.setTipo(tipo);
        c.setTecnico("Ana");c.setDataAbertura(abertura);c.setDataConclusao(conclusao);return c;
    }
    @Test void indicadoresEAtendimentos() {
        var r=service.consultar(null,null,null,null,null,null);
        assertEquals(3,r.indicadores().total());assertEquals(1,r.indicadores().abertos());
        assertEquals(2,r.indicadores().concluidos());assertEquals(66.7,r.indicadores().taxaResolucao());
        assertEquals("Software",r.tipos().getFirst().tipo());assertEquals(2,r.tipos().getFirst().atendidos());
    }
    @Test void filtroTipoStatusEPesquisa() {
        var r=service.consultar(null,null,"CONCLUIDO","Software","Ana","teste");
        assertEquals(2,r.indicadores().total());assertEquals(2,r.indicadores().concluidos());
    }
    @Test void filtroPeriodoExcluiChamadosAnteriores() {
        var inicio=java.time.LocalDate.now().minusDays(5).toString();
        var r=service.consultar(inicio,null,null,null,null,null);
        assertEquals(2,r.indicadores().total());
    }
    @Test void periodoInvalidoRetornaErro() {
        assertThrows(IllegalArgumentException.class,
            ()->service.consultar("2026-11-30","2026-11-01",null,null,null,null));
        assertThrows(IllegalArgumentException.class,
            ()->service.consultar("invalido",null,null,null,null,null));
    }
    @Test void semChamadosNaoDividePorZero() {
        when(repository.findAll()).thenReturn(List.of());
        var r=service.consultar(null,null,null,null,null,null);
        assertEquals(0,r.indicadores().taxaResolucao());
        assertEquals(0,r.indicadores().total());
    }
}

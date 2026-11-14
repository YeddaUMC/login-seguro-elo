package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final ChamadoRepository repository;
    public DashboardService(ChamadoRepository repository) { this.repository = repository; }

    public record Indicadores(long total, long abertos, long emAndamento, long concluidos,
        long cancelados, long pendentes, double taxaResolucao, double mediaHorasResolucao, long semDataAbertura) {}
    public record Situacao(String status, long quantidade, double percentual) {}
    public record TipoAtendimento(String tipo, long atendidos, long total) {}
    public record Evolucao(String periodo, long concluidos) {}
    public record Filtros(List<String> tipos, List<String> tecnicos) {}
    public record Resultado(Indicadores indicadores, List<Situacao> situacoes,
        List<TipoAtendimento> tipos, List<Evolucao> evolucao, List<Chamado> chamados,
        Filtros filtros, String atualizadoEm, String criterioPeriodo) {}

    public Resultado consultar(String inicio, String fim, String status, String tipo, String tecnico, String busca) {
        LocalDate comeco = parseData(inicio);
        LocalDate termino = parseData(fim);
        if (comeco != null && termino != null && comeco.isAfter(termino))
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        if (comeco != null && termino != null && ChronoUnit.DAYS.between(comeco, termino) > 3650)
            throw new IllegalArgumentException("O período máximo de consulta é de dez anos.");
        String statusFiltro = status == null || status.isBlank() ? "" : normalizarStatus(status);
        if (!statusFiltro.isEmpty() && !List.of("ABERTO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO").contains(statusFiltro))
            throw new IllegalArgumentException("Filtro de status inválido.");
        List<Chamado> todos = repository.findAll();
        Filtros filtros = new Filtros(distintos(todos.stream().map(Chamado::getTipo).toList()),
            distintos(todos.stream().map(Chamado::getTecnico).toList()));
        List<Chamado> encontrados = todos.stream().filter(c -> {
            LocalDate aberta = c.getDataAbertura() == null ? null : c.getDataAbertura().toLocalDate();
            if (comeco != null && (aberta == null || aberta.isBefore(comeco))) return false;
            if (termino != null && (aberta == null || aberta.isAfter(termino))) return false;
            if (!statusFiltro.isEmpty() && !statusFiltro.equals(normalizarStatus(c.getStatus()))) return false;
            if (!igualFiltro(c.getTipo(), tipo) || !igualFiltro(c.getTecnico(), tecnico)) return false;
            if (busca != null && !busca.isBlank()) {
                String alvo = normalizar(c.getDescricao()) + " " + normalizar(c.getUsuario()) + " " + normalizar(c.getConsideracoesTecnicas());
                if (!alvo.contains(normalizar(busca))) return false;
            }
            return true;
        }).sorted(Comparator.comparing(Chamado::getDataAtualizacao,
            Comparator.nullsLast(Comparator.reverseOrder()))).toList();
        long total = encontrados.size();
        long aberto = contar(encontrados, "ABERTO");
        long andamento = contar(encontrados, "EM_ANDAMENTO");
        long concluido = contar(encontrados, "CONCLUIDO");
        long cancelado = contar(encontrados, "CANCELADO");
        long semData = encontrados.stream().filter(c -> c.getDataAbertura() == null).count();
        double mediaHoras = encontrados.stream()
            .filter(c -> c.getDataAbertura() != null && c.getDataConclusao() != null)
            .mapToLong(c -> Math.max(0, ChronoUnit.MINUTES.between(c.getDataAbertura(), c.getDataConclusao())))
            .average().orElse(0) / 60.0;
        Indicadores indicadores = new Indicadores(total, aberto, andamento, concluido, cancelado,
            aberto + andamento, percentual(concluido, total), arredondar(mediaHoras), semData);
        List<Situacao> situacoes = List.of(
            new Situacao("ABERTO", aberto, percentual(aberto,total)),
            new Situacao("EM_ANDAMENTO", andamento, percentual(andamento,total)),
            new Situacao("CONCLUIDO", concluido, percentual(concluido,total)),
            new Situacao("CANCELADO", cancelado, percentual(cancelado,total)));
        Map<String,Long> totaisTipos = encontrados.stream().collect(Collectors.groupingBy(
            c -> rotuloTipo(c.getTipo()), Collectors.counting()));
        Map<String,Long> concluidosTipos = encontrados.stream().filter(c -> normalizarStatus(c.getStatus()).equals("CONCLUIDO"))
            .collect(Collectors.groupingBy(c -> rotuloTipo(c.getTipo()), Collectors.counting()));
        List<TipoAtendimento> tipos = totaisTipos.entrySet().stream()
            .map(e -> new TipoAtendimento(e.getKey(), concluidosTipos.getOrDefault(e.getKey(),0L), e.getValue()))
            .sorted(Comparator.comparingLong(TipoAtendimento::atendidos).reversed()
                .thenComparing(Comparator.comparingLong(TipoAtendimento::total).reversed())
                .thenComparing(TipoAtendimento::tipo))
            .limit(8).toList();
        List<Evolucao> evolucao = evolucao(encontrados, comeco, termino);
        return new Resultado(indicadores, situacoes, tipos, evolucao, encontrados.stream().limit(20).toList(),
            filtros, LocalDateTime.now().toString(), "Filtro por data de abertura; gráfico por data de conclusão.");
    }

    private List<Evolucao> evolucao(List<Chamado> chamados, LocalDate inicio, LocalDate fim) {
        YearMonth agora = YearMonth.now();
        YearMonth primeiro = inicio == null ? agora.minusMonths(5) : YearMonth.from(inicio);
        YearMonth ultimo = fim == null ? agora : YearMonth.from(fim);
        if (primeiro.isAfter(ultimo)) return List.of();
        // Evita criar uma série de milhares de barras em períodos históricos longos.
        if (ChronoUnit.MONTHS.between(primeiro, ultimo) > 23) primeiro = ultimo.minusMonths(23);
        Map<YearMonth,Long> contagens = chamados.stream()
            .filter(c -> "CONCLUIDO".equals(normalizarStatus(c.getStatus())) && c.getDataConclusao() != null)
            .collect(Collectors.groupingBy(c -> YearMonth.from(c.getDataConclusao()), Collectors.counting()));
        List<Evolucao> valores = new ArrayList<>();
        for (YearMonth mes = primeiro; !mes.isAfter(ultimo); mes = mes.plusMonths(1))
            valores.add(new Evolucao(mes.toString(), contagens.getOrDefault(mes, 0L)));
        return valores;
    }
    private static LocalDate parseData(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try { return LocalDate.parse(valor); }
        catch (Exception ex) { throw new IllegalArgumentException("Data inválida. Use o formato AAAA-MM-DD."); }
    }
    private static List<String> distintos(List<String> valores) {
        return valores.stream().filter(v -> v != null && !v.isBlank()).distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }
    private static String rotuloTipo(String tipo) { return tipo == null || tipo.isBlank() ? "Não informado" : tipo.strip(); }
    private static boolean igualFiltro(String origem, String filtro) {
        return filtro == null || filtro.isBlank() || normalizar(origem).equals(normalizar(filtro));
    }
    public static String normalizarStatus(String status) {
        String s = normalizar(status).replace(' ', '_').replace('-', '_');
        return switch (s) {
            case "aberto", "aberta", "pendente" -> "ABERTO";
            case "em_andamento", "andamento", "em_atendimento" -> "EM_ANDAMENTO";
            case "concluido", "concluida", "resolvido", "finalizado", "fechado" -> "CONCLUIDO";
            case "cancelado", "cancelada" -> "CANCELADO";
            default -> s.toUpperCase(Locale.ROOT);
        };
    }
    private static String normalizar(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor.strip().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
    }
    private static long contar(List<Chamado> chamados, String status) {
        return chamados.stream().filter(c -> status.equals(normalizarStatus(c.getStatus()))).count();
    }
    private static double percentual(long numerador, long denominador) {
        return denominador == 0 ? 0 : arredondar(numerador * 100.0 / denominador);
    }
    private static double arredondar(double numero) { return Math.round(numero * 10.0) / 10.0; }
}

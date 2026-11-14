# API REST do Thindesk

Base local: `http://localhost:8080` — respostas JSON, autenticação por sessão após login no formulário HTML. Todas as rotas descritas abaixo exigem usuário administrativo. Para requisições com escrita (POST, PUT, DELETE), o Spring Security exige **token CSRF**; as telas enviam automaticamente o token em header indicado nos meta tags Thymeleaf. Sem autenticação, o navegador é redirecionado para `/login`.

## Parâmetros de consulta (GET /api/dashboard e variantes)

| Parâmetro | Tipo | Exemplo | Regra |
|---|---|---|---|
| `inicio` | data AAAA-MM-DD | `2026-09-01` | Inclusivo; abertura a partir da data |
| `fim` | data AAAA-MM-DD | `2026-11-14` | Inclusivo; abertura até a data |
| `status` | enum | `CONCLUIDO` | `ABERTO`, `EM_ANDAMENTO`, `CONCLUIDO`, `CANCELADO` |
| `tipo` | string | `Rede` | Comparação ignorando maiúsculas/acentos |
| `tecnico` | string | `Ana Costa` | Comparação ignorando maiúsculas/acentos |
| `busca` | string | `impressora` | Busca em descrição, usuário e notas técnicas |

Sem parâmetros a API retorna os totais de todos os chamados, incluindo antigos que não têm datas. Um intervalo maior que dez anos é recusado; o gráfico mostra no máximo 24 meses consecutivos. Os exemplos abaixo são **ilustrativos**, não resultados de uma instalação real.

## Endpoints

| Método | Rota | Finalidade |
|---|---|---|
| GET | `/api/dashboard` | Resumo único: indicadores, situações, tipos, evolução, até 20 chamados e opções de filtros |
| GET | `/api/dashboard/indicadores` | Apenas KPIs |
| GET | `/api/dashboard/evolucao` | Série mensal de concluídos |
| GET | `/api/dashboard/situacoes` | Distribuição por situação |
| GET | `/api/dashboard/tipos` | Ranking por tipo |
| GET | `/api/chamados?busca=rede` | Todos os chamados ou filtro por descrição |
| GET | `/api/chamados/{id}` | Detalhar chamado |
| POST | `/api/chamados` | Criar chamado |
| PUT | `/api/chamados/{id}` | Atualizar chamado |
| GET | `/api/clientes` | Listar clientes |
| POST | `/api/clientes` | Cadastrar cliente |
| GET | `/api/ajustes-horarios` | Listar horários |
| POST | `/api/ajustes-horarios` | Cadastrar horário |
| DELETE | `/api/ajustes-horarios/{id}` | Excluir horário |

### Exemplo A — relatório completo

`GET /api/dashboard?inicio=2026-09-01&fim=2026-11-14&status=CONCLUIDO`

Resposta 200 (exemplo simplificado):

```json
{
  "indicadores": {"total": 8, "abertos": 0, "emAndamento": 0, "concluidos": 8, "cancelados": 0, "pendentes": 0, "taxaResolucao": 100.0, "mediaHorasResolucao": 8.5, "semDataAbertura": 0},
  "situacoes": [{"status": "CONCLUIDO", "quantidade": 8, "percentual": 100.0}],
  "tipos": [{"tipo": "Rede", "atendidos": 5, "total": 5}],
  "evolucao": [{"periodo": "2026-11", "concluidos": 4}],
  "chamados": [{"id": "abc123", "descricao": "Rede instável", "status": "CONCLUIDO", "tipo": "Rede", "tecnico": "Ana", "usuario": "João", "consideracoesTecnicas": "Roteador reiniciado", "dataAbertura": "2026-11-01T09:00:00", "dataConclusao": "2026-11-01T11:00:00"}],
  "filtros": {"tipos": ["Rede"], "tecnicos": ["Ana"]},
  "atualizadoEm": "2026-11-14T16:30:00",
  "criterioPeriodo": "Filtro por data de abertura; gráfico por data de conclusão."
}
```

Os quatro endpoints específicos retornam somente a parte correspondente: por exemplo `/api/dashboard/evolucao` produz `{"evolucao":[{"periodo":"2026-11","concluidos":4}]}`.

### Exemplo B — cadastro

`POST /api/chamados` (com sessão e header CSRF válidos)

```json
{
  "descricao": "Computador sem internet",
  "status": "ABERTO",
  "tipo": "Rede",
  "tecnico": "Ana Costa",
  "usuario": "João Souza",
  "prioridade": "ALTA",
  "consideracoesTecnicas": "Equipamento será avaliado."
}
```

Resposta 201: objeto do chamado com `id`, `dataAbertura` e `dataAtualizacao` definidos pelo servidor; `dataConclusao` é nula para chamado aberto. Header `Location: /api/chamados/{id}`.

### Exemplo C — atualização

`PUT /api/chamados/abc123` (com sessão e CSRF, mesmos campos do cadastro)

```json
{
  "descricao": "Computador sem internet", "status": "CONCLUIDO", "tipo": "Rede",
  "tecnico": "Ana Costa", "usuario": "João Souza", "prioridade": "ALTA",
  "consideracoesTecnicas": "Cabo trocado e conexão normalizada."
}
```

Resposta 200: objeto atualizado com `dataConclusao` preenchida. Chamado inexistente retorna 404.

## Possíveis erros

| HTTP | Quando acontece | Resposta / comportamento |
|---|---|---|
| 400 | Data inválida, início posterior ao fim, status inválido, descrição ausente | JSON `{"codigo":400,"mensagem":"...","instante":"..."}` |
| 302 | Usuário sem login | Redirecionamento para `/login` |
| 403 | Sem permissão administrativa ou sem token CSRF válido | Acesso negado pelo Spring Security |
| 404 | ID de chamado não encontrado | JSON `{"codigo":404,"mensagem":"Chamado não encontrado","instante":"..."}` |
| 500 | MongoDB inacessível ou erro inesperado no servidor | Erro de infraestrutura; verificar configuração e logs |

## Autenticação e validação

O projeto base do professor usa autenticação administrativa simples do Spring Security. Para não expor senha no GitHub, o pacote exige `THINDESK_ADMIN_PASSWORD` no ambiente. A variável `THINDESK_ADMIN_USER` define o usuário. O perfil usado é `ADMIN`. Este trabalho **não** reutiliza o banco de usuários do sistema Login Seguro de outra atividade; apenas segue estética simples semelhante.

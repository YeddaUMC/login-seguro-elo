# Requisitos — Thindesk

## Contexto e modelo de domínio

O Thindesk gerencia chamados técnicos e disponibiliza estatísticas por API para um dashboard administrativo. `Chamado` já tinha os campos `id`, `descricao`, `status`, `tipo`, `tecnico` e `usuario`. Foram acrescentados `prioridade`, `consideracoesTecnicas`, `dataAbertura`, `dataConclusao` e `dataAtualizacao`, sem apagar os dados já existentes. As coleções `clientes` e `horariosAtendimento` são mantidas.

## Requisitos funcionais

| Código | Descrição | Implementação |
|---|---|---|
| RF01 | Exibir total, abertos, em andamento, concluídos, pendentes e cancelados | `/api/dashboard/indicadores` e cards |
| RF02 | Visualizar chamados concluídos por mês | `/api/dashboard/evolucao` e barras |
| RF03 | Analisar situação/status dos chamados | `/api/dashboard/situacoes` e barras de distribuição |
| RF04 | Exibir ranking de tipos mais atendidos | `/api/dashboard/tipos`, ordenado por concluídos |
| RF05 | Filtrar por abertura, status, tipo, técnico e busca | Parâmetros da API e formulário na tela |
| RF06 | Integrar painel à API com dados do MongoDB | `DashboardService` + `MongoRepository` + `fetch` |
| RF07 | Atualizar e tratar dados, erros, vazios e datas antigas | Botão salvar, feedback, validações, `ApiErrorHandler` |
| RF08 | Criar e editar chamado com notas técnicas | `POST/PUT /api/chamados`, formulário modal |
| RF09 | Login e acesso administrativo | Spring Security; ROLE_ADMIN |

## Requisitos não funcionais

| Código | Descrição | Verificação proposta |
|---|---|---|
| RNF01 | Compatível com JDK 21 e Maven | `mvn clean test` e `spring-boot:run` |
| RNF02 | Persistência no MongoDB | Inserir, atualizar e reler o chamado |
| RNF03 | API retornando JSON e códigos HTTP adequados | 200, 201, 400, 404; CSRF nos métodos de escrita |
| RNF04 | Interface responsiva em computador e celular | Testes de navegação em 1440 px e 390 px |
| RNF05 | Acesso autenticado e sessão protegida | Login e proteção CSRF |
| RNF06 | Não versionar credenciais nem artefatos compilados | Variáveis de ambiente; `.gitignore` |
| RNF07 | Preservar chamados antigos | Campos opcionais, sem migração destrutiva |
| RNF08 | Clareza visual | Interface neutra, cartões brancos, azul discreto |

## Regras de negócio

1. Status aceitos: `ABERTO`, `EM_ANDAMENTO`, `CONCLUIDO`, `CANCELADO`. Variantes antigas como `Aberto` e `Concluído` são normalizadas nos indicadores.
2. Ao criar chamado, o servidor registra `dataAbertura` e `dataAtualizacao`.
3. Ao concluir, registra-se `dataConclusao`, se ainda não existia; ao reabrir ou cancelar um concluído, essa data é limpa.
4. Concluídos são os chamados com status normalizado `CONCLUIDO`. Taxa = concluídos / total consultado * 100.
5. Tempo médio usa apenas registros com data de abertura e de conclusão, em horas; registros sem datas não entram no cálculo.
6. Filtros de período usam a data de abertura (inclusive as duas datas). Gráfico agrupa por data de conclusão.
7. A carga fictícia só é permitida com variável específica e coleção vazia.
8. Os dados do dashboard são exclusivamente do MongoDB configurado, não do Login Seguro nem de API externa.

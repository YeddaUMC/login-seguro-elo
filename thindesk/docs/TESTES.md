# Testes e evidências

## O que foi executado nesta preparação

A execução foi feita em uma **prévia estática que simula as respostas da API**, usando Chromium com Playwright. Isso valida interface e JavaScript, mas **não comprova** integração Java ↔ MongoDB.

| ID | Teste | Ambiente | Resultado |
|---|---|---|---|
| UI01 | Carregar os indicadores, gráficos, ranking e tabela | Chromium 1440 px, API simulada | PASSOU |
| UI02 | Filtrar por tipo e recalcular KPIs | Chromium 1440 px, API simulada | PASSOU |
| UI03 | Abrir detalhes de chamado existente e ver nota técnica | Chromium 1440 px, API simulada | PASSOU |
| UI04 | Abrir formulário novo e enviar ao endpoint simulado | Chromium 1440 px, API simulada | PASSOU |
| UI05 | Repetir fluxo em resolução móvel 390 px | Chromium 390 px, API simulada | PASSOU |
| UI06 | Carregar páginas de clientes, chamados e horários | Chromium, API simulada | PASSOU |
| JS01 | Verificação sintática dos cinco JavaScript com `node --check` | Node.js | PASSOU |
| JAVA01 | Compilar e executar testes de unidade JUnit | Maven indisponível neste ambiente | **NÃO EXECUTADO** |
| DB01 | Criar/editar chamado no MongoDB real | Sem banco configurado | **NÃO EXECUTADO** |
| SEC01 | Validar login real e token CSRF | Sem servidor Java iniciado | **NÃO EXECUTADO** |

### Capturas

- `docs/evidencias/dashboard-desktop-simulado.png`
- `docs/evidencias/dashboard-mobile-simulado.png`

**Atenção:** são capturas com informações simuladas. Para a entrega definitiva, execute a aplicação, tire capturas **reais** das telas e atualize esta seção. Não identifique capturas simuladas como evidência de integração.

## Checklist para executar no computador e acrescentar resultados

1. No PowerShell, com Java e Maven, execute `./mvnw.cmd clean test` na pasta `thindesk` (Windows: `.\mvnw.cmd clean test`).
2. Registre o resultado final no terminal (por exemplo, testes aprovados) sem expor senha e salve captura em `docs/evidencias/maven-test-real.png`.
3. Configure MongoDB e rode `.\mvnw.cmd spring-boot:run`.
4. Faça login em `/login`; observe redirecionamento a `/dashboard`.
5. Confirme os indicadores com a coleção vazia. Se desejar dados fictícios, reinicie com `THINDESK_DEMO_SEED=true` **apenas em banco de teste vazio**.
6. Cadastre um chamado pela interface. Confira se aparece na tabela e se o total mudou.
7. Atualize para `CONCLUIDO` com consideração técnica; veja o ranking, o status e o gráfico se atualizar.
8. Aplique filtro por técnico, tipo, status e período; valide os números e resultado vazio.
9. Pelo navegador, consulte `/api/dashboard` e `/api/dashboard/indicadores`; salve capturas da resposta JSON.
10. Teste a URL `/api/dashboard?inicio=2026-12-01&fim=2026-11-01`; resultado esperado: HTTP 400.
11. Teste uma página protegida em janela anônima: redireciona para o login.
12. Anote resultados reais, versão do Java/MongoDB e data do teste.

## Fonte da verificação

Os testes de Java em `src/test/java` usam Mockito com repositório simulado, sem depender de MongoDB. Eles verificam indicadores, normalização, filtro de data, validações, criação/conclusão/reabertura. A execução do comando Maven precisa ser comprovada na máquina do aluno.

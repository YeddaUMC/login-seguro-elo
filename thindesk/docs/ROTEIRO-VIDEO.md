# Roteiro para vídeo de até 3 minutos — Thindesk

**0:00–0:20** — Introdução: “Este é o Thindesk, sistema de chamados do professor, ao qual integrei um dashboard administrativo e uma API REST. Mantive o projeto com Java, Spring Boot, Thymeleaf e MongoDB e um layout simples.”

**0:20–0:40** — Login: mostre o formulário; faça login como administrador; explique brevemente que as rotas exigem autenticação.

**0:40–1:20** — Dashboard: aponte total, abertos, em andamento e concluídos; explique o gráfico de conclusões por mês, a distribuição de status e os tipos mais atendidos. Mostre os filtros por data, tipo, técnico e situação.

**1:20–2:05** — Operação: clique em “Novo chamado”, preencha e salve. Depois abra “Detalhes”, altere status para “Concluído”, adicione considerações técnicas e salve. Volte aos indicadores.

**2:05–2:30** — API: no navegador, entre em `/api/dashboard` já autenticado e mostre o JSON; explique que a API reúne dados reais do MongoDB. Se possível, mostre `/api/dashboard/indicadores`.

**2:30–2:50** — Decisões: mesma stack do professor, MongoRepository, autenticação, CSRF habilitado, datas e tratamento de erros; documentação e testes organizados no repositório.

**2:50–3:00** — Encerramento: “O dashboard auxilia a análise de desempenho e as decisões sobre os atendimentos técnicos.”

> Grave com o sistema realmente rodando. Não apresente a captura simulada como um teste do MongoDB. Depois salve como `docs/VIDEO-THINDESK.mp4` e envie somente o link do GitHub no Teams.

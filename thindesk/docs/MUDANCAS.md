# Guia rápido: o que mudou em relação ao ZIP do professor

O projeto recebido não trazia dashboard administrativo e possuía um `Chamado` com apenas seis campos principais. Esta entrega preserva o nome do projeto, os repositories e a stack Spring/MongoDB, alterando as seguintes partes:

- `pom.xml`: passa a usar o parent do Spring Boot e compilar Java 21, adicionando suporte a testes e validação.
- `application.properties`: remove usuário/senha do MongoDB e senha fixa de login; recebe variáveis de ambiente.
- `Chamado.java`: mantém campos originais e acrescenta prioridade, notas e três datas.
- `ChamadoService.java` / `ChamadoController.java`: passam a validar, registrar datas e retornar os códigos REST adequados.
- `DashboardService.java` / `DashboardController.java`: novos, agregam indicadores, séries, ranking e filtros.
- `PageController.java`: rotas das páginas protegidas.
- `templates/`: login e páginas administrativas com tema simples; não reutiliza a tela antiga de sidebar, que tinha links inativos.
- `static/css/ui.css` e `static/js/`: nova apresentação, gráficos, filtros e formulários conectados à API.
- `DemoDataConfig.java`: dados fictícios apenas por opção explícita.
- `src/test/java/`: testes JUnit/Mockito adicionados para os cálculos e atualizações.

**Credenciais:** a URI do Atlas enviada no arquivo `application.properties` do professor foi retirada do pacote, inclusive dos artefatos antigos da pasta `target/`. Se for uma credencial real, o responsável pelo banco deverá rotacionar ou revogar o acesso anterior. Não publicar a versão original do arquivo no GitHub.

**Compatibilidade:** chamados antigos continuam consultáveis sem migração; por não terem datas, não entram em filtros de período nem gráficos de conclusão. Caso se deseje relatórios históricos completos, será necessário preencher datas verificadas a partir da fonte original, sem inventar registros.

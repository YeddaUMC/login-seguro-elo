# THINDESK — versão integrada ao projeto fornecido em aula

Aplicação de chamados técnicos em **Java 21, Spring Boot 3.4.0, Thymeleaf, Spring Security e MongoDB**, ampliada com dashboard administrativo, API REST e considerações técnicas. **Base:** ZIP `thindesk(1).zip` fornecido pelo professor. O projeto de Login Seguro anterior foi utilizado somente como referência de visual simples; a autenticação deste pacote continua sendo a autenticação administrativa do projeto Thindesk, e **não** o módulo MongoDB de usuários do Login Seguro.

## O que foi implementado

- Dashboard em `/dashboard`: indicadores, status, gráfico de concluídos por mês, ranking dos tipos, últimos chamados e filtros.
- API `/api/dashboard` e endpoints de indicadores, evolução, situações e tipos; dados obtidos da coleção `chamados` no MongoDB.
- Criação, listagem, detalhamento e atualização de chamados por API e interface, incluindo prioridade e considerações técnicas.
- Páginas simples e responsivas de chamados, clientes, horários e login.
- Proteção com login e perfil `ADMIN`, sessão com CSRF habilitado para POST, PUT e DELETE.
- Carga de 72 chamados **fictícios** opcional e desativada por padrão.
- Testes Java escritos e testes de interface executados em navegador com API simulada.

## Como executar no Windows (VS Code ou PowerShell)

**Pré-requisitos:** Java JDK 21 ou superior, Maven (ou o wrapper `mvnw.cmd`), MongoDB local ou cluster MongoDB Atlas autorizado.

1. Extraia o ZIP e abra a **pasta `thindesk`** no VS Code.
2. Abra o PowerShell na pasta `thindesk` (onde existe `pom.xml`).
3. Defina as variáveis (troque `SENHA-FORTE-AQUI` por sua senha local de administrador; não use a senha original do ZIP do professor):

```powershell
$env:THINDESK_ADMIN_USER = "admin"
$env:THINDESK_ADMIN_PASSWORD = "SENHA-FORTE-AQUI"
$env:MONGODB_URI = "mongodb://localhost:27017/thindesk"
$env:MONGODB_DATABASE = "thindesk"
$env:THINDESK_DEMO_SEED = "true"
```

Se usar MongoDB Atlas, substitua `MONGODB_URI` pela **nova** URL de conexão válida, criada/rotacionada no Atlas, sem colocá-la no GitHub. O usuário precisa de permissão de leitura/escrita. A aplicação usa o banco `thindesk`.

4. Execute (a partir da pasta `thindesk`):

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Caso `mvnw.cmd` não consiga baixar o Maven, instale o Maven e use `mvn clean test` e `mvn spring-boot:run`.

5. Abra **http://localhost:8080/login**, entre com usuário `admin` e a senha escolhida. O dashboard abre automaticamente. O seed é executado **apenas se o banco não tiver nenhum chamado** e se `THINDESK_DEMO_SEED=true`. Depois da primeira carga, pode definir `false`.

> Não envie o arquivo `.env`, senhas, URI real do Atlas nem a pasta `target/` ao GitHub. O arquivo `.env.example` usa apenas valores fictícios. Se a credencial que veio no ZIP do professor era real, ela deve ser revogada/trocada no MongoDB Atlas.

## Onde está cada item da entrega

| Entregável | Local |
|---|---|
| Código e endpoints | `src/main/java/com/pfc/thindesk/` |
| Dashboard e JS/CSS | `src/main/resources/templates/dashboard.html` e `static/` |
| Requisitos funcionais e não funcionais | `docs/REQUISITOS.md` |
| Endpoints, exemplos, erros e autenticação | `docs/API.md` |
| Evidências e roteiro de testes | `docs/TESTES.md` e `docs/evidencias/` |
| Documento final em Word/PDF | `docs/RELATORIO-THINDESK.docx` / `.pdf` |
| Roteiro de vídeo (até 3 minutos) | `docs/ROTEIRO-VIDEO.md` |

## Gitflow e publicação sem apagar o Login Seguro

É mais seguro preservar o trabalho de Login Seguro e **adicionar esta pasta `thindesk/` como subprojeto** ao repositório exigido. O ZIP entregue nesta conversa contém a pasta `thindesk/` pronta para isso.

No clone local de `https://github.com/YeddaUMC/login-seguro-elo`, acesse a branch `develop` (se não existir, crie-a primeiro com `git switch -c develop` a partir da branch principal). Depois crie a branch de funcionalidade:

```bash
git switch develop
git switch -c feature/api-dashboard-chamados
```

Copie **a pasta `thindesk` inteira** para a raiz do repositório (sem substituir o projeto de Login Seguro). Depois:

```bash
git add thindesk
git commit -m "feat: dashboard administrativo e API REST Thindesk"
git push -u origin feature/api-dashboard-chamados
```

Coloque `VIDEO-THINDESK.mp4` em `thindesk/docs/` após gravar a demonstração real e faça commit/push. No Teams envie somente o link do repositório/branch. Não é necessário gerar nada em `start.spring.io`, pois o projeto já tem `pom.xml` e wrapper.

## Limitações e decisões

- O gráfico conta **conclusões por mês**, não simplesmente chamados criados.
- A consulta por período usa `dataAbertura`; registros legados sem essa data aparecem na visão sem filtro de datas, mas não em períodos específicos.
- Ranking considera a quantidade de chamados com situação `CONCLUIDO` por tipo.
- O gráfico é limitado aos últimos 24 meses exibidos quando o período escolhido é grande (para manter a interface legível).
- A API usa `MongoRepository.findAll()` + filtros em memória, solução adequada ao escopo didático. Para grande volume em produção, recomenda-se filtros e agregações diretamente no MongoDB, índices e paginação.
- O projeto contém testes unitários Java **a executar no seu computador**. Capturas de tela são de testes de interface com **API simulada**, não de MongoDB real.

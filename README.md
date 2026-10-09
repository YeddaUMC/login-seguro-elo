# Login Seguro - ELO

Projeto desenvolvido para a atividade de Login Seguro usando Java, Spring Boot, Spring Security, Thymeleaf e MongoDB Atlas.

O sistema possui cadastro, login, logout, senha criptografada e controle de acesso por perfil.

## Perfis

- ADMIN
- GESTOR
- USUARIO

## Tecnologias usadas

- Java 21
- Spring Boot
- Spring Security
- Thymeleaf
- MongoDB Atlas
- Maven

## Configuração do MongoDB Atlas

Para conectar o projeto ao MongoDB Atlas, é necessário criar um cluster, um usuário do banco e copiar a string de conexão.

No Windows PowerShell, antes de executar o projeto, configure:

```powershell
$env:MONGODB_URI="mongodb+srv://SEU_USUARIO:SUA_SENHA@SEU_CLUSTER.mongodb.net/?retryWrites=true&w=majority"
$env:MONGODB_DATABASE="login_seguro_elo"
$env:ADMIN_NAME="Administrador"
$env:ADMIN_EMAIL="admin@exemplo.com"
$env:ADMIN_PASSWORD="sua-senha"
```

O arquivo `.env.example` mostra as variáveis usadas no projeto sem expor dados reais.

## Como executar

Na pasta do projeto:

```bash
mvn spring-boot:run
```

Depois acesse:

```text
http://localhost:8080
```

Os usuários são armazenados no MongoDB e as senhas são salvas com BCrypt. As sessões também utilizam o MongoDB por meio do Spring Session.

# finance-api — Banco Sentry (backend)

API REST de gestão financeira feita com **Quarkus 3** + **PostgreSQL**.
O front-end (Angular) está no repositório **finance-ui**.

## O que tem

| Recurso | Endpoints (prefixo `/api`) |
|---|---|
| Autenticação | `POST /auth/registrar`, `POST /auth/login`, `POST /auth/logout`, `GET /auth/me` |
| Contas | `GET/POST /conta`, `GET/PUT/DELETE /conta/{id}` |
| Categorias | `GET/POST /categoria`, `GET/PUT/DELETE /categoria/{id}` |
| Transações | `GET /transacao?contaId=&categoriaId=` (filtros opcionais), `POST`, `PUT/DELETE /transacao/{id}` |
| Transferência | `POST /transferencia` (gera uma despesa na origem e uma receita no destino) |
| Saldo | `GET /saldo` (saldo de cada conta e saldo geral) |

Cada usuário só enxerga as próprias contas, categorias e transações.

## Autenticação (JWT em cookie httpOnly)

1. No login, a senha é conferida com o hash **BCrypt** guardado no banco (a senha em texto nunca é salva).
2. A API gera um **JWT assinado (RS256)** com o id do usuário e o devolve num cookie
   `finance_token` com **HttpOnly** (o JavaScript não lê o token → protege contra XSS) e
   **SameSite=Strict** (o navegador não manda o cookie em requisições de outros sites → protege contra CSRF).
3. Em cada requisição o navegador envia o cookie sozinho; o Quarkus (smallrye-jwt) valida a assinatura e a
   validade. Endpoints com `@Authenticated` respondem **401** sem um token válido.
4. O logout devolve o cookie vazio com validade zero, e o navegador o apaga.

Arquivos principais: `controller/AuthController.java`, `security/TokenService.java`,
`security/UsuarioLogado.java` e a seção de autenticação do `application.properties`.

## Estrutura

```
src/main/java/
  controller/   endpoints REST (um por recurso)
  entity/       entidades JPA/Panache (Usuario, Conta, Categoria, Transacao)
  dto/          objetos de entrada/saída que não são entidades (login, transferência, saldo)
  security/     geração do token/cookie e usuário logado
  erro/         erro de regra de negócio -> JSON {"mensagem": "..."}
database/       script SQL do banco
```

## Como executar

**Pré-requisitos:** JDK 17+, Docker Desktop (para o PostgreSQL). Não precisa instalar o Maven: o projeto usa o `mvnw`.

```bash
# 1. Subir o PostgreSQL (cria o banco "financeiro" e as tabelas na primeira vez)
docker compose up -d

# 2. Rodar a API em modo dev (http://localhost:8080)
./mvnw quarkus:dev          # Windows (cmd/PowerShell): mvnw quarkus:dev
```

Sem Docker: instale o PostgreSQL, crie os bancos `financeiro` e `financeiro_teste`
(usuário/senha `postgres`/`postgres`) e rode `database/financeiro.sql` no `financeiro`.

Se o `java -version` mostrar Java 8, aponte o `JAVA_HOME` para o JDK 17 antes do `mvnw`.

### Testes

```bash
./mvnw test
```

Os testes (`src/test/java/controller/FluxoFinanceiroTest.java`) usam o banco `financeiro_teste`
do mesmo PostgreSQL e cobrem: acesso sem login (401), senha errada, fluxo completo
(contas → categorias → transações → filtros → transferência → saldo) e isolamento entre usuários.

### Compilar o pacote

```bash
./mvnw package
```

O resultado fica em `target/quarkus-app/`. As chaves do JWT são geradas automaticamente em modo dev e teste.
Para rodar o pacote em produção (`java -jar target/quarkus-app/quarkus-run.jar`), é preciso informar um par de chaves RSA
(`mp.jwt.verify.publickey.location` e `smallrye.jwt.sign.key.location`).

### Zerar o banco

```bash
docker compose down -v && docker compose up -d
```

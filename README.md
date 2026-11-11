# QuadraFácil — entrega inicial

Projeto da disciplina Oficina Prática Backend I (UNIESP), de Daniel Lucas, João Nobrega e Dante Cavalcante.

Esta pasta é uma entrega independente da primeira etapa do projeto. Não é necessário entregar o sistema completo da ideia original.

## Itens da entrega

| Item solicitado | Onde encontrar |
|---|---|
| 1. Grupo + problema | `docs/01-proposta.md`, seção 1 |
| 2. Uma persona | `docs/01-proposta.md`, seção 2 |
| 3. Três histórias priorizadas | `docs/01-proposta.md`, seção 3 |
| 4. MVP definido | `docs/01-proposta.md`, seção 4 |
| 5. Modelo inicial | `docs/02-modelo-inicial.md` e classes em `src/main/java/br/com/quadrafacil/modelo` |
| 6. Spring Boot rodando | Aplicação executável e endpoints abaixo |
| 7. README | Este arquivo |

## Executar

Requisitos: Java 21 e Maven 3.9 ou superior.

Abra o terminal nesta pasta e execute:

```sh
mvn spring-boot:run
```

Abra http://localhost:8080. A página inicial informa o nome e a etapa do projeto.

Endpoints:

- `GET /` — apresentação simples do serviço.
- `GET /api/status` — confirma que a aplicação está em execução.
- `GET /api/quadras` — retorna uma quadra ilustrativa, sem persistência.

Para usar outra porta: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.

Para verificar e gerar um executável:

```sh
mvn test
mvn package
java -jar target/quadrafacil-inicial-0.1.0.jar
```

## Escopo desta etapa

**Objetivo do MVP:** cadastrar clientes e quadras e reservar um horário sem conflitos. A regra central é que uma mesma quadra não pode ter duas reservas confirmadas com horários sobrepostos.

**O que está implementado hoje:** o serviço Spring Boot, o modelo inicial em Java (records), a documentação, `GET /api/status` e `GET /api/quadras`. A resposta de quadras é apenas um exemplo fixo.

**O que está planejado:** cadastro e consulta de clientes e quadras, funcionamento, criação e cancelamento de reservas, consulta de agenda e testes automatizados. O desenvolvimento é acompanhado pelas issues B01 a B16 (#5 a #20).

As issues #1 a #4 estão fechadas porque o planejamento foi concluído. Elas registram as histórias e a definição do MVP, **não funcionalidades implementadas**.

**Tecnologias desta etapa:** Java 21, Spring Boot, Maven, Spring Web, Spring Data JPA, Bean Validation, H2 e JUnit. O H2 é um banco em memória usado **apenas com finalidade didática**; os dados não são mantidos ao reiniciar. A API será testada com Bruno, Postman ou curl.

**Fora desta etapa:** login e perfis de acesso, pagamento online, frontend completo, integrações externas, Docker e microsserviços. A ideia original também previa PostgreSQL/Supabase, código de acesso por reserva e publicação na Vercel/Railway; esses itens são **requisitos futuros** e estão listados em `docs/01-proposta.md`, seção 6.

## Estrutura

```text
docs/                       proposta, modelo e evidências
src/main/java/              aplicação, controller e modelo inicial
src/main/resources/         configuração e página inicial
src/test/java/              teste HTTP de inicialização
pom.xml                     dependências e build
```

# QuadraFácil — entrega inicial

Esta pasta é uma entrega independente da primeira etapa do projeto. Não é necessário entregar o sistema completo que está na pasta superior.

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

O serviço Spring Boot, o modelo inicial em Java e a documentação estão implementados. A resposta de quadras é apenas um exemplo fixo para demonstrar o endpoint. Ainda não há cadastro, login, criação de reservas, banco de dados, pagamento ou código de acesso nesta entrega.

O MVP descrito na proposta é o objetivo das próximas etapas, não uma afirmação de funcionalidades prontas. A evolução prevista usa PostgreSQL/Supabase e frontend HTML, CSS e JavaScript. A regra contra sobreposição deverá ser garantida por transações e restrições no banco quando a persistência for implementada.

## Estrutura

```text
docs/                       proposta, modelo e evidências
src/main/java/              aplicação, controller e modelo inicial
src/main/resources/         configuração e página inicial
src/test/java/              teste HTTP de inicialização
pom.xml                     dependências e build
```

```


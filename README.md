# QuadraFácil

API REST para cadastrar quadras esportivas e reservar horários sem conflitos. Projeto acadêmico da disciplina Oficina Prática Backend I (UNIESP), de Daniel Lucas, João Nobrega e Dante Cavalcante.

## Executar

Com JDK 21 e Maven 3.9 ou superior, rode na raiz do repositório:

```sh
mvn spring-boot:run
```

A aplicação sobe em [localhost:8080](http://localhost:8080/). Para mudar a porta, use `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.

```sh
mvn test
mvn verify
java -jar target/quadrafacil-inicial-0.1.0.jar
```

`mvn test` usa o banco H2 em memória `quadrafacil-testes`, isolado do banco usado ao executar a aplicação. `mvn verify` também gera o JAR executável.

## Endpoints

| Método | Caminho | Resultado |
|---|---|---|
| GET | `/` e `/api/status` | Apresentação e status |
| POST, GET | `/api/clientes` | Cadastra ou lista clientes |
| GET | `/api/clientes/{id}` | Consulta cliente |
| POST, GET | `/api/quadras` | Cadastra ou lista quadras |
| GET, PUT | `/api/quadras/{id}` | Consulta ou edita quadra |
| PATCH | `/api/quadras/{id}/situacao` | Ativa ou desativa quadra |
| GET, PUT | `/api/quadras/{id}/funcionamento` | Consulta ou substitui o funcionamento semanal |
| POST | `/api/reservas` | Calcula o valor e confirma a reserva |
| GET | `/api/reservas/{id}` | Consulta a reserva, inclusive cancelada |
| POST | `/api/reservas/{id}/cancelamento` | Cancela e libera o horário |
| GET | `/api/quadras/{id}/agenda?data=AAAA-MM-DD` | Lista reservas confirmadas do dia |

Envie início e fim em ISO 8601 com offset. A API retorna os instantes em UTC e interpreta o funcionamento e a data da agenda em `America/Sao_Paulo`. O [contrato HTTP](docs/03-contratos-http.md) detalha entradas, respostas e erros.

## Demonstrar o MVP

Importe [QuadraFacil.postman_collection.json](postman/QuadraFacil.postman_collection.json) no Postman e execute a coleção inteira em ordem pelo Collection Runner. A variável `baseUrl` aponta para `http://localhost:8080`. A coleção cria dados fictícios, escolhe uma data futura e reutiliza os IDs retornados pela API.

O [roteiro da demonstração](docs/05-demonstracao.md) apresenta a sequência e as falas da equipe. O [registro de contribuições](docs/06-contribuicoes.md) reúne as evidências da entrega.

## Escopo acadêmico

O H2 em memória começa vazio. A aplicação grava dados durante a execução e os perde ao reiniciar. O console local está em `/h2-console` e usa a configuração de desenvolvimento em `src/main/resources/application.yml`. Execute apenas em ambiente local com dados fictícios.

O MVP tem quatro entidades: cliente, quadra, reserva e horário de funcionamento. Um dia aceita uma janela de funcionamento, sem atravessar a meia-noite. O cancelamento preserva o histórico e só é permitido antes do início da reserva. Login, controle de acesso por usuário, pagamentos e armazenamento durável estão fora do escopo.

- [Proposta, persona e histórias](docs/01-proposta.md)
- [Modelo de dados](docs/02-modelo-inicial.md)
- [Guia das camadas do código](docs/04-guia-do-codigo.md)

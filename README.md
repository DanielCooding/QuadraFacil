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
| 5. Modelo inicial | `docs/02-modelo-inicial.md` e entidades nos pacotes `cliente`, `quadra`, `reserva` e `funcionamento` |
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
- `GET /api/quadras` — consulta as quadras cadastradas no H2, incluindo inativas.

Para usar outra porta: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.

Para verificar e gerar um executável:

```sh
mvn test
mvn package
java -jar target/quadrafacil-inicial-0.1.0.jar
```

## Escopo desta etapa

**Objetivo do MVP:** cadastrar clientes e quadras e reservar um horário sem conflitos. A regra central é que uma mesma quadra não pode ter duas reservas confirmadas com horários sobrepostos.

**O que está implementado hoje:** o serviço Spring Boot, quatro entidades JPA, repositories, DTOs com Bean Validation e H2 configurado. `GET /api/status` continua funcionando e os endpoints de quadras já usam o H2 para cadastro, consulta, edição e alteração de situação. Clientes já possuem cadastro e consultas. Reservas e funcionamento ainda serão implementados.

**Próximas etapas:** funcionamento, criação e cancelamento de reservas, consulta de agenda e ampliação dos testes automatizados. O desenvolvimento é acompanhado pelas issues B01 a B16 (#5 a #20).

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

## Base preparada em B02 e contratos de B03

Os contratos completos estão em [docs/03-contratos-http.md](docs/03-contratos-http.md). Eles distinguem o que funciona hoje dos endpoints planejados. O modelo atualizado está em [docs/02-modelo-inicial.md](docs/02-modelo-inicial.md).

```text
br/com/quadrafacil/
  apresentacao/controller/       status da aplicação
  cliente/                      entity, repository, dto, service, controller
  quadra/                       entity, repository, dto, service, controller
  reserva/                      entity, repository, dto, service, controller
  funcionamento/                quarta entidade e configuração semanal
```

Os pacotes ainda sem implementação de fluxo têm `package-info.java` explicando a responsabilidade. Não criamos Controllers ou Services vazios nem endpoints que finjam salvar dados. Quadra já possui Controller, Service transacional e Repository; entidades JPA não são retornadas diretamente pela API.

### Banco didático

A URL local é `jdbc:h2:mem:quadrafacil`, usuário `sa` e senha vazia apenas para esse banco em memória. Nenhum serviço externo ou arquivo `.env` é necessário. `create-drop` cria o schema na inicialização e o remove no encerramento. Os dados não sobrevivem ao reinício; `DB_CLOSE_DELAY=-1` só mantém o banco vivo entre conexões da mesma execução. O console H2 está desabilitado.

Os testes usam outro banco, `quadrafacil-testes`, e as transações do teste JPA são revertidas. Execute `mvn verify` para compilar, testar e gerar o JAR. A suíte verifica status HTTP, fluxo HTTP de quadras, validação dos DTOs e gravação/leitura dos relacionamentos no H2. Ela ainda não comprova regras de reservas, que serão implementadas e testadas em B09–B13.

`target/`, arquivos de IDE, bancos locais, logs e segredos estão no `.gitignore`. Maven 3.9+ deve estar instalado; o projeto ainda não fornece Maven Wrapper.

## Quadras — B04 e B05

O banco começa vazio. A lista fixa foi removida e `proprietarioId` não faz mais parte da resposta, conforme B03.

| Método | Caminho | Resultado |
|---|---|---|
| POST | `/api/quadras` | 201 e Location da quadra criada |
| GET | `/api/quadras` | 200, lista do banco, inclusive inativas |
| GET | `/api/quadras/{id}` | 200 ou 404 |
| PUT | `/api/quadras/{id}` | 200, atualiza nome/modalidade/preço sem alterar situação |
| PATCH | `/api/quadras/{id}/situacao` | 200, desativa ou reativa sem excluir dados |

No PowerShell, com a aplicação rodando:

```powershell
$quadra = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/quadras -ContentType 'application/json' -Body '{"nome":"Central","modalidade":"Futebol","precoHora":120.00}'
Invoke-RestMethod http://localhost:8080/api/quadras
Invoke-RestMethod -Method Put -Uri "http://localhost:8080/api/quadras/$($quadra.id)" -ContentType 'application/json' -Body '{"nome":"Central Coberta","modalidade":"Futebol","precoHora":150.00}'
Invoke-RestMethod -Method Patch -Uri "http://localhost:8080/api/quadras/$($quadra.id)/situacao" -ContentType 'application/json' -Body '{"ativa":false}'
```

Nome e modalidade são obrigatórios e têm espaços externos removidos. Preço deve ser positivo e ter até duas casas. Campos extras são rejeitados. Erros usam o tratamento global de B07: JSON com `status`, `mensagem` e `campos`.

Desativar não apaga reservas nem altera seus valores. A rejeição de novas reservas em quadras inativas será implementada/testada com o endpoint de reservas em B09; ele ainda não existe.

## Clientes e erros — B06 e B07

| Método | Caminho | Resultado |
|---|---|---|
| POST | `/api/clientes` | 201, cliente salvo e header Location |
| GET | `/api/clientes` | 200, lista ordenada por nome e ID |
| GET | `/api/clientes/{id}` | 200 ou 404 |

```powershell
$cliente = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/clientes -ContentType 'application/json' -Body '{"nome":"Joao","email":"joao@example.com"}'
Invoke-RestMethod "http://localhost:8080/api/clientes/$($cliente.id)"
```

Nome e e-mail são obrigatórios, com limites de 100 e 254 caracteres. Removemos espaços nas extremidades antes de validar. E-mail precisa ter formato válido, mas não é único nem representa uma conta de login.

`compartilhado/erro/ApiExceptionHandler` atende todos os controllers: 400 para entrada inválida, 404 para ausência, 409 para conflito de negócio/integridade e 500 com mensagem genérica para falha inesperada. Erros HTTP como 405 e 415 preservam seus códigos. Nenhuma resposta inclui stack trace, SQL ou o valor rejeitado. As mensagens de `ResponseStatusException` devem ser constantes controladas pelo Service, nunca mensagens de exceções internas.

DTOs usam Bean Validation e mensagens em português. O corpo segue o contrato `status`, `mensagem`, `campos`. Novos módulos devem usar esse handler, sem duplicar tratamentos locais. Os testes de conflito e falha inesperada usam controllers exclusivos de teste; não existem rotas artificiais de erro no JAR de produção. A prevenção de sobreposição continua em B10.

# Como explicar o código

O projeto usa Java 21, Maven e Spring Boot. A organização segue o caminho de uma requisição:

1. O Controller recebe a requisição HTTP, por exemplo `GET /api/clientes/1`.
2. O Service executa a operação e verifica se o registro existe.
3. O Repository consulta ou salva os dados no banco H2.
4. A Entity representa a tabela no banco.
5. O DTO define os campos recebidos ou devolvidos pela API.

## IDs numéricos

Todas as entidades usam `Long` para o ID e `@GeneratedValue(strategy = GenerationType.IDENTITY)`. O banco gera o número ao salvar: o cliente não precisa enviar um ID no cadastro. Os números podem ter intervalos; não são contadores de registros.

Antes de salvar, o ID pode ser `null`, por isso usamos `Long` em vez do tipo primitivo `long`. Nas respostas JSON, o ID é um número, por exemplo `{"id":1,"nome":"Daniel","email":"daniel@example.com"}`.

Essa troca altera o contrato antigo de IDs UUID: chamadas e dados de exemplo devem usar os IDs numéricos retornados pelo cadastro. O banco H2 atual é recriado ao iniciar a aplicação; não foi feita migração de dados persistentes.

## Listas e DTOs

Os Services usam um `for` para percorrer as entidades e montar uma lista de respostas. `ClienteResponse.de(cliente)` copia os campos que podem aparecer na resposta.

Os DTOs são `record`: uma forma curta do Java de declarar um conjunto de dados. O Java cria o construtor e os métodos de leitura, como `nome()`. As entidades são classes comuns porque representam registros gerenciados pelo JPA.

## Anotações principais

- `@RestController`: recebe chamadas HTTP e devolve respostas JSON.
- `@Service`: identifica a classe que executa as operações.
- `@Entity`: associa uma classe a uma tabela.
- `@Id`: indica a chave primária.
- `@Valid`: pede que o Spring valide o DTO recebido.
- `@NotBlank`: exige um texto preenchido.
- `@Transactional`: agrupa a operação de banco em uma transação.

O `ApiExceptionHandler` centraliza as respostas de erro. Mantemos essa classe para que os Controllers não repitam o mesmo tratamento.

Dinheiro continua como `BigDecimal`, para evitar erros de arredondamento de `double`. Datas de reservas continuam como `Instant`, para representar o horário sem ambiguidades de fuso.

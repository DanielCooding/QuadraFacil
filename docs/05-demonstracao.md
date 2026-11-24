# Demonstração do QuadraFácil

## Preparação

Use um clone limpo do PR, JDK 21, Maven 3.9+ e o Postman. Rode `mvn verify`, depois `mvn spring-boot:run`. Importe a [coleção](../postman/QuadraFacil.postman_collection.json) e execute pelo Collection Runner. A coleção escolhe uma data futura em Brasília e guarda os IDs retornados pelos cadastros, sem depender de dados anteriores.

## Roteiro sugerido

| Integrante | Fala e ação | Evidência esperada |
|---|---|---|
| Daniel Lucas | Explicar o problema das reservas duplicadas e apresentar as histórias de [consulta](https://github.com/DanielCooding/QuadraFacil/issues/1), [reserva sem conflito](https://github.com/DanielCooding/QuadraFacil/issues/2) e [agenda diária](https://github.com/DanielCooding/QuadraFacil/issues/3). Cadastrar Marina e a Quadra Demo, consultar a cliente e configurar o funcionamento das 08h às 22h. | Cadastros com 201 e consulta com os dados gravados. Agenda inicial vazia. |
| João Nobrega | Reservar das 18h às 19h30 a R$ 120 por hora. Consultar a reserva pelo ID retornado e explicar o caminho Controller → Service → Repository → H2. Tentar reservar das 19h às 20h na mesma quadra. | 201, valor de R$ 180 e estado `CONFIRMADA`. Consulta posterior retorna a reserva do H2. A tentativa sobreposta devolve 409 e a agenda segue com uma reserva. |
| Dante Cavalcante | Cancelar a reserva, consultar a agenda, repetir o pedido original e consultar o histórico da primeira reserva. Explicar a duração dos dados no H2. | A primeira reserva permanece `CANCELADA`. A agenda fica livre e o mesmo intervalo aceita uma nova reserva com outro ID. |

## Pendências da entrega acadêmica

- Um segundo integrante deve executar o roteiro a partir de um clone limpo e registrar nome, commit, comando e resultado no PR.
- A equipe deve confirmar a divisão das falas e registrar a revisão antes da integração à `main`.
- A gravação da apresentação ou as capturas da execução em sala podem ser anexadas ao PR quando realizadas. A coleção contém as verificações reproduzíveis.

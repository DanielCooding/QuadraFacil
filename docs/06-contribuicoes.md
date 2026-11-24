# Contribuições e evidências do QuadraFácil

## Base de referência

O commit [900f74a](https://github.com/DanielCooding/QuadraFacil/commit/900f74a) é a base recebida pelo backlog. Já incluía a proposta, o modelo inicial em records e endpoints ilustrativos. O trabalho posterior está vinculado a entregas específicas. As issues [#1 a #4](https://github.com/DanielCooding/QuadraFacil/issues?q=is%3Aissue%20sort%3Acreated-asc) são registros de planejamento.

## Participação que consta no repositório

| Integrante | Entregas e registros | Revisões registradas |
|---|---|---|
| Daniel Lucas (`DanielCooding`) | B02–B03 no [PR #22](https://github.com/DanielCooding/QuadraFacil/pull/22), B04–B05 no [PR #23](https://github.com/DanielCooding/QuadraFacil/pull/23), B06–B07 no [PR #24](https://github.com/DanielCooding/QuadraFacil/pull/24), organização por camadas e IDs nos PRs [#25](https://github.com/DanielCooding/QuadraFacil/pull/25) e [#26](https://github.com/DanielCooding/QuadraFacil/pull/26), e B08 no [PR #27](https://github.com/DanielCooding/QuadraFacil/pull/27). O PR #24 registra auxílio do Codex e o PR #27 registra implementação pelo Codex a pedido de Daniel. A entrega B09–B16 também foi preparada pelo Codex a pedido de Daniel. | O PR #24 registra conferência local do diff e testes. A revisão humana de B09–B16 está pendente. |
| João Nobrega | B01 tem como evidência a documentação do recorte acadêmico no [PR #21](https://github.com/DanielCooding/QuadraFacil/pull/21), publicado pela conta `joaognfreire`. O roteiro prevê a explicação de reserva, persistência e conflito. | Sem submissão de revisão localizada nos PRs #21–#27. |
| Dante Cavalcante | O roteiro prevê a explicação de cancelamento e reutilização do intervalo. O histórico consultado não oferece evidência para atribuir issue ou PR implementado. | Sem submissão de revisão localizada nos PRs #21–#27. |

As falas são propostas para a apresentação. A atribuição usa o conteúdo dos PRs e o histórico de autoria disponível.

## Aceite e evidências de B09–B16

| Issue | Evidência verificável |
|---|---|
| [B09 / #13](https://github.com/DanielCooding/QuadraFacil/issues/13) | A API retorna 201, persiste a reserva e conserva o valor após atualização do preço da quadra. A suíte testa validação, entidades ausentes e quadra inativa. |
| [B10 / #14](https://github.com/DanielCooding/QuadraFacil/issues/14) | A criação bloqueia a quadra no H2 e testa sobreposição parcial e total, horários consecutivos, quadras diferentes e duas tentativas simultâneas. |
| [B11 / #15](https://github.com/DanielCooding/QuadraFacil/issues/15) | Testes verificam cancelamento repetido, histórico, preservação das demais reservas e reutilização do intervalo. |
| [B12 / #16](https://github.com/DanielCooding/QuadraFacil/issues/16) | Testes conferem o filtro por quadra/data, ordem, agenda vazia, cliente, fuso e remoção de canceladas. |
| [B13 / #17](https://github.com/DanielCooding/QuadraFacil/issues/17) | [ReservaApiTest](../src/test/java/br/com/quadrafacil/ReservaApiTest.java) usa HTTP real e confere os resultados no H2 de testes. |
| [B14 / #18](https://github.com/DanielCooding/QuadraFacil/issues/18) | [README](../README.md) e [coleção Postman](../postman/QuadraFacil.postman_collection.json). Falta registrar a execução por outro integrante a partir de um clone limpo. |
| [B15 / #19](https://github.com/DanielCooding/QuadraFacil/issues/19) | [Roteiro da demonstração](05-demonstracao.md), dados fictícios reproduzíveis e asserções da coleção. A apresentação em sala segue pendente. |
| [B16 / #20](https://github.com/DanielCooding/QuadraFacil/issues/20) | Mapeamento de contribuições e critérios neste documento. A equipe ainda precisa registrar a revisão, a execução independente e a integração à `main`. |

## Verificação local

- `mvn verify` com JDK 21 e Maven 3.9.11 executou 21 testes, sem falhas, erros ou testes ignorados. A suíte de reservas executou 7 testes de integração.
- A coleção foi executada via Newman 6.2.3 contra o JAR com H2 em memória. As 13 requisições e as 23 verificações passaram.
- Na demonstração, a primeira reserva retornou 201 e foi encontrada pelo ID. A sobreposição retornou 409. Após o cancelamento, a agenda ficou vazia, o mesmo intervalo retornou 201 com novo ID e a reserva antiga permaneceu consultável como `CANCELADA`.
- Na suíte concorrente, as duas tentativas para a mesma quadra e horário retornaram um 201 e um 409. A consulta ao H2 confirmou que apenas uma reserva foi gravada.

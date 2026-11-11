# Contratos HTTP do MVP — B03

Vamos usar `/api` em todos os endpoints de negócio. Este documento define o que será implementado nas próximas tarefas; não significa que todos os endpoints abaixo já funcionam.

## O que funciona agora

| Endpoint | Situação |
|---|---|
| `GET /api/status` | Implementado: status da aplicação |
| `GET /api/quadras` | Implementado com consulta ao H2 (B04), incluindo quadras inativas |
| `GET /` | Página simples de apresentação |

B04/B05 implementam também `POST /api/quadras`, `GET /api/quadras/{id}`, `PUT /api/quadras/{id}` e `PATCH /api/quadras/{id}/situacao`, conforme as seções abaixo. A resposta não inclui mais `proprietarioId`. Clientes, funcionamento, reservas e agenda permanecem planejados. Há tratamento local de erros de quadras; B07 deverá padronizar os demais módulos.

## Convenções

- Requisições com corpo usam `Content-Type: application/json`.
- IDs são UUIDs gerados pelo servidor. ID malformado retorna 400; bem formado, mas inexistente, retorna 404.
- Coleções retornam arrays, inclusive `[]` quando não há resultados. Sem paginação neste MVP didático.
- Campos de entrada aceitos são apenas os documentados. Campos extras devem retornar 400 na implementação HTTP; nunca aceitar `valorTotal`, `estado` ou `id` enviados na criação de reserva.
- Não há login ou controle por usuário nesta etapa. Usar dados fictícios e executar localmente, sem tratar a API como pronta para exposição pública.
- Dinheiro usa `BigDecimal`, reais e duas casas decimais. Preço por hora: de 0,01 a 9.999.999.999,99. Valor total com a mesma precisão; exceder esse limite retorna 400.
- Instantes são enviados em ISO 8601 com offset obrigatório, por exemplo `2030-01-07T18:00:00-03:00`. Respostas usam UTC com `Z`. Interpretar funcionamento e data da agenda em `America/Sao_Paulo`.
- Nos exemplos, substitua os UUIDs pelos retornados no cadastro e escolha uma data futura.

## Clientes — B06

| Método e caminho | Entrada | Sucesso |
|---|---|---|
| `POST /api/clientes` | `nome`, `email` | 201, cliente criado e header `Location: /api/clientes/{id}` |
| `GET /api/clientes` | Sem corpo | 200, array de clientes |
| `GET /api/clientes/{id}` | UUID no caminho | 200, cliente |

Nome obrigatório, não branco, até 100 caracteres. E-mail obrigatório, formato válido, até 254 caracteres. Remover espaços nas extremidades antes de validar/persistir. E-mail não é uma conta de login nem chave única neste MVP. Não há exclusão de clientes nesta etapa.

Entrada:
```json
{"nome":"Joãozinho Botafogo","email":"joao@example.com"}
```
Resposta 201 ou objeto de consulta:
```json
{"id":"00000000-0000-0000-0000-000000000001","nome":"Joãozinho Botafogo","email":"joao@example.com"}
```

## Quadras — B04 e B05

| Método e caminho | Entrada | Sucesso |
|---|---|---|
| `POST /api/quadras` | `nome`, `modalidade`, `precoHora` | 201, quadra com `ativa:true` e Location |
| `GET /api/quadras` | Sem corpo | 200, array de quadras, incluindo inativas |
| `GET /api/quadras/{id}` | UUID | 200, quadra |
| `PUT /api/quadras/{id}` | Os mesmos três campos obrigatórios do cadastro | 200, quadra atualizada; preserva `ativa` |
| `PATCH /api/quadras/{id}/situacao` | `{"ativa":false}` ou `{"ativa":true}` | 200, quadra atualizada |

Nome não branco até 100 caracteres; modalidade não branca até 40; preço positivo com no máximo duas casas. Não há exclusão física. Desativar impede novas reservas, mas não cancela as existentes. Alterar preço não recalcula reservas anteriores.

Entrada:
```json
{"nome":"Quadra Central","modalidade":"Futebol","precoHora":120.00}
```
Resposta:
```json
{"id":"00000000-0000-0000-0000-000000000002","nome":"Quadra Central","modalidade":"Futebol","precoHora":120.00,"ativa":true}
```

## Funcionamento — B08

Mantemos HorárioFuncionamento como quarta entidade: um registro por quadra e dia da semana.

- `GET /api/quadras/{id}/funcionamento`: 200, array de horários ordenados de segunda a domingo.
- `PUT /api/quadras/{id}/funcionamento`: substitui a semana inteira em uma transação; retorna 200 com o array salvo.
- Dias omitidos são fechados. Quadra nova começa sem funcionamento; não aceita reservas até a configuração. `[]` fecha a semana, se não invalidar reservas futuras.
- `diaSemana`: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`. Não repetir dia; no máximo sete itens.
- Abertura e fechamento em `HH:mm`, em blocos de 30 minutos, entre `00:00` e `23:30`. Abertura menor que fechamento. Não há funcionamento que atravesse a meia-noite, múltiplos turnos ou feriados no MVP.
- Alteração incompatível com reserva confirmada ainda não encerrada retorna 409 e mantém a semana anterior.

Entrada e resposta:
```json
[
  {"diaSemana":"MONDAY","abertura":"08:00","fechamento":"22:00"},
  {"diaSemana":"TUESDAY","abertura":"08:00","fechamento":"22:00"}
]
```

## Reservas — B09, B10 e B11

| Método e caminho | Entrada | Sucesso |
|---|---|---|
| `POST /api/reservas` | clienteId, quadraId, inicio, fim | 201, reserva confirmada e Location |
| `GET /api/reservas/{id}` | UUID | 200, reserva, inclusive cancelada |
| `POST /api/reservas/{id}/cancelamento` | Sem corpo | 200, reserva cancelada |

Entrada:
```json
{
  "clienteId":"00000000-0000-0000-0000-000000000001",
  "quadraId":"00000000-0000-0000-0000-000000000002",
  "inicio":"2030-01-07T18:00:00-03:00",
  "fim":"2030-01-07T19:30:00-03:00"
}
```
Resposta:
```json
{
  "id":"00000000-0000-0000-0000-000000000003",
  "clienteId":"00000000-0000-0000-0000-000000000001",
  "quadraId":"00000000-0000-0000-0000-000000000002",
  "inicio":"2030-01-07T21:00:00Z",
  "fim":"2030-01-07T22:30:00Z",
  "valorTotal":180.00,
  "estado":"CONFIRMADA"
}
```

Regras confirmadas para implementação:

1. Cliente e quadra devem existir; quadra precisa estar ativa.
2. Início futuro pelo relógio do servidor, anterior ao fim. Duração mínima de 60 minutos, em múltiplos de 30. Início e fim alinhados a meia hora, sem segundos/frações.
3. Mesmo dia local e dentro do funcionamento. A própria janela diária limita a duração máxima.
4. Calcular `precoHora × minutos / 60`, arredondando ao final para duas casas com `HALF_UP`. Salvar valor, sem cobrança online.
5. Intervalo `[início, fim)`: sobreposição quando `existente.inicio < nova.fim && existente.fim > nova.inicio`. Apenas reservas CONFIRMADAS bloqueiam.
6. B10 deve bloquear a quadra dentro da transação, consultar conflitos e inserir antes de liberar o bloqueio. Todas as operações que alterem disponibilidade deverão usar a mesma ordem de bloqueios. Somente consultar e depois inserir não basta.
7. Cancelar CONFIRMADA somente se `agora < inicio`; no instante do início ou depois, retornar 409. Cancelar uma já CANCELADA retorna 200 com o mesmo registro, sem novo efeito, inclusive após o horário original.
8. Cancelamento preserva histórico e libera o intervalo. Não há reembolso nem estados de pagamento.

## Agenda — B12

`GET /api/quadras/{id}/agenda?data=2030-01-07` retorna 200. Data obrigatória no formato `AAAA-MM-DD`; inválida retorna 400. Quadra inexistente retorna 404. Quadra inativa pode ser consultada para histórico.

```json
{
  "data":"2030-01-07",
  "fuso":"America/Sao_Paulo",
  "quadra":{"id":"00000000-0000-0000-0000-000000000002","nome":"Quadra Central","modalidade":"Futebol","precoHora":120.00,"ativa":true},
  "funcionamento":{"abertura":"08:00","fechamento":"22:00"},
  "reservas":[{"id":"00000000-0000-0000-0000-000000000003","clienteId":"00000000-0000-0000-0000-000000000001","inicio":"2030-01-07T21:00:00Z","fim":"2030-01-07T22:30:00Z","estado":"CONFIRMADA"}]
}
```

Agenda retorna apenas CONFIRMADAS, ordenadas pelo início. Sem reservas: `reservas:[]`. Dia fechado: `funcionamento:null`. Intervalos livres são as partes do funcionamento não ocupadas; não precisamos gerar uma lista de slots. Reservas canceladas permanecem consultáveis pelo ID. Consultar a agenda não garante reserva; a validação definitiva ocorre ao criar.

## Erros — B07 e Services futuros

Formato definido, ainda a implementar:
```json
{"status":409,"mensagem":"Já existe uma reserva para essa quadra no intervalo informado.","campos":[]}
```

```json
{"status":400,"mensagem":"Confira os campos informados.","campos":[{"campo":"nome","mensagem":"O nome é obrigatório."}]}
```

| Código | Exemplos |
|---|---|
| 400 | JSON/UUID/data inválidos; campos ausentes ou extras; início >= fim; duração inválida; fora do funcionamento; valor fora do limite |
| 404 | Cliente, quadra ou reserva inexistente |
| 409 | Sobreposição; quadra inativa ao reservar; cancelamento após início; mudança de funcionamento que invalida reserva |

Nunca incluir stack trace, SQL ou credenciais. Erros inesperados retornam 500 com mensagem genérica. Validar campos antes de resolver referências; depois verificar existência, estado da quadra, regras de intervalo e conflito. Os testes futuros devem verificar tanto HTTP quanto ausência de gravações parciais.

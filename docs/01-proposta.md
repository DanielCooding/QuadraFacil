# Proposta inicial — QuadraFácil

## 1. Grupo e problema

**Nome do projeto:** QuadraFácil.

**Integrantes:** Daniel Lucas, João Nobrega e Dante Cavalcante.

**Problema:** Proprietários de quadras esportivas que organizam reservas por mensagens ou anotações precisam conferir a agenda manualmente. Isso pode gerar reservas duplicadas, informações desatualizadas e dificuldade para informar horários livres. Os clientes dependem de respostas do responsável para escolher e confirmar um horário.

**Público-alvo:** Proprietários de espaços esportivos e pessoas que desejam reservar e organizar quadras de futebol, vôlei, beach tennis ou outras modalidades.

**Solução proposta:** Um sistema que centraliza quadras e reservas, permite consultar a agenda e impede reservas sobrepostas para a mesma quadra. Nesta etapa da disciplina, o sistema é uma **API REST com respostas JSON**, testada diretamente por Bruno, Postman ou curl.

## 2. Uma persona

**Persona fictícia**

**Nome:** Joãozinho Botafogo. **Idade:** 27 anos. **Ocupação:** auxiliar administrativo.

Joãozinho organiza uma partida de futebol com amigos toda semana. Usa principalmente o celular e combina o jogo em um grupo de amigos no WhatsApp.

- **Objetivo:** encontrar uma quadra e confirmar um horário sem depender de várias trocas de mensagens.
- **Dificuldades:** demora na resposta sobre disponibilidade, falta de clareza no preço e receio de chegar e encontrar outra reserva no mesmo horário.
- **Necessidades:** visualizar horários livres, conhecer o valor antes de confirmar e consultar a reserva depois.
- **Frase que resume sua expectativa:** “Quero escolher um horário e ter certeza de que ele está reservado para o meu grupo.”

## 3. Três histórias de usuário priorizadas

As histórias abaixo estão ajustadas ao recorte da disciplina: não há login, cobrança online nem frontend completo. O estado de cada uma está na seção 5.

### Prioridade 1 — Consultar disponibilidade

Como cliente, quero consultar as quadras e a agenda de uma data para escolher um intervalo adequado ao meu grupo.

**Critérios de aceitação previstos:**

- Consultar a agenda de uma quadra em uma data, com o funcionamento do dia e os intervalos já ocupados por reservas confirmadas.
- Reservas canceladas não aparecem como ocupadas.
- A resposta traz nome, modalidade e preço por hora da quadra.

### Prioridade 2 — Reservar sem conflito

Como cliente, quero reservar um intervalo livre e ver o valor calculado para garantir meu horário sem sobreposição com outra reserva.

**Critérios de aceitação previstos:**

- Informar um cliente e uma quadra existentes, e um intervalo dentro do funcionamento.
- Calcular o valor no backend e salvar o preço contratado na reserva.
- Rejeitar sobreposição parcial ou total, inclusive em solicitações simultâneas.
- Permitir reservas consecutivas e reservas simultâneas em quadras diferentes.
- Retornar a confirmação ou explicar o conflito de horário.

### Prioridade 3 — Administrar a agenda

Como responsável pelas quadras, quero cadastrar quadras, definir o funcionamento e consultar as reservas por data para organizar o uso do espaço.

**Critérios de aceitação previstos:**

- Cadastrar nome, modalidade e preço por hora da quadra, e desativar uma quadra.
- Definir dias e horários de funcionamento.
- Consultar as reservas de uma quadra por data e cancelar uma reserva quando necessário.
- Impedir alterações de funcionamento que invalidem reservas confirmadas.

## 4. MVP definido (recorte da disciplina)

**Objetivo do MVP:** permitir cadastrar clientes e quadras e **reservar um horário sem conflitos**.

**Regra central:** uma mesma quadra não pode ter duas reservas confirmadas com horários sobrepostos.

**Fluxo principal de ponta a ponta:**

1. Cadastrar uma quadra (com funcionamento) e um cliente.
2. Consultar a agenda da quadra em uma data.
3. Criar uma reserva em um intervalo livre; o servidor calcula o valor.
4. Tentar reservar um intervalo que se sobrepõe: o sistema recusa com mensagem clara.
5. Cancelar uma reserva: o histórico fica preservado e o intervalo é liberado.

**Entidades (quatro):** Cliente, Quadra, Reserva e Horário de Funcionamento. Detalhes em `docs/02-modelo-inicial.md`.

**Incluído no MVP:**

- Cadastro e consulta de clientes.
- Cadastro, consulta, edição e desativação de quadras, com funcionamento semanal.
- Reserva com duração mínima de 1 hora, incrementos de 30 minutos e valor calculado pelo servidor.
- Proteção contra sobreposição no backend, com transação e bloqueio da quadra durante a verificação e a gravação.
- Consulta da agenda por quadra e data.
- Cancelamento com preservação do histórico e liberação do intervalo.
- Validações e respostas de erro padronizadas, e testes automatizados com H2.

**Fora desta etapa:** login e perfis de acesso, pagamento online, frontend completo, integrações externas, Docker e microsserviços. A lista de requisitos futuros está na seção 6.

**Política simplificada do MVP:**

- Uma reserva aceita fica confirmada, sem cobrança online.
- Uma reserva confirmada pode ser cancelada enquanto ainda não começou. O cancelamento preserva o histórico e libera o horário.
- Os horários usam o fuso de Brasília (`America/Sao_Paulo`).

> Duração, alinhamento de horários e cancelamento foram definidos em `docs/03-contratos-http.md` (B03). A implementação dessas regras continua nas tarefas B09–B11.

**Tecnologias desta etapa:** Java 21, Spring Boot, Maven, Spring Web, Spring Data JPA, Bean Validation, H2 e JUnit. A API é testada com Bruno, Postman ou curl.

**Sobre o H2:** é um banco em memória, usado nesta etapa **apenas com finalidade didática**. Os dados não são mantidos ao reiniciar a aplicação. Ele não é a escolha para um sistema em produção.

## 5. Estado das histórias e das issues de planejamento

As issues #1 a #4 estão fechadas porque o **planejamento** foi concluído. Elas registram a definição das histórias e do MVP e **não indicam que a funcionalidade esteja implementada**. O desenvolvimento é acompanhado pelas issues B01 a B16 (#5 a #20).

| Registro | Conteúdo | Estado da funcionalidade |
|---|---|---|
| #1 — História 01 | Consultar disponibilidade | Planejada |
| #2 — História 02 | Reservar sem conflito | Planejada |
| #3 — História 03 | Administrar a agenda | Planejada |
| #4 — MVP | Definição do escopo | Planejado |

O que existe hoje: entidades JPA, repositories, H2 e DTOs validados. O módulo de quadras já permite cadastro, consulta, edição e alteração de situação pela API. Clientes já possuem cadastro, listagem e consulta por ID. Funcionamento e reservas ainda não têm seus fluxos HTTP implementados. Os contratos estão em `docs/03-contratos-http.md`.

## 6. Requisitos futuros (fora da entrega acadêmica)

Fazem parte da ideia original do projeto, mas **não serão implementados nesta etapa**:

| Recurso | Observação |
|---|---|
| Autenticação e perfis (cliente e proprietário) | A etapa atual não tem login |
| PostgreSQL/Supabase | Substitui o H2 em uma versão real; a restrição de exclusão de intervalos no banco entra aqui |
| Pagamento online (Mercado Pago), prazo e reembolso | Hoje a reserva aceita já fica confirmada |
| Código de acesso por reserva | Depende de definição futura |
| Frontend HTML, CSS e JavaScript | A demonstração desta etapa é feita pela API |
| Publicação na Vercel/Railway | Não é requisito para executar esta entrega |
| Fotos, relatórios avançados e notificações | Evolução do projeto completo |

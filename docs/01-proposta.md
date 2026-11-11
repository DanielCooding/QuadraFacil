# Proposta inicial — QuadraFácil

## 1. Grupo e problema

**Nome do projeto:** QuadraFácil.

**Integrantes:** Daniel Lucas, João Nobrega e Dante Cavalcante .

**Problema:** Um proprietário de quadras esportivas que organiza reservas por mensagens ou anotações e precisava conferir manualmente a agenda. Isso poderia gerar reservas duplicadas, informações desatualizadas e dificuldade para informar horários livres. Os clientes dependem de respostas do responsável para escolher e confirmar um horário.

**Público-alvo:** Proprietários de espaços esportivos e pessoas que desejam reservar e organizar quadras de futebol, vôlei, beach tennis ou outras modalidades.

**Solução proposta:** Uma aplicação web que centraliza quadras e reservas, permite consultar disponibilidade e impede reservas sobrepostas para a mesma quadra.

## 2. Uma persona

**Persona fictícia**

**Nome:** Joãozinho Botafogo. **Idade:** 27 anos. **Ocupação:** auxiliar administrativo.

Joãozinho organiza uma partida de futebol com amigos toda semana. Usa principalmente o celular e combina o jogo em um grupo de amigos no Whatsapp.

- **Objetivo:** encontrar uma quadra e confirmar um horário sem depender de várias trocas de mensagens.
- **Dificuldades:** demora na resposta sobre disponibilidade, falta de clareza no preço e receio de chegar e encontrar outra reserva no mesmo horário.
- **Necessidades:** visualizar horários livres, conhecer o valor antes de confirmar e consultar a reserva depois.
- **Frase que resume sua expectativa:** “Quero escolher um horário e ter certeza de que ele está reservado para o meu grupo.”

## 3. Três histórias de usuário priorizadas

### Prioridade 1 — Consultar disponibilidade

Como cliente, quero consultar quadras e horários disponíveis por data para escolher um intervalo adequado ao meu grupo.

**Critérios de aceitação previstos:**

- Selecionar a data e consultar o funcionamento da quadra.
- Diferenciar horários livres e ocupados com textos, além das cores.
- Exibir nome, modalidade e preço por hora.

### Prioridade 2 — Reservar sem conflito

Como cliente, quero reservar um intervalo livre e revisar o valor para garantir meu horário sem sobreposição com outra reserva.

**Critérios de aceitação previstos:**

- Exigir identificação do cliente e intervalo dentro do funcionamento.
- Calcular o valor no backend e salvar o preço contratado.
- Rejeitar sobreposição parcial ou total, inclusive em solicitações simultâneas.
- Permitir reservas consecutivas e reservas simultâneas em quadras diferentes.
- Retornar confirmação ou explicar o conflito de horário.

### Prioridade 3 — Administrar a agenda

Como proprietário, quero cadastrar minhas quadras, definir funcionamento e consultar reservas por data para organizar o uso do espaço.

**Critérios de aceitação previstos:**

- Cadastrar nome, modalidade e preço por hora da quadra.
- Definir dias e horários de funcionamento.
- Consultar reservas de suas próprias quadras e cancelar uma reserva quando necessário.
- Impedir alterações de funcionamento que invalidem reservas existentes.

## 4. MVP definido

**Objetivo do MVP:** permitir que um cliente encontre um horário e faça uma reserva confiável, enquanto o proprietário acompanha a agenda.

**Incluído no MVP planejado:**

- Autenticação e separação de cliente/proprietário.
- Cadastro básico de quadras e funcionamento semanal.
- Consulta de disponibilidade por data.
- Reserva com duração mínima de 1 hora, incrementos de 30 minutos e valor calculado pelo servidor.
- Proteção contra sobreposição no backend e no PostgreSQL.
- Consulta das próprias reservas pelo cliente e da agenda pelo proprietário.
- Cancelamento com preservação do histórico e liberação do intervalo.

**Fora deste MVP:** pagamento online, prazo de pagamento, reembolso, código de acesso, fechaduras/portões, upload de fotos, relatórios avançados e notificações automáticas. Essas funcionalidades pertencem à evolução do projeto completo.

**Política simplificada do MVP:** reservas aceitas ficam confirmadas. O cliente pode cancelar antes do início, e o proprietário pode cancelar suas reservas com registro do histórico. As regras financeiras serão definidas quando o pagamento for incorporado.

**Tecnologias previstas:** Java 21 e Spring Boot; PostgreSQL/Supabase; frontend HTML, CSS e JavaScript puro. Vercel e Railway são opções de publicação futura, não requisitos para executar esta primeira entrega.


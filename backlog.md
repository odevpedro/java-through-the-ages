# Backlog — Java Through the Ages

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado.
> **Ultima atualizacao:** 2026-06-30

---

## Sobre o Projeto

Monorepo educacional que apresenta a evolucao da plataforma Java como um museu
de problemas historicos resolvidos. Cada modulo implementa uma aplicacao diferente,
coerente com o contexto da epoca — nao uma repeticao do mesmo dominio funcional.

**Versao atual:** `2.0.0`
**Repositorio:** [github.com/odevpedro/java-through-the-ages](https://github.com/odevpedro/java-through-the-ages)
**Stack principal:** multipla (JDK 1.1 a JDK 21)

---

## Legenda

| Simbolo | Significado |
|---------|-------------|
| `[ ]` | Pendente |
| `[~]` | Em andamento |
| `[x]` | Concluido |
| `P0` | Critico |
| `P1` | Alta prioridade |
| `P2` | Media prioridade |

---

---

## Concluidas

| Item | Data | Modulo |
|------|------|--------|
| `[x]` Estrutura de diretorios | 2026-06-13 | Todos |
| `[x]` README principal | 2026-06-13 | — |
| `[x]` Documentacao em docs/ | 2026-06-13 | — |
| `[x]` Modulo 01 — Applet/AWT Browser Era (reescrito: catalogo de produtos) | 2026-06-13 | 01 |
| `[x]` Modulo 02 — Swing Desktop Backoffice (reescrito: cadastro de clientes) | 2026-06-13 | 02 |
| `[x]` Modulo 03 — RMI Branch Office (reescrito: consulta/reserva de estoque) | 2026-06-13 | 03 |
| `[x]` Modulo 04 — Servlet/JSP/JDBC Intranet | 2026-06-13 | 04 |
| `[x]` Modulo 05 — EJB Enterprise Transaction Era | 2026-06-13 | 05 |
| `[x]` Modulo 06 — Java ME Field Service Mobile | 2026-06-13 | 06 |
| `[x]` Modulo 07 — Spring XML Service Layer | 2026-06-13 | 07 |
| `[x]` Modulo 08 — Java5 Generics/Concurrency | 2026-06-13 | 08 |
| `[x]` Modulo 09 — Spring MVC REST JSON | 2026-06-13 | 09 |
| `[x]` Modulo 10 — Spring Boot Microservice | 2026-06-13 | 10 |
| `[x]` Modulo 11 — Cloud Native Observability | 2026-06-13 | 11 |
| `[x]` Modulo 12 — Modern Java Language | 2026-06-13 | 12 |
| `[x]` Bugfix: tabela nao criada no Mod 07 (InitializingBean) | 2026-06-30 | 07 |
| `[x]` Bugfix: rollback simulado no Mod 05 (restauracao de backup) | 2026-06-30 | 05 |
| `[x]` Bugfix: status nulo em dados iniciais do Mod 10 | 2026-06-30 | 10 |
| `[x]` Bugfix: JSP legado do dominio anterior removido (Mod 04) | 2026-06-30 | 04 |
| `[x]` Bugfix: links quebrados no Mod 04 (/mensagens e /WEB-INF/) | 2026-06-30 | 04 |
| `[x]` Bugfix: recursao @Async bypassando proxy Spring (Mod 11) | 2026-06-30 | 11 |
| `[x]` Bugfix: condicao de corrida no retry do Mod 11 | 2026-06-30 | 11 |
| `[x]` Melhoria: metricas Prometheus customizadas (Mod 11) | 2026-06-30 | 11 |
| `[x]` Melhoria: ResponseEntity 404 e JSON Map no Mod 09 | 2026-06-30 | 09 |
| `[x]` Melhoria: otimizacao RecordStore e remocao de metodo morto (Mod 06) | 2026-06-30 | 06 |
| `[x]` Limpeza: @SuppressWarnings desnecessario removido (Mod 08) | 2026-06-30 | 08 |
| `[x]` Exception handling: e.printStackTrace() substituido por java.util.logging (Mod 02, 03) | 2026-06-30 | 02, 03 |
| `[x]` Troca double por BigDecimal (Mod 07, 09, 10, 11, 12) | 2026-06-30 | 07, 09, 10, 11, 12 |
| `[x]` DTOs para desacoplar API do modelo interno (Mod 09, 10, 11) | 2026-06-30 | 09, 10, 11 |
| `[x]` Testes unitarios com JUnit 5 + Mockito (Mod 09, 10, 11, 12) | 2026-06-30 | 09, 10, 11, 12 |
| `[x]` data-model.md atualizado com BigDecimal e tipos corrigidos | 2026-06-30 | docs |
| `[x]` Limpeza: pacote 'mensagens' renomeado para dominio real (01, 02, 03, 04) | 2026-09-11 | 01, 02, 03, 04 |
| `[x]` Remocao da pasta orfa 05-javame (residuo pre-reescrita) | 2026-09-11 | — |

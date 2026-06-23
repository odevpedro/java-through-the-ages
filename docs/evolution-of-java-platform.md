# Evolution of the Java Platform

Como a plataforma Java evoluiu para resolver problemas de cada epoca.

---

## A linha conceitual

Java nunca foi "uma linguagem". Foi uma plataforma que se adaptou aos problemas
de cada decada:

```
1995: "Rode no navegador"
1998: "Rode no desktop"
2001: "Rode no servidor"
2004: "Rode no celular"
2008: "Sirva dados para outros sistemas"
2014: "Rode como microservico"
2018: "Seja observavel na cloud"
2021: "Seja expressivo como linguagens modernas"
```

---

## As 3 grandes transicoes

### 1. Cliente → Servidor (1998–2004)

Java comecou como tecnologia de **cliente** (Applets no browser, Swing no desktop).
Mas a web venceu. Em 2004, o centro gravitacional do Java tinha migrado para
o servidor: Servlets, JSP, EJBs.

O que se perdeu: o sonho "Write Once, Run Anywhere" no cliente.
O que se ganhou: dominio absoluto no backend corporativo.

### 2. Monolito → Microservicos (2014–2018)

Aplicacoes Java tradicionais eram WARs enormes em servidores de aplicacao (JBoss,
WebLogic). Microservicos dividiram essas aplicacoes em dezenas de componentes
independentes.

O que se perdeu: transacoes distribuidas faceis, deploy centralizado.
O que se ganhou: escalabilidade independente, isolamento de falhas, deploy frequente.

### 3. Funcional → Operavel (2018+)

Nao bastava a aplicacao funcionar. Ela precisa ser **operavel**: health checks,
metricas, tracing, logs estruturados, resiliencia.

O que se perdeu: simplicidade operacional.
O que se ganhou: capacidade de rodar em ambientes com milhares de servicos.

---

## O que cada geracao herdou da anterior

| Geracao | Herdou | Descartou |
|---------|--------|-----------|
| Applet | — | — |
| Swing | Componentes AWT | Delegacao ao SO |
| RMI | Serializacao | Transparencia de localizacao |
| Servlet/JSP | JDBC | RMI como protocolo web |
| EJB | RMI, JNDI, transacoes | Simplicidade |
| Java ME | Sintaxe Java | Toda API SE |
| Spring XML | JDBC, Servlets | Container EJB |
| Java 5 | Collections | Casts explicitos |
| Spring MVC | MVC, DI | Paginas JSP |
| Spring Boot | Spring | Configuracao XML |
| Cloud Native | Spring Boot | Deploy manual sem metricas |
| Modern Java | Tipos, compatibilidade | Verbosidade |

---

## A persistencia do Java

Java sobreviveu a:

1. **Bolha das pontocom** (2000–2001) — consolidou-se como linguagem corporativa
2. **Aquisicao pela Oracle** (2009) — comunidade receou o fim do Java open source
3. **Ascensao do Android** (2008) — fragmentou, mas expandiu o ecossistema
4. **Cloud e containers** (2014+) — adaptou-se com Spring Boot e Micronaut
5. **Linguagens modernas** (2010+) — Kotlin, Scala, Go — Java respondeu com records,
   sealed classes, virtual threads

Cada crise gerou uma inovacao que manteve a plataforma relevante.

# Data Model — Java Through the Ages

> Documento do modelo de dados de cada modulo. Cada modulo tem seu proprio dominio
> e modelo de dados, refletindo o problema historico que a tecnologia resolvia.

---

## Indice

- [Modulo 01 — Produto (Applet)](#modulo-01--produto-applet)
- [Modulo 02 — Cliente (Swing)](#modulo-02--cliente-swing)
- [Modulo 03 — Produto (RMI)](#modulo-03--produto-rmi)
- [Modulo 04 — Chamado (Servlet/JSP)](#modulo-04--chamado-servletjsp)
- [Modulo 05 — Conta (EJB Simulado)](#modulo-05--conta-ejb-simulado)
- [Modulo 06 — Vistoria (Java ME)](#modulo-06--vistoria-java-me)
- [Modulo 07 — Pedido (Spring XML)](#modulo-07--pedido-spring-xml)
- [Modulo 08 — Tarefa/Resultado (Java5)](#modulo-08--tarefaresultado-java5)
- [Modulo 09 — Produto (Spring MVC)](#modulo-09--produto-spring-mvc)
- [Modulo 10 — Conta (Spring Boot)](#modulo-10--conta-spring-boot-jpa)
- [Modulo 11 — Pedido (Cloud Native)](#modulo-11--pedido-cloud-native)
- [Modulo 12 — Solicitacao/Resultado (Modern Java)](#modulo-12--solicitacaoresultado-modern-java)

---

## Modulo 01 — Produto (Applet)

**Armazenamento:** memoria (Vector), sem persistencia
**Tecnologia:** Java 1.0 sem Collections Framework

| Campo | Tipo | Descricao |
|-------|------|-----------|
| nome | String | Nome do produto |
| precoUnitario | double | Preco unitario |

---

## Modulo 02 — Cliente (Swing)

**Armazenamento:** serializacao em clientes.dat (ObjectOutputStream)
**Tecnologia:** java.io.Serializable

| Campo | Tipo | Descricao |
|-------|------|-----------|
| nome | String | Nome do cliente |
| email | String | Email |
| telefone | String | Telefone |

---

## Modulo 03 — Produto (RMI)

**Armazenamento:** memoria no servidor RMI
**Tecnologia:** Serializable (trafega pela rede via RMI)

| Campo | Tipo | Descricao |
|-------|------|-----------|
| id | int | Identificador unico |
| nome | String | Nome do produto |
| quantidadeEmEstoque | int | Quantidade disponivel |

---

## Modulo 04 — Chamado (Servlet/JSP)

**Armazenamento:** HSQLDB (banco relacional em memoria)
**Tecnologia:** JDBC puro

| Campo | Tipo SQL | Descricao |
|-------|----------|-----------|
| id | INTEGER (auto) | Identificador unico |
| titulo | VARCHAR(255) | Titulo do chamado |
| descricao | VARCHAR(2000) | Descricao detalhada |
| solicitante | VARCHAR(255) | Nome de quem abriu |
| dataAbertura | VARCHAR(50) | Data de abertura |

---

## Modulo 05 — Conta (EJB Simulado)

**Armazenamento:** HashMap em memoria
**Tecnologia:** Java padrão (simulacao)

| Campo | Tipo | Descricao |
|-------|------|-----------|
| numero | int | Numero da conta |
| titular | String | Nome do titular |
| saldo | double | Saldo atual |

---

## Modulo 06 — Vistoria (Java ME)

**Armazenamento:** RecordStore (RMS)
**Tecnologia:** Serializacao manual via DataOutputStream -> byte[]

| Campo | Tipo | Descricao |
|-------|------|-----------|
| codigoCliente | String | Codigo do cliente visitado |
| status | String | Realizada/Pendente/Cancelada |
| observacao | String | Observacao do tecnico |
| data | long | Timestamp da vistoria |

---

## Modulo 07 — Pedido (Spring XML)

**Armazenamento:** HSQLDB via JdbcTemplate
**Tecnologia:** JDBC com template Spring

| Campo | Tipo SQL | Descricao |
|-------|----------|-----------|
| id | INTEGER (auto) | Identificador |
| cliente | VARCHAR(255) | Nome do cliente |
| produto | VARCHAR(255) | Produto comprado |
| quantidade | INTEGER | Quantidade |
| valorTotal | DECIMAL(12,2) | Valor total do pedido |
| status | VARCHAR(50) | PENDENTE, PAGO, CANCELADO |

---

## Modulo 08 — Tarefa/Resultado (Java5)

**Armazenamento:** em memoria (List<Tarefa>, List<Resultado>)
**Tecnologia:** Generics + Enums

### Tarefa
| Campo | Tipo | Descricao |
|-------|------|-----------|
| id | int | Identificador |
| nome | String | Nome da tarefa |
| simulacaoMs | long | Tempo simulado de processamento |

### ResultadoProcessamento
| Campo | Tipo | Descricao |
|-------|------|-----------|
| idTarefa | int | Referencia a tarefa |
| nomeTarefa | String | Nome original |
| status | StatusTarefa | Enum: PENDENTE, PROCESSANDO, CONCLUIDA, ERRO |
| mensagem | String | Mensagem de resultado |

---

## Modulo 09 — Produto (Spring MVC)

**Armazenamento:** ConcurrentHashMap em memoria
**Tecnologia:** Java collections + REST JSON

| Campo | Tipo | Descricao |
|-------|------|-----------|
| id | int | Identificador |
| nome | String | Nome do produto |
| preco | BigDecimal | Preco |
| categoria | String | Categoria |

---

## Modulo 10 — Conta (Spring Boot JPA)

**Armazenamento:** H2 in-memory via JPA
**Tecnologia:** Spring Data JPA + Hibernate

| Campo | Tipo | Descricao |
|-------|------|-----------|
| id | Long (auto) | Identificador |
| descricao | String | Descricao da conta |
| valor | BigDecimal | Valor |
| tipo | String | RECEBER ou PAGAR |
| status | String | PENDENTE ou PAGO |
| vencimento | LocalDate | Data de vencimento |

---

## Modulo 11 — Pedido (Cloud Native)

**Armazenamento:** H2 in-memory via JPA
**Tecnologia:** Spring Boot + Actuator + Micrometer

| Campo | Tipo | Descricao |
|-------|------|-----------|
| id | Long (auto) | Identificador |
| cliente | String | Nome do cliente |
| descricao | String | Descricao do pedido |
| valor | BigDecimal | Valor |
| status | String | CRIADO, PROCESSANDO, CONCLUIDO, ERRO |
| criadoEm | LocalDateTime | Timestamp de criacao |
| tentativas | int | Numero de tentativas de processamento |

---

## Modulo 12 — Solicitacao/Resultado (Modern Java)

**Armazenamento:** em memoria (List.of)
**Tecnologia:** Records + Sealed interfaces

### Solicitacao (sealed interface)
| Subtipo | Campos | Descricao |
|---------|--------|-----------|
| Emprestimo | cliente (String), valor (BigDecimal), parcelas (int), rendaMensal (BigDecimal) | Solicitacao de emprestimo |
| Credito | cliente (String), valor (BigDecimal), limiteDisponivel (BigDecimal), possuiRestricao (boolean) | Solicitacao de credito |
| Consorcio | cliente (String), valor (BigDecimal), totalParcelas (int), parcelasPagas (int) | Solicitacao de consorcio |

### RegraAnalise (sealed interface)
| Subtipo | Campos | Descricao |
|---------|--------|-----------|
| Aprovada | motivo | Solicitacao aprovada |
| Negada | motivo | Solicitacao negada |
| RevisaoManual | motivo | Requer analise humana |

# Modulo 11 — Cloud Native Observability Era

> **Era:** 2018–2021 · **JDK:** 11+ · **Paradigma:** Cloud native, observabilidade

---

## 1. Contexto historico

Entre 2018 e 2021, a infraestrutura de software passou por uma transformacao
profunda. Aplicacoes Java deixaram de rodar em servidores dedicados ou VMs
e passaram a rodar em **containers Docker** orquestrados por **Kubernetes**,
em ambientes com centenas ou milhares de microservicos.

Isso mudou fundamentalmente o que significa "fazer a aplicacao funcionar":
- Nao se pode mais fazer SSH para debugar
- Nao se pode mais "olhar os logs" no console
- Nao se pode reiniciar o servidor manualmente
- O orchestrator (Kubernetes) precisa saber se a aplicacao esta viva e pronta
- Operadores precisam de metricas, dashboards e alertas
- Incidentes precisam de tracing para rastrear a causa raiz

Nesse contexto, o Spring Boot 2.x (2018) foi um marco, integrando:
- **Micrometer** para metricas (Prometheus, Graphite, Datadog)
- **Actuator** aprimorado com health groups, info, metrics
- **Logback** com appenders JSON para logs estruturados
- **Reactive stack** (WebFlux) para melhor utilizacao de recursos

## 2. Problema historico

Uma aplicacao que funciona em desenvolvimento ou em uma VM dedicada pode
falhar em producao num ambiente Kubernetes por razoes que nao sao de codigo:

- O health endpoint retorna 200 mas a aplicacao nao consegue conectar ao banco
- A memoria cresce lentamente e o OOMKill do Kubernetes mata o pod sem aviso
- Uma requisicao lenta em um servico degrada todo o sistema
- O log esta espalhado em centenas de pods que morreram e foram recriados
- Nao ha como saber se um pico de erros e normal ou um incidente

**A observabilidade virou requisito, nao feature.**

## 3. Aplicacao: Servico de processamento assincrono de pedidos

Uma API que:
- Recebe pedidos via HTTP POST
- Publica evento em fila em memoria (simulada)
- Worker processa de forma assincrona com `@Async` e ThreadPool
- Registra logs estruturados em cada etapa
- Expoe health checks customizados (pronto para Kubernetes probes)
- Expoe metricas (contagem de pedidos por status)
- Tem mecanismo de retry para falhas de processamento
- Status tracking: CRIADO -> PROCESSANDO -> CONCLUIDO (ou ERRO com retry)

## 4. Recursos e tecnicas

| Recurso | Relevancia neste modulo |
|---------|------------------------|
| Spring Boot 2.7.x | Base do projeto |
| `spring-boot-starter-actuator` | Health, metrics, info |
| Micrometer + Prometheus | Metricas de negocio e sistema |
| `@Async` + `ThreadPoolTaskExecutor` | Processamento assincrono |
| Logback com MDC | Logs estruturados com contexto |
| `HealthIndicator` customizado | Health check com logica de negocio |
| `application.yml` | Configuracao de datasource, pool, logging |
| Retry manual | Simulacao de resiliencia |

## 5. Arquitetura e design

```
    POST /api/pedidos
         │
         ▼
  PedidoController
    └── service.criarPedido(dados)
         │
         ▼
  PedidoService
    ├── 1. Salva Pedido como CRIADO (JPA)
    ├── 2. Log estruturado: "Pedido criado" (MDC: id, cliente, valor)
    ├── 3. Submete processamento async
    └── Retorna Pedido com status 201
         │
         ▼
  @Async ProcessamentoSimulado
    ├── 1. Atualiza status para PROCESSANDO (JPA)
    ├── 2. Log: "Iniciando processamento"
    ├── 3. Simula delay (Thread.sleep)
    ├── 4. Se falha > log de erro, retry
    ├── 5. Se sucesso > status CONCLUIDO
    └── 6. Log: "Processamento finalizado"
         │
         ▼
  Actuator Endpoints:
    ├── /actuator/health        → ready for K8s probes
    ├── /actuator/metrics       → pedidos.ativos, pedidos.erro
    └── /actuator/info          → versao, descricao
```

## 6. Limitacoes e dificuldades

### Fila em memoria (simulada)

Em producao, usariamos RabbitMQ, Kafka ou AWS SQS. A fila em memoria nao
persiste mensagens se a aplicacao reiniciar, e nao escala horizontalmente.

### Sem tracing distribuido

Tracing (Spring Cloud Sleuth + Zipkin) nao foi implementado para manter
o modulo simples. Em producao, tracing e essencial para debugar fluxos
que cruzam multiplos servicos.

### Retry simplificado

O retry implementado e um `for` com `Thread.sleep`. Em producao, usariamos
Resilience4J ou Spring Retry com exponential backoff e circuit breaker.

### Sem Dockerfile

Embora o modulo seja "cloud native", o Dockerfile e opcional.
Um Dockerfile simples seria:
```dockerfile
FROM openjdk:11-jre-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 7. Peca de museu

Este modulo representa a mudanca de paradigma mais significativa do Java
moderno: o foco saiu de "fazer a aplicacao funcionar" para "manter o
sistema operacional em ambientes distribudos".

Saude, metricas, logs estruturados e processamento assincrono nao sao
"extras" — sao requisitos de primeira classe. O codigo que apenas
"funciona" nao e suficiente para rodar em Kubernetes com centenas de
microservicos.

A observabilidade nao e sobre ferramentas. E sobre mudanca cultural:
o desenvolvedor precisa pensar em operacao enquanto escreve codigo.

## 8. Como executar

**Pre-requisito:** JDK 11+ + Maven 3.6+

```bash
cd 11-cloud-native-observability-era
mvn spring-boot:run
```

Testar:
```bash
# Criar pedido
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Empresa X","descricao":"Notebook Dell","valor":5500.00}'

# Listar pedidos
curl http://localhost:8080/api/pedidos

# Health check
curl http://localhost:8080/actuator/health

# Metricas
curl http://localhost:8080/actuator/metrics
```

## 9. O que essa era resolveu

Comparado ao Spring Boot inicial (modulo 10):
- Observabilidade nativa (Micrometer, Actuator aprimorado)
- Health checks semanticos (liveness vs readiness, health groups)
- Metricas de negocio alem das tecnicas
- Logs estruturados (JSON) para agregacao em Loki/ELK
- Processamento assincrono com pool de threads gerenciado
- Resiliencia basica (retry)

## 10. O que essa era ainda nao resolvia

- Complexidade real de mensageria (Kafka/RabbitMQ)
- Configuracao externa centralizada (Config Server)
- Service mesh (Istio/Linkerd)
- Tracing distribuido completo (OpenTelemetry)
- Native images (startup instantaneo Spring Native/GraalVM)
- Virtual threads (Project Loom, JDK 21)
- Performance para serverless (cold start)

## 11. Evolucao posterior

| Problema | Solucao |
|----------|---------|
| Fila em memoria | **RabbitMQ / Kafka / AWS SQS** |
| Sem tracing | **OpenTelemetry (2021+)** |
| Retry simplificado | **Resilience4J + exponential backoff** |
| Startup lento | **Spring Native / GraalVM (2021+)** |
| Configuracao fixa | **Spring Cloud Config + Vault** |
| Thread pool pesado | **Virtual threads / Project Loom (JDK 21)** |
| Observabilidade manual | **Service mesh (Istio) com telemetria automatica** |

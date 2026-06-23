# Modulo 10 — Spring Boot Microservice

> **Era:** 2014–2017 · **JDK:** 8+ · **Paradigma:** Microservico, auto-configuracao

---

## 1. Contexto historico

Em 2014, dois eventos transformaram o desenvolvimento Java:

1. **Java 8** (marco 2014) — lambdas, streams, Optional, nova API de datas
2. **Spring Boot 1.0** (abril 2014) — auto-configuration, embedded server,
   starter dependencies, fat JAR, Actuator

Spring Boot foi a resposta a um cansaço generalizado com configuracao XML.
O mantra "convention over configuration" eliminou decisoes repetitivas:
o framework escolhia defaults inteligentes, e o desenvolvedor so configurava
o que fugia do padrao.

Ao mesmo tempo, o movimento de microservicos ganhava forca (Martin Fowler,
2014). Aplicacoes monoliticas estavam sendo divididas em dezenas de servicos
independentes. Cada servico precisava ser criado, configurado e deployado
rapidamente — algo que Spring Boot viabilizou.

## 2. Problema historico

Antes do Spring Boot, criar uma aplicacao Spring web exigia:
- `web.xml` — configurar DispatcherServlet
- `applicationContext.xml` — configurar DataSource, TransactionManager, etc.
- `spring-servlet.xml` — configurar component-scan, view resolver, message converters
- `pom.xml` — declarar todas as dependencias manualmente
- Tomcat externo — baixar, configurar, deployar WAR

Para 10 microservicos, essa configuracao era repetida 10 vezes.
Cada microservico era um mini-projeto de configuracao antes de escrever
qualquer codigo de negocio.

## 3. Aplicacao: Microservico de contas a pagar/receber

API REST para gerenciamento financeiro:
- Contas a pagar e receber
- CRUD completo
- Status: PENDENTE, PAGO
- JPA + H2 in-memory
- Actuator para health check e metricas
- Configuracao via application.yml

## 4. Recursos do Spring Boot

| Recurso | Relevancia neste modulo |
|---------|------------------------|
| `@SpringBootApplication` | Entry point unico; combina @Configuration + @EnableAutoConfiguration + @ComponentScan |
| `spring-boot-starter-web` | Dependencia unica para aplicacao web (inclui Tomcat embutido) |
| `spring-boot-starter-data-jpa` | JPA + Hibernate + HikariCP + transacoes |
| `spring-boot-starter-actuator` | Health checks, metricas, info endpoints |
| `@RestController` | @Controller + @ResponseBody em uma anotacao |
| `JpaRepository<T, ID>` | CRUD automatico sem implementacao |
| `application.yml` | Configuracao declarativa, hierarquica |
| `CommandLineRunner` | Execucao de codigo na inicializacao |
| H2 Console | Banco em memoria com console web para debug |

## 5. Arquitetura e design

```
[Client HTTP]
     │
     ▼
ContasApplication.main()  (@SpringBootApplication)
     │
     ├── Embedded Tomcat (porta 8080)
     │
     ├── ContaController (@RestController)
     │     ├── GET    /api/contas
     │     ├── GET    /api/contas/{id}
     │     ├── POST   /api/contas
     │     ├── PUT    /api/contas/{id}/pagar
     │     └── DELETE /api/contas/{id}
     │
     ├── ContaService (@Service)
     │     └── @Transactional
     │
     ├── ContaRepository (extends JpaRepository)
     │     └── CRUD automatico
     │
     ├── H2 Database (in-memory)
     │
     ├── Actuator Endpoints:
     │     ├── /actuator/health
     │     ├── /actuator/info
     │     └── /actuator/metrics
     │
     └── H2 Console: /h2-console
```

## 6. Limitacoes e dificuldades

### Single module (sem multi-module Maven)

Microservicos reais tem multiplos modulos (domain, application, infrastructure).
Aqui usamos um modulo unico para simplicidade didatica.

### Sem service discovery

Em producao, microservicos se registram no Eureka/Consul e se descobrem
automaticamente. Aqui o endereco e fixo.

### Sem circuit breaker

Resilience4J (ou Hystrix) para evitar cascata de falhas.
Nao implementado neste modulo basico.

### Sem distributed tracing

Sleuth + Zipkin para rastrear requisicoes entre servicos.
Nao implementado — sera abordado no modulo 11.

### Banco unico

Cada microservico idealmente tem seu proprio banco de dados.
Usamos H2 em memoria no mesmo processo.

## 7. Peca de museu

Spring Boot representa a "democratizacao" do desenvolvimento Java enterprise.
O que exigia 7 arquivos no EJB, 5 no Spring MVC tradicional, agora cabe
em uma unica classe com `@SpringBootApplication`.

Mas este modulo tambem mostra o paradoxo: a simplicidade do Spring Boot
esconde a complexidade que ele gerencia. O desenvolvedor que nunca configurou
um `web.xml` ou um `beans.xml` nao aprecia o valor do que o Spring Boot faz.
Entender os modulos 07 e 09 e essencial para valorizar o modulo 10.

## 8. Como executar

**Pre-requisito:** JDK 8+ + Maven 3.6+

```bash
cd 10-spring-boot-microservice
mvn spring-boot:run
# Acessar: http://localhost:8080/api/contas
# H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:contasdb)
# Health: http://localhost:8080/actuator/health
```

Testar a API:
```bash
curl http://localhost:8080/api/contas
curl -X POST -H "Content-Type: application/json" \
  -d '{"descricao":"Aluguel","valor":2500.00,"tipo":"PAGAR","vencimento":"2024-01-15"}' \
  http://localhost:8080/api/contas
```

## 9. O que essa era resolveu

Comparado ao Spring MVC (modulo 09):
- Zero configuracao XML (`@SpringBootApplication` substitui tudo)
- Servidor web embutido (sem Tomcat externo)
- Auto-configuration (DataSource, JPA, Jackson automaticos)
- Fat JAR executavel (`java -jar app.jar`)
- Starters (uma dependencia = stack completa)
- Actuator pronto (health, metrics)
- Application.properties/yml (configuracao simplificada)

## 10. O que essa era ainda nao resolvia

- Sem observabilidade nativa para cloud (logs estruturados, tracing)
- Sem configuracao cloud-native (health probes, readiness)
- Sem gerenciamento de configuracao externa (Config Server)
- Sem service discovery
- Sem circuit breaker
- Nao otimizado para containers (startup time, memory footprint)

## 11. Evolucao posterior

| Problema | Solucao |
|----------|---------|
| Logs sem estrutura | **Logback + JSON layout / Loki** |
| Sem metricas nativas | **Micrometer + Prometheus (Spring Boot 2.x)** |
| Health check basico | **Liveness + Readiness probes (Spring Boot 2.3+)** |
| Startup lento | **Spring Native / GraalVM (2021+)** |
| Configuracao repetida entre servicos | **Spring Cloud Config** |
| Sem tracing distribuido | **Spring Cloud Sleuth + Zipkin** |
| Fragil em cascata | **Resilience4J circuit breaker** |

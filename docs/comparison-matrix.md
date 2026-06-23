# Comparison Matrix — Java Through the Ages

Matriz comparativa dos 12 modulos atraves de dimensoes tecnicas e historicas.

---

## Visao geral

| Dimensao | 01 Applet | 02 Swing | 03 RMI | 04 Servlet | 05 EJB | 06 Java ME | 07 Spring XML | 08 Java 5 | 09 Spring MVC | 10 Spring Boot | 11 Cloud Native | 12 Modern Java |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Era** | 1995–97 | 1998–02 | 1997–00 | 1999–03 | 2001–05 | 2003–08 | 2004–08 | 2004–06 | 2008–12 | 2014–17 | 2018–21 | 2021+ |
| **JDK** | 1.0/1.1 | 1.2+ | 1.1+ | 1.3/1.4 | 1.4 | CLDC | 5+ | 5 | 6+ | 8+ | 11+ | 17+ |
| **Paradigma** | OO+evento | OO+EDT | Objetos distr. | MVC manual | Container | OO restrito | IoC+DI | OO tipado | REST | Microservico | Cloud native | Funcional+OO |
| **Build** | javac+jar | javac+jar | javac+rmic+jar | javac+war | Ant+XML | preverify+jar | Ant+Ivy | javac+jar | Maven | Maven/Gradle | Maven/Gradle | Maven/Gradle |
| **Deploy** | HTML+plugin | Executavel | rmiregistry | Tomcat | App server | JAD+JAR | JAR+classpath | JAR | WAR | Fat JAR | Docker/K8s | JAR |

## Problema resolvido

| Modulo | Problema |
|--------|----------|
| 01 | Colocar aplicacoes no navegador sem JavaScript |
| 02 | Desktop corporativo multiplataforma |
| 03 | Chamada remota entre JVMs como se fosse local |
| 04 | Aplicacoes web internas sem frameworks |
| 05 | Transacoes distribuidas e seguranca declarativa |
| 06 | Apps em dispositivos com 128 KB de RAM |
| 07 | Reduzir complexidade do EJB com POJOs |
| 08 | Codigo mais seguro e expressivo |
| 09 | APIs HTTP/JSON para integracao |
| 10 | Setup minimo para microservicos |
| 11 | Operabilidade em ambientes distribudos |
| 12 | Linguagem menos verbosa sem perder tipagem |

## Persistencia

| Modulo | Mecanismo | Escopo |
|--------|-----------|--------|
| 01 | Memoria (instancia) | Sessao do browser |
| 02 | Serializacao em arquivo | Disco local |
| 03 | Memoria no servidor RMI | Sessao do servidor |
| 04 | JDBC + HSQLDB | Banco em memoria |
| 05 | Simulada (documental) | N/A |
| 06 | RecordStore (RMS) | Dispositivo |
| 07 | JDBC via JdbcTemplate | Banco relacional |
| 08 | Arquivo + processamento | Memoria + disco |
| 09 | Mock/repositorio em memoria | Memoria |
| 10 | JPA + H2 | Banco embutido |
| 11 | JPA + H2 + fila simulada | Banco + fila |
| 12 | N/A (regras em memoria) | Memoria |

## Interface

| Modulo | UI | Paradigma |
|--------|-----|-----------|
| 01 | AWT nativo | Imperativa, delegacao a SO |
| 02 | Swing | Imperativa, Look and Feel |
| 03 | Cliente texto | Console |
| 04 | HTML (JSP) | Servidor renderiza |
| 05 | N/A (documental) | N/A |
| 06 | LCDUI (MIDP) | Comandos + telas |
| 07 | Console + servico | Texto |
| 08 | Console | Texto |
| 09 | JSON (REST) | Sem UI |
| 10 | JSON (REST) | Sem UI |
| 11 | JSON + Actuator | Observabilidade |
| 12 | Console | Regras + resultados |

## Complexidade de setup

```
01 Applet    ██
02 Swing     ██
03 RMI       ███
04 Servlet   ████
05 EJB       ██████ (documental)
06 Java ME   █████
07 Spring XML ████
08 Java 5    ██
09 Spring MVC █████
10 Spring Boot ██
11 Cloud Nat. ███
12 Modern J.  ██
```

O modulo 05 (EJB) e o mais complexo em setup real, por isso e tratado como documental/simulado. O modulo 10 (Spring Boot) mostra a reducao drastica de configuracao comparado aos modulos anteriores.

## Verbosidade relativa do codigo

```
01 Applet    ████████
02 Swing     ██████████
03 RMI       ██████
04 Servlet   █████████
05 EJB       ██████████████ (boilerplate extremo)
06 Java ME   ██████
07 Spring XML ███████
08 Java 5    █████
09 Spring MVC █████
10 Spring Boot ████
11 Cloud Nat. █████
12 Modern J.  ███
```

Java 5+ reduziu significativamente a verbosidade com generics, for-each e annotations. Java moderno (12) atinge o minimo historico com records, sealed classes e pattern matching.

# Linha do tempo: Java Through the Ages

Uma visão cronológica dos principais marcos da plataforma Java e de onde
cada módulo deste repositório se posiciona historicamente.

---

## 1991–1995 — Origem

| Ano | Marco |
|---|---|
| 1991 | Projeto "Oak" iniciado na Sun Microsystems por James Gosling |
| 1995 | Java 1.0 anunciado publicamente na SunWorld; promessa "Write Once, Run Anywhere" |
| 1995 | Netscape anuncia suporte a Java no Navigator 2.0 — Applets ganham visibilidade |

---

## 1996–1997 — JDK 1.0 / 1.1: os primórdios

| Ano | Marco |
|---|---|
| 1996 | **JDK 1.0** lançado — AWT, Applets, I/O básico |
| 1996 | `java.applet.Applet` é a interface padrão para UIs no navegador |
| 1997 | **JDK 1.1** — inner classes, JavaBeans, JDBC 1.0, **RMI**, serialização |
| 1997 | `rmic` e `rmiregistry` tornam possível distribuição de objetos entre JVMs |

> 📦 **Módulo 01 — Applet/AWT** representa este período (JDK 1.0/1.1, ~1996)
> 📦 **Módulo 03 — RMI** representa este período (JDK 1.1, ~1997)

---

## 1998–2000 — Java 2 e a era desktop

| Ano | Marco |
|---|---|
| 1998 | **Java 2 (JDK 1.2)** — Collections framework, **Swing**, JIT compiler |
| 1998 | Swing substitui AWT como toolkit padrão para UIs desktop ricas |
| 1999 | **J2EE 1.2** — Servlets 2.2, JSP 1.1, EJB 1.1, JNDI |
| 1999 | Java dividido em J2SE, J2EE e **J2ME** |
| 2000 | **JDK 1.3** — JNDI embutido, Java Sound, melhorias de performance |

> 📦 **Módulo 02 — Swing Desktop** representa este período (JDK 1.2+, ~1998)

---

## 2001–2004 — J2EE e a ascensão do Java web

| Ano | Marco |
|---|---|
| 2001 | **J2EE 1.3** — Servlets 2.3, JSP 1.2, EJB 2.0, Connector Architecture |
| 2002 | **JDK 1.4** — assert, NIO, logging API, XSLT |
| 2002 | Struts 1.x ganha adoção massiva como framework MVC sobre Servlets/JSP |
| 2003 | **Spring Framework 1.0** lançado por Rod Johnson — IoC e DI como alternativa ao EJB |
| 2004 | **J2EE 1.4** — Web Services (JAX-RPC), deployment descriptors mais complexos |
| 2004 | **JDK 5 (Tiger)** — Generics, autoboxing, enums, varargs, anotações, for-each |

> 📦 **Módulo 04 — Servlet/JSP/JDBC** representa este período (J2EE 1.2/1.3, ~2001)

---

## 2004–2008 — Java ME, mobile e a fragmentação

| Ano | Marco |
|---|---|
| 2004 | **MIDP 2.0 / CLDC 1.1** consolidados — padrão para celulares com Java |
| 2005 | Java ME atinge pico: mais de 1 bilhão de dispositivos com suporte |
| 2005 | Nokia, Motorola, Sony Ericsson distribuem jogos e apps como JARs MIDP |
| 2006 | Sun anuncia abertura do código do Java (futura base do OpenJDK) |
| 2006 | **Java EE 5** — EJB 3.0, JPA 1.0, JAX-WS; anotações reduzem XML |
| 2007 | **iPhone** lançado — sem suporte a Java ME; início do declínio do MIDP |
| 2008 | **Android** lançado (Dalvik VM) — usa Java mas não é J2ME nem J2SE padrão |

> 📦 **Módulo 05 — Java ME** representa este período (MIDP 2.0, CLDC 1.1, ~2005)

---

## 2009–2014 — Consolidação e modernização

| Ano | Marco |
|---|---|
| 2009 | Oracle adquire Sun Microsystems |
| 2011 | **Java SE 7** — diamond operator, try-with-resources, NIO.2, fork/join |
| 2013 | **Java EE 7** — WebSockets, JSON-P, melhorias em JPA e CDI |
| 2013 | **Spring Boot 1.0** em preview — "convention over configuration" |
| 2014 | **Java SE 8** — Lambdas, Streams, Optional, nova Date/Time API |
| 2014 | **Spring Boot 1.0 GA** — auto-configuração, fat JARs, sem WAR |

---

## 2017–hoje — Java moderno

| Ano | Marco |
|---|---|
| 2017 | **Java SE 9** — módulos (Project Jigsaw), JShell |
| 2017 | Ciclo de releases semestrais (LTS a cada 3 anos) |
| 2018 | **Java SE 11 (LTS)** — HTTP Client, remoção de Applets e JavaFX do JDK |
| 2021 | **Java SE 17 (LTS)** — Records, Sealed Classes, Pattern Matching |
| 2023 | **Java SE 21 (LTS)** — Virtual Threads (Project Loom), Sequenced Collections |
| 2025 | **Java SE 25 (LTS)** — Project Valhalla (value types) em preview |

---

## Posição dos módulos na linha do tempo

```
1995   1996   1997   1998   1999   2000   2001   2002   2003   2004   2005   2006
  |      |      |      |      |      |      |      |      |      |      |      |
  |   [01-applet-awt]
  |          [03-rmi]
  |                 [02-swing-desktop]
  |                                        [04-servlet-jsp-jdbc]
  |                                                                   [05-javame]
```

Os módulos não cobrem a era pós-2006 intencionalmente — essa é a fronteira onde
Spring Boot, JPA e o Java moderno (8+) assumem, e onde outros materiais didáticos
já existem em abundância. O foco deste repositório é o período onde **não havia
atalhos**: cada tecnologia exigia conhecimento explícito da plataforma.

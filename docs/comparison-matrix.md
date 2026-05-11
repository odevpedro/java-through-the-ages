# Matriz de comparação: Java Through the Ages

Tabela comparativa dos cinco módulos ao longo de dimensões técnicas e operacionais.
Use esta matriz para orientar discussões sobre a evolução da plataforma.

---

## Visão geral rápida

| Dimensão | 01 Applet/AWT | 02 Swing | 03 RMI | 04 Servlet/JSP/JDBC | 05 Java ME |
|---|---|---|---|---|---|
| **Era** | ~1996 | ~1998 | ~1997 | ~2001 | ~2005 |
| **JDK** | 1.0 / 1.1 | 1.2+ | 1.1+ | 1.3 / 1.4 | CLDC 1.1 / MIDP 2.0 |
| **Paradigma** | OO + evento | OO + evento | OO distribuído | Camadas (MVC manual) | OO restrito |
| **Plataforma-alvo** | Navegador (plugin) | Desktop (SO host) | Rede local (JVM-to-JVM) | Servidor web | Dispositivo móvel |
| **Build** | `javac` + `jar` | `javac` + `jar` | `javac` + `rmic` + `jar` | `javac` + `jar` (WAR manual) | `javac` + `preverify` + `jar` |
| **Deploy** | `<applet>` tag no HTML | Executável local | `rmiregistry` + servidor | Tomcat / container J2EE | `.jad` + `.jar` no dispositivo |

---

## Persistência

| Módulo | Mecanismo | Durabilidade | Complexidade |
|---|---|---|---|
| 01 Applet/AWT | Memória (lista em variável de instância) | Perdida ao fechar o browser | Nenhuma |
| 02 Swing | Arquivo texto (serialização ou `.dat`) | Persiste entre execuções | Baixa |
| 03 RMI | Memória no servidor remoto | Perdida ao parar o servidor | Baixa |
| 04 Servlet/JSP/JDBC | Banco relacional via JDBC puro (`DriverManager`) | Total | Alta (SQL manual, `ResultSet`, `PreparedStatement`) |
| 05 Java ME | `RecordStore` (RMS) | Persiste no dispositivo | Média (API proprietária, tamanho limitado) |

---

## Interface com o usuário

| Módulo | Tecnologia de UI | Modelo de eventos | Portabilidade visual |
|---|---|---|---|
| 01 Applet/AWT | `java.awt` (componentes nativos do SO) | `ActionListener`, `MouseListener` via `AWTEvent` | Aparência varia por SO |
| 02 Swing | `javax.swing` (componentes em Java puro) | EDT (Event Dispatch Thread), `EventQueue` | Consistente entre SOs; Look and Feel configurável |
| 03 RMI | Nenhuma (protocolo de rede) | N/A | N/A |
| 04 Servlet/JSP/JDBC | HTML gerado pelo servidor (JSP + JSTL) | HTTP request/response (sem estado) | Depende do browser |
| 05 Java ME | `javax.microedition.lcdui` (`Form`, `List`, `Canvas`) | `CommandListener` | Varia fortemente por dispositivo/fabricante |

---

## Comunicação e rede

| Módulo | Protocolo | Estilo | Descoberta de serviço |
|---|---|---|---|
| 01 Applet/AWT | Nenhum (local) | — | — |
| 02 Swing | Nenhum (local) | — | — |
| 03 RMI | JRMP (Java Remote Method Protocol) | RPC síncrono | `rmiregistry` (porta 1099) |
| 04 Servlet/JSP/JDBC | HTTP/1.0–1.1 | Request/response stateless | URL direta |
| 05 Java ME | HTTP ou sockets (via `javax.microedition.io`) | Limitado por operadora/dispositivo | Manual |

---

## Segurança

| Módulo | Modelo | Principal risco da época |
|---|---|---|
| 01 Applet/AWT | Sandbox do browser (SecurityManager) | Escape de sandbox; acesso ao sistema de arquivos |
| 02 Swing | Processo local sem sandbox | Sem isolamento; executa com permissões do usuário |
| 03 RMI | Sem autenticação padrão | Deserialização não segura; `codebase` remoto arbitrário |
| 04 Servlet/JSP/JDBC | Dependente do container | SQL injection; Session fixation; XSS via JSP |
| 05 Java ME | Sandbox MIDP (domínios de proteção) | Assinatura digital fraca; acesso à rede controlado por operadora |

---

## Verbosidade e fricção operacional

| Dimensão | 01 Applet/AWT | 02 Swing | 03 RMI | 04 Servlet/JSP/JDBC | 05 Java ME |
|---|---|---|---|---|---|
| Linhas de código (domínio) | ~150 | ~250 | ~200 + interfaces | ~300 + JSPs | ~200 |
| Arquivos de configuração | 1 HTML | Nenhum | 2 scripts de start | `web.xml` + `server.xml` | `.jad` |
| Passos para executar | 3 (compile, html, browser) | 2 (compile, run) | 5 (compile, rmic, registry, server, client) | 4 (compile, war, deploy, tomcat) | 4 (compile, preverify, jar, emulator) |
| Ferramental externo | `appletviewer` ou browser com plugin | Nenhum | `rmiregistry` | Container J2EE (Tomcat) | WTK / emulador MIDP |
| Diagnóstico de erros | Difícil (console do browser) | Fácil (stderr local) | Difícil (stack remoto) | Médio (logs do container) | Muito difícil (display limitado) |

---

## O que cada módulo ensina de único

| Módulo | Lição principal |
|---|---|
| 01 Applet/AWT | Como a ideia de "aplicação no browser" surgiu antes de HTML/CSS serem suficientes. Por que o modelo falhou. |
| 02 Swing | O custo de gerenciar UI complexa manualmente. A importância do EDT e os bugs que aparecem quando ele é ignorado. |
| 03 RMI | O que é transparência de localização e por que ela é uma ilusão perigosa. Latência de rede ≠ chamada local. |
| 04 Servlet/JSP/JDBC | Como o padrão MVC emergiu de necessidade, não de elegância. O que frameworks como Struts e Spring vieram resolver. |
| 05 Java ME | Como programar quando memória, CPU e API são escassos. Por que o Android ganhou: developer experience muito superior. |

---

## Resumo: complexidade acumulada por era

```
Complexidade
de setup
    ▲
    │                              ████ 04 Servlet
    │                    ███       ████
    │          ██        ███       ████ ████
    │  ██      ██   ███  ███       ████ ████
    │  ██      ██   ███  ███  ██   ████ ████
    └──────────────────────────────────────────▶ módulo
       01      02   03   04   05
```

O módulo 04 (Servlet/JSP/JDBC) representa o pico de complexidade configuracional
antes da chegada de frameworks que abstraíam essas camadas.

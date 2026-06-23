# Modulo 04 — Servlet JSP JDBC Intranet

> Era: 1999–2003 · J2EE: 1.2/1.3 · Paradigma: camadas MVC manual

## 1. Contexto historico
Servlet 2.2/2.3 era, JSP 1.1/1.2. Tomcat lancado pela Apache em 1999. Trio Servlet + JSP + JDBC = base do Java web pre-frameworks.

## 2. Problema historico
Empresas migrando apps desktop para web/intranet. Atualizar servidor = todos veem nova versao. Nao havia frameworks — era Servlet puro, JDBC puro, JSP com scriptlets.

## 3. Aplicacao: Sistema de chamados internos
Employees can open support tickets, list all tickets, view details. Uses: JSP form, Servlet controller, JDBC to HSQLDB (in-memory), HTTP sessions.

## 4. Recursos do Java/J2EE

| Recurso | Uso |
|---------|-----|
| `HttpServlet` | Controller da aplicacao (doGet/doPost) |
| `ServletContextListener` | Inicializacao do banco na subida do servidor |
| `RequestDispatcher` | Encaminhamento para JSP (forward) |
| `HttpServletResponse.sendRedirect` | Redirect apos POST (evita duplo submit) |
| JSP scriptlets | Logica de apresentacao na view |
| `web.xml` | Mapeamento de servlets e listeners |
| `DriverManager` | Conexao JDBC direta (sem pool) |
| `PreparedStatement` | SQL parametrizado (previne SQL injection) |

## 5. Arquitetura

```
Browser --> Tomcat --> ChamadoServlet (doGet/doPost)
                           |
                           v
                      ChamadoDao (JDBC)
                           |
                           v
                       HSQLDB (memoria)
                           |
                      JSP views (forward/redirect)
```

## 6. Limitacoes
- web.xml verboso e conflituoso
- Scriptlets misturam Java e HTML
- JDBC sem pool (uma conexao por request)
- Encoding problem (UTF-8 config em 3 lugares)
- POST sem redirect causa duplo submit

## 7. Peca de museu
This module shows the raw, unfiltered Java web experience. Every Servlet mapped in XML. Every SQL query manual. Every JSP filled with Java code. This is WHY frameworks like Struts, Spring MVC, and later Spring Boot were created.

## 8. Build e execucao
Requires JDK 8 + Tomcat + HSQLDB (download-deps.sh baixa automaticamente)

## 9. Evolucao posterior
@WebServlet annotation, JSTL+EL, Connection Pool, Hibernate, Spring, Spring Boot

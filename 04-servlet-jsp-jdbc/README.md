# Módulo 04 — Servlet/JSP/JDBC

> **Era:** início dos anos 2000 · **J2EE:** 1.2/1.3 · **Servlet:** 2.3 · **JSP:** 1.2
> **Paradigma:** camadas MVC manual (sem framework)

---

## 1. Contexto histórico

No início dos anos 2000, o Java web estava em sua fase de maior crescimento e
maior complexidade operacional. A plataforma J2EE prometia ser a resposta corporativa
para tudo: distribuição, persistência, mensageria, segurança e transações declarativas.
O preço era uma pilha de tecnologias interconectadas, cada uma com sua própria curva
de aprendizado e seus próprios arquivos de configuração XML.

O trio Servlet + JSP + JDBC representava a camada mais acessível dessa plataforma:
sem EJBs, sem JNDI obrigatório, sem servidor de aplicação completo. Bastava um
container Servlet como o Tomcat — gratuito, open source, lançado pela Apache em 1999.

Esta combinação foi o alicerce sobre o qual frameworks como **Struts** (2000),
**Spring MVC** (2003) e **JSF** (2004) foram construídos. Entender Servlet/JSP puro
é entender o que esses frameworks abstraem — e por que eles foram necessários.

---

## 2. Cenário de uso real

Aplicações Servlet/JSP eram usadas em:

- **Portais corporativos internos** — intranets com listagens, formulários, relatórios.
- **E-commerce** — lojas virtuais de primeira geração (Amazon usava Java extensivamente).
- **Sistemas bancários web** — extrato, transferência, pagamento de contas.
- **ERPs e CRMs web** — migração de sistemas desktop para browser nos anos 2000.
- **Governos e instituições públicas** — sistemas de declaração de imposto, certidões.

O modelo era: servidor processa tudo, cliente recebe HTML estático. JavaScript era
usado com moderação (validação de formulário, masks). AJAX só seria popularizado em
2005; até lá, toda interação exigia um round-trip completo ao servidor.

---

## 3. Recursos do Java/J2EE relevantes para a época

| Recurso | Versão | Relevância neste módulo |
|---|---|---|
| `HttpServlet` (doGet/doPost) | Servlet 2.2 (1998) | Controlador central; ciclo de vida gerenciado pelo container |
| `ServletContextListener` | Servlet 2.3 (2001) | Inicialização do banco no startup da aplicação |
| `RequestDispatcher.forward()` | Servlet 2.2 | Transferência de controle Servlet → JSP |
| `HttpServletResponse.sendRedirect()` | Servlet 2.1 | Post-Redirect-Get para evitar duplicação |
| `request.setAttribute/getAttribute()` | Servlet 2.0 | Canal de comunicação Controller → View |
| JSP Scriptlets (`<% %>`, `<%= %>`) | JSP 1.0 | Java embutido em HTML; problemático mas onipresente |
| `web.xml` (Deployment Descriptor) | Servlet 2.2 | Configuração de Servlets, listeners, filtros, erros |
| `DriverManager.getConnection()` | JDBC 1.0 | Conexão direta sem pool; simples e frágil |
| `PreparedStatement` | JDBC 1.0 | Consultas parametrizadas; proteção contra SQL Injection |
| `Statement.RETURN_GENERATED_KEYS` | JDBC 3.0 / J2SE 1.4 | Recuperar ID auto-gerado após INSERT |
| HSQLDB embutido | — | Banco in-memory para demo; sem necessidade de servidor externo |

---

## 4. Arquitetura e design

```
Browser (HTTP)
     │
     │  GET /mensagens          POST /mensagens
     ▼                               │
┌─── Tomcat (container Servlet) ─────────────────────────────────────┐
│                                                                     │
│  InicializadorListener.contextInitialized()                         │
│    └── ConexaoFactory.inicializar()  ← DDL + HSQLDB na inicialização│
│                                                                     │
│  MensagemServlet.doGet()           MensagemServlet.doPost()         │
│    ├── dao.listar()                  ├── validar campos             │
│    ├── request.setAttribute(         ├── dao.inserir()              │
│    │     "mensagens", lista)         └── sendRedirect("/mensagens") │
│    └── forward → listar.jsp                                         │
│                                                                     │
│  MensagemDao                                                        │
│    ├── inserir()  → PreparedStatement INSERT                        │
│    ├── listar()   → PreparedStatement SELECT + ResultSet mapping    │
│    ├── remover()  → PreparedStatement DELETE                        │
│    └── contar()   → PreparedStatement SELECT COUNT(*)               │
│                                                                     │
│  ConexaoFactory                                                     │
│    └── DriverManager.getConnection("jdbc:hsqldb:mem:mensagensdb")   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
                    │
               HSQLDB (in-memory)
               tabela: mensagens
```

**Separação de camadas — emergente e incompleta:**

| Camada | Classe | Responsabilidade |
|---|---|---|
| Controller | `MensagemServlet` | Recebe HTTP, valida, chama DAO, faz forward/redirect |
| View | `listar.jsp` | Renderiza HTML com scriptlets Java |
| Model / DAO | `MensagemDao` | Operações JDBC; isolamento do SQL |
| Infraestrutura | `ConexaoFactory`, `InicializadorListener` | Setup do banco |
| Domain | `Mensagem` | POJO de domínio |

O MVC está presente em estrutura, mas com acoplamentos: o Servlet conhece o DAO
diretamente (sem interface, sem IoC), e a JSP acessa os atributos do request
por nome de string (sem tipagem — `(List) request.getAttribute("mensagens")`).

---

## 5. Limitações e dificuldades

### web.xml: o arquivo de todos os conflitos

Cada Servlet, filtro, listener e mapeamento de URL precisava ser declarado no
`web.xml`. Em times com 10 desenvolvedores, cada um adicionando features:
- Merges conflitantes no CVS/SVN eram rotineiros
- Ordem dos elementos era obrigatória (validada pelo DTD); erros quebravam o deploy
- Não havia validação em tempo de desenvolvimento — erros apareciam apenas ao subir o Tomcat

A Servlet 3.0 (2009) resolveu com `@WebServlet`, `@WebFilter`, `@WebListener`.

### Scriptlets JSP: mistura de responsabilidades

```jsp
<% List mensagens = (List) request.getAttribute("mensagens");
   for (int i = 0; i < mensagens.size(); i++) {
       Mensagem m = (Mensagem) mensagens.get(i); %>
<tr><td><%= m.getAutor() %></td></tr>
<% } %>
```

Esta mistura tornava JSPs imensos, difíceis de testar e impossíveis de reusar.
A alternativa (JSTL + EL) era disponível mas menos adotada em 2001.
O JSP 2.0 (2003) padronizou a Expression Language (`${mensagens}`), mas
o legado de scriptlets persistiu por décadas em sistemas em produção.

### JDBC sem pool: uma conexão por request

`DriverManager.getConnection()` por request significava:
- Uma nova conexão TCP ao banco a cada requisição HTTP
- Custo de handshake + autenticação a cada chamada
- Risco de esgotar o limite de conexões do banco sob carga

Em produção, a solução era um connection pool (Apache DBCP, C3P0) configurado
via JNDI no Tomcat. Mas configurar JNDI exigia editar `server.xml` e `context.xml`
do Tomcat, adicionar JARs ao `$TOMCAT_HOME/lib`, e entender o classloader hierarchy
do container — uma curva de aprendizado significativa.

### Encoding: o pesadelo do charset

Sem `request.setCharacterEncoding("UTF-8")` antes do primeiro `getParameter()`,
caracteres como `ã`, `ç`, `é` chegavam corrompidos. O Tomcat processava o corpo
do POST com ISO-8859-1 por padrão.

Além disso, o response precisava de `response.setContentType("text/html; charset=UTF-8")`
(feito pelo `<%@ page contentType="..." %>` na JSP). E o HTML precisava de
`<meta http-equiv="Content-Type" ...>`. Três camadas de configuração de encoding,
cada uma em um lugar diferente.

### POST-Redirect-GET: padrão sem nome, mas essencial

Submeter um formulário via POST e responder com `forward()` para uma JSP causava
o problema do "duplo POST": F5 no browser resultava em nova submissão.
`sendRedirect()` após o POST resolve isso, mas a mensagem de feedback precisava
de um mecanismo diferente para sobreviver ao redirect (session attribute ou
query parameter). Frameworks como Spring MVC e Rails codificaram esse padrão
com "flash messages" integradas.

---

## 6. Build e execução

### Pré-requisitos

- **JDK 8+** — `java`, `javac`, `jar` no PATH
- **Apache Tomcat 8.x ou 9.x** — [https://tomcat.apache.org](https://tomcat.apache.org)
- **Dependências em `lib/`**

### Passo 1: baixar dependências

```bash
# Opção A: script automatizado (requer curl + internet)
chmod +x download-deps.sh
sh download-deps.sh

# Opção B: manual
# 1. Baixe hsqldb-2.5.2.jar de https://hsqldb.org -> salve como lib/hsqldb.jar
# 2. Copie servlet-api.jar do Tomcat:
cp $TOMCAT_HOME/lib/servlet-api.jar lib/servlet-api.jar
```

### Passo 2: compilar e empacotar

```bash
# Linux/macOS
chmod +x build.sh
sh build.sh

# Windows
build.bat
```

### Passo 3: deploy no Tomcat

```bash
# Copiar WAR para o Tomcat (autodeploy)
cp mensagens.war $TOMCAT_HOME/webapps/

# Iniciar Tomcat (se não estiver rodando)
$TOMCAT_HOME/bin/startup.sh

# Verificar logs
tail -f $TOMCAT_HOME/logs/catalina.out
```

### Passo 4: acessar no browser

```
http://localhost:8080/mensagens/
```

O `InicializadorListener` cria a tabela automaticamente na inicialização.
Não há migração manual de banco necessária.

### Estrutura do WAR gerado

```
mensagens.war
├── WEB-INF/
│   ├── web.xml                         ← deployment descriptor
│   ├── classes/
│   │   └── mensagens/
│   │       ├── Mensagem.class
│   │       ├── ConexaoFactory.class
│   │       ├── MensagemDao.class
│   │       ├── InicializadorListener.class
│   │       ├── MensagemServlet.class
│   │       └── RedirectServlet.class
│   ├── lib/
│   │   └── hsqldb.jar
│   └── views/
│       ├── listar.jsp
│       └── erro.jsp
```

### Nota: dados em memória

O banco HSQLDB está configurado em modo `mem:` (in-memory). Os dados são
**perdidos ao reiniciar o Tomcat**. Para persistência em arquivo, altere
a URL em `ConexaoFactory.java`:
```java
// in-memory (padrão deste módulo):
private static final String URL = "jdbc:hsqldb:mem:mensagensdb";

// arquivo (persistente):
private static final String URL = "jdbc:hsqldb:file:/tmp/mensagensdb";
```

---

## 7. Evolução posterior

| Problema | Solução que veio depois |
|---|---|
| `web.xml` obrigatório e conflituoso | **`@WebServlet` / `@WebFilter` (Servlet 3.0, 2009)** |
| Scriptlets JSP misturando Java e HTML | **JSTL + EL (JSP 2.0, 2003)**; depois **Thymeleaf**, **FreeMarker** |
| `DriverManager` sem pool | **Apache DBCP** / **C3P0** (2001+); **HikariCP** (2013) como padrão moderno |
| SQL manual + ResultSet mapping | **Hibernate (2002)**, **JPA (2006)**, **Spring Data JPA (2011)** |
| `request.setCharacterEncoding()` manual | **`CharacterEncodingFilter`** do Spring; Servlet 3.1 define UTF-8 como default |
| Sem injeção de dependência | **Spring Framework 1.0 (2003)** — IoC elimina `new MensagemDao()` no Servlet |
| POST sem redirect causa duplo submit | **Post-Redirect-Get** codificado pelos frameworks; Flash attributes no Spring MVC |
| Deploy em container externo (Tomcat) | **Spring Boot (2014)** embute Tomcat no JAR executável — `java -jar app.jar` |
| Configuração JNDI para DataSource | **`application.properties` (Spring Boot)** — 3 linhas substituem 30 de XML |

O módulo 04 é onde o custo do Java web "na mão" fica mais evidente: 6 classes Java,
1 JSP, 1 `web.xml` verboso, 2 scripts de build, e ainda é necessário um Tomcat
externo para rodar. O Spring Boot resolveu tudo isso em 2014 — e o módulo 04
é a razão pela qual a comunidade Java recebeu o Spring Boot com tanto entusiasmo.

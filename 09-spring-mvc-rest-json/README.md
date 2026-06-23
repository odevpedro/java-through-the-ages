# Modulo 09 — Spring MVC REST JSON

> **Era:** 2008–2012 · **JDK:** 6+ · **Paradigma:** REST, APIs JSON

---

## 1. Contexto historico

Em 2007-2008, o cenario de desenvolvimento web comecou a mudar drasticamente:

- **jQuery** (2006) popularizou AJAX e consumo de APIs JSON no browser
- **iPhone** (2007) exigiu dados de servidor para aplicativos nativos
- **Android** (2008) precisava de APIs REST para comunicacao
- **Single Page Applications** estavam emergindo (AngularJS 2010, React 2013)

Antes disso, aplicacoes Java web geravam HTML no servidor e enviavam pronto
para o browser (JSP, JSF, Struts). O front-end nao consumia dados — consumia
paginas prontas.

Spring 3.0 (2009) introduziu suporte nativo a REST: `@Controller` com
`@ResponseBody`, `@PathVariable`, `@RequestBody`, e integracao com Jackson
para serializacao JSON. Foi o reconhecimento oficial de que o Java web
precisava servir dados, nao apenas paginas.

## 2. Problema historico

Aplicacoes comecaram a precisar expor APIs HTTP para:
- Frontends JavaScript consumirem dados sem recarregar pagina
- Aplicativos mobile (iOS, Android) se comunicarem com o servidor
- Integracao entre sistemas corporativos
- Parceiros e terceiros acessarem funcionalidades

O modelo JSP nao atendia: o que o cliente queria era JSON, nao HTML renderizado.
SOAP era complexo demais para comunicacoes simples.

## 3. Aplicacao: API REST de catalogo de produtos

Uma API RESTful para gerenciamento de produtos:
- `GET /api/produtos` — listar todos
- `GET /api/produtos/{id}` — obter por ID
- `POST /api/produtos` — criar novo
- `PUT /api/produtos/{id}` — atualizar
- `DELETE /api/produtos/{id}` — remover

Configuracao manual: `web.xml` + `spring-servlet.xml` + XML de componentes.
Sem Spring Boot — deploy em Tomcat externo como WAR.

## 4. Recursos do Spring 3.x relevantes

| Recurso | Relevancia neste modulo |
|---------|------------------------|
| `@Controller` | Define um controller MVC |
| `@RequestMapping("/api/produtos")` | Mapeamento de rota no tipo/metodo |
| `@ResponseBody` | Resposta serializada diretamente (sem view resolver) |
| `@RequestBody` | Corpo da requisicao desserializado do JSON |
| `@PathVariable("{id}")` | Variavel de path da URL |
| `@RequestParam` | Parametro de query string |
| `MappingJacksonHttpMessageConverter` | Conversao JSON <-> objeto |
| `HttpMethod` / `RequestMethod` | GET, POST, PUT, DELETE |
| `ResponseEntity<T>` | Resposta HTTP com status code customizado |

## 5. Arquitetura e design

```
Browser / Cliente HTTP
      │
      ▼  HTTP (GET/POST/PUT/DELETE)
DispatcherServlet (web.xml → spring-servlet.xml)
      │
      ▼  HandlerMapping
ProdutoController (@Controller)
      │
      ├── GET    /api/produtos      → listar()
      ├── GET    /api/produtos/{id} → buscarPorId()
      ├── POST   /api/produtos      → criar()
      ├── PUT    /api/produtos/{id} → atualizar()
      └── DELETE /api/produtos/{id} → deletar()
      │
      ▼
ProdutoRepository (in-memory: ConcurrentHashMap)
      │
      ▼
Jackson Message Converter → JSON Response
```

**Configuracao manual (sem Spring Boot):**
```
web.xml                    — DispatcherServlet + contexto
spring-servlet.xml         — component-scan + annotation-driven + Jackson
```

## 6. Limitacoes e dificuldades

### Configuracao XML multipla

Ainda era necessario configurar manualmente:
1. `web.xml` — mapeamento do DispatcherServlet, init-param
2. `spring-servlet.xml` — component scan, annotation-driven, message converters
3. Cada dependencia externa exigia configuracao XML (ou JavaConfig emergente)

### Sem Spring Boot

- Deploy manual em Tomcat externo (WAR file)
- Sem auto-configuration (cada bean precisa ser declarado)
- Sem embedded server
- Sem actuator para monitoramento
- Sem starter dependencies

### Jackson configurado manualmente

```xml
<mvc:annotation-driven>
    <mvc:message-converters>
        <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter"/>
    </mvc:message-converters>
</mvc:annotation-driven>
```

### Sem HATEOAS

As APIs REST nao seguiam padroes como HATEOAS. Cada API inventava
sua propria convencao de nomes, estruturas de resposta e tratamentos de erro.

### Tratamento de erro manual

Sem `@ControllerAdvice` (introduzido no Spring 3.2), erros eram tratados
caso a caso em cada controller ou com handlers globais complexos.

## 7. Peca de museu

Este modulo representa o momento historico em que o Java web virou a chave:
de gerador de HTML para provedor de APIs. 

O mesmo Spring que servia JSPs agora servia JSON. A configuracao ainda era
XML-heavy, e o deploy era WAR em Tomcat externo — mas o conceito ja estava
maduro. Este e o "ultimo XML" antes do Spring Boot eliminar a configuracao.

A transicao de JSP para REST e uma das migracoes mais significativas da
historia do Java web. Este modulo captura o exato momento da transicao.

## 8. Como executar

**Pre-requisito:** JDK 8+ + Maven 3.6+ + Apache Tomcat 8+

```bash
cd 09-spring-mvc-rest-json
mvn clean package
# Copiar target/*.war para $TOMCAT_HOME/webapps/
# Iniciar Tomcat
# Acessar: http://localhost:8080/09-spring-mvc-rest-json/api/produtos
```

Para testar a API:
```bash
curl http://localhost:8080/09-spring-mvc-rest-json/api/produtos
curl -X POST -H "Content-Type: application/json" \
  -d '{"nome":"Notebook","preco":3500.00,"categoria":"Informatica"}' \
  http://localhost:8080/09-spring-mvc-rest-json/api/produtos
```

## 9. O que essa era resolveu

Comparado ao Servlet/JSP (modulo 04):
- APIs JSON nativas sem JSP
- Mapeamento automatico de parametros via anotacoes
- Serializacao/desserializacao automatica com Jackson
- Roteamento declarativo com `@RequestMapping`
- Sem `request.getParameter()` manual
- Sem `PrintWriter` para escrever resposta JSON

## 10. O que essa era ainda nao resolvia

- Configuracao XML ainda necessaria
- Deploy WAR em container externo
- Sem auto-configuration
- Sem embedded server
- Sem tratamento global de erros (ate Spring 3.2)
- Sem HATEOAS
- Sem documentacao automatica (Swagger seria 2011+)

## 11. Evolucao posterior

| Problema | Solucao |
|----------|---------|
| Configuracao XML multipla | **Spring Boot auto-configuration (2014)** |
| Deploy WAR externo | **Embedded Tomcat no Spring Boot** |
| Conversao manual de JSON | **`@EnableAutoConfiguration` + Jackson automatico** |
| Tratamento de erro verboso | **`@ControllerAdvice` + `@ExceptionHandler` (Spring 3.2)** |
| Documentacao de API | **Swagger/OpenAPI (2011+)** |
| Validacao manual | **Bean Validation (`@Valid`, `@NotBlank`, etc)** |
| Sem monitoramento | **Spring Boot Actuator (2014)** |

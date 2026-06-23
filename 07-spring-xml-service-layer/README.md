# Modulo 07 — Spring XML Service Layer

> **Era:** 2004–2008 · **JDK:** 5+ · **Paradigma:** IoC, DI, POJOs

---

## 1. Contexto historico

Em 2003, Rod Johnson lancou o livro "Expert One-on-One J2EE Design and Development",
que incluia um framework de 30.000 linhas de codigo. Esse framework se tornaria o
**Spring Framework 1.0**, lancado oficialmente em 2004.

O contexto era de saturacao com a complexidade do J2EE/EJB 2.x. Desenvolvedores
gastavam mais tempo configurando deployment descriptors, interfaces Home/Remote,
e lidando com excecoes do container do que escrevendo logica de negocio.

Spring propos tres revolucoes:
1. **POJOs (Plain Old Java Objects)** — qualquer classe Java podia ser gerenciada
   pelo container, sem estender classes especificas ou implementar interfaces
   do framework.
2. **Injecao de Dependencia (DI)** — as dependencias de um objeto sao fornecidas
   pelo container, nao criadas pelo proprio objeto. Acoplamento reduzido.
3. **Configuracao XML** — beans, propriedades e dependencias configurados em
   arquivos XML.

## 2. Problema historico

O EJB 2.x era considerado o padrao corporativo, mas sua complexidade era
cada vez mais questionada. Para implementar um simples servico de consulta:

```
EJB 2.x:        Home interface + Remote interface + Bean class + ejb-jar.xml
                + jboss.xml (ou weblogic.xml) + JNDI lookup + factory
Spring:         POJO + beans.xml
```

Empresas queriam a transacao, seguranca e gerenciamento de recursos que o EJB
prometia, mas sem o custo operacional. Spring entregou exatamente isso.

## 3. Aplicacao: Sistema de pedidos com camada de servico

Um sistema que:
- Cria pedidos (cliente, produto, quantidade, valor)
- Valida estoque (simulado)
- Confirma pagamento
- Persiste em banco HSQLDB via JdbcTemplate
- Usa transacao declarativa gerenciada pelo Spring
- Demonstra injecao de dependencia via XML

## 4. Recursos do Spring relevantes

| Recurso | Versao | Relevancia neste modulo |
|---------|--------|------------------------|
| `ApplicationContext` | 1.0 | Container IoC que gerencia todos os beans |
| `ClassPathXmlApplicationContext` | 1.0 | Carrega contexto de arquivos XML no classpath |
| beans.xml | 1.0 | Configuracao declarativa de beans e dependencias |
| `JdbcTemplate` | 1.0 | Template para JDBC; elimina try/catch/finally de conexao |
| `DataSourceTransactionManager` | 1.0 | Gerenciador de transacoes para DataSource JDBC |
| `@Transactional` | 2.0 | Anotacao para demarcacao declarativa de transacoes |
| Property injection (`<property>`) | 1.0 | Injecao de dependencias via setter |

## 5. Arquitetura e design

```
Main.main()
  └── ClassPathXmlApplicationContext("beans.xml")
        │
        ├── beans.xml
        │     ├── <bean id="dataSource" ...>         ← HSQLDB in-memory
        │     ├── <bean id="pedidoRepository" ...>    ← injeta dataSource
        │     ├── <bean id="pedidoService" ...>       ← injeta repository
        │     └── <bean id="transactionManager" ...>  ← gerenciador TX
        │
        └── Main obtem bean pedidoService
              ├── service.criarPedido(...)     ← transacao automatica
              ├── service.listarPedidos()      ← consulta
              └── service.confirmarPagamento() ← atualizacao com transacao
```

**Separacao de camadas:**
| Camada | Classe | Resposabilidade |
|--------|--------|-----------------|
| Domain | `Pedido` | Entidade de dominio |
| Repository | `PedidoRepository` | Persistencia via JdbcTemplate |
| Service | `PedidoService` | Logica de negocio e transacao |
| Config | `beans.xml` | Injecao de dependencias |
| Bootstrap | `Main` | Inicializacao do ApplicationContext |

## 6. Limitacoes e dificuldades

### XML verboso e sem validacao

Cada bean exigia uma entrada XML completa. Em projetos com centenas de beans,
o XML crescia para milhares de linhas. Nao havia auto-complete ou validacao
em tempo de desenvolvimento — erros de sintaxe no XML apareciam apenas em
runtime com `BeanCreationException`.

```xml
<bean id="pedidoService" class="pedidos.PedidoService">
    <property name="repository" ref="pedidoRepository"/>
</bean>
```

Hoje isso seria uma anotacao: `@Autowired PedidoRepository repository;`

### Classpath scanning limitado

Spring 1.x/2.x exigia declaracao explicita de cada bean no XML. Na pratica,
desenvolvedores criavam arquivos XML enormes ou dividiam em multiplos arquivos
(um por camada, um por modulo) e os importavam.

### Transaction proxies e AOP complexo

Transacoes declarativas exigiam configuracao de `TransactionProxyFactoryBean`
ou `BeanNameAutoProxyCreator` — mecanismos de AOP complexos que muitos
desenvolvedores nunca entendiam completamente.

```xml
<bean id="transactionManager" class="...DataSourceTransactionManager"/>
<bean id="pedidoServiceTarget" class="pedidos.PedidoService">
    <property name="repository" ref="pedidoRepository"/>
</bean>
<bean id="pedidoService" class="...TransactionProxyFactoryBean">
    <property name="proxyInterfaces" value="..."/>
    <property name="target" ref="pedidoServiceTarget"/>
    <property name="transactionManager" ref="transactionManager"/>
    <property name="transactionAttributes">
        <props>
            <prop key="criar*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

### Sem auto-configuration

Cada bean de infraestrutura (DataSource, TransactionManager, JdbcTemplate)
precisava ser configurado manualmente. Nao havia "starter" que configurasse
tudo automaticamente.

## 7. Peca de museu

Spring XML representa a ponte entre a complexidade do EJB 2.x e a simplicidade
do Java moderno. Cada linha de XML neste modulo explica porque `@Autowired`
e `@SpringBootApplication` foram recebidos com tanto entusiasmo pela comunidade.

O modulo mostra que a "simplicidade" e relativa a epoca: em 2004, configurar
beans em XML era visto como uma simplificacao enorme comparado ao EJB.
Hoje, olhamos para o mesmo XML e pensamos "que horror". A licao e que
cada geracao resolve as dores da anterior, mas cria novas dores que a
proxima geracao resolvera.

## 8. Como executar

**Pre-requisito:** JDK 8+ e Maven 3.6+

```bash
cd 07-spring-xml-service-layer
mvn clean compile exec:java
```

Ou manualmente:
```bash
mvn dependency:copy-dependencies -DoutputDirectory=lib
javac -cp "lib/*" -d out -sourcepath src src/pedidos/*.java
java -cp "lib/*:out" pedidos.Main
```

O modulo depende de Spring 4.3.x e HSQLDB, baixados automaticamente pelo Maven.

## 9. O que essa era resolveu

Comparado ao EJB (modulo 05):
- Eliminou interfaces Home/Remote
- Eliminou deployment descriptors complexos
- POJOs puros — sem heranca de classes do framework
- Testavel sem container (basta instanciar com new)
- JdbcTemplate eliminou try/catch/finally repetitivo

## 10. O que essa era ainda nao resolvia

- XML ainda era necessario para configuracao basica
- Classpath scanning era limitado ou inexistente
- Transacoes exigiam proxies AOP complexos
- Nao havia embedded server
- Deploy ainda era WAR em Tomcat externo
- Configuracao de DataSource ainda era manual
- Nao havia auto-configuration

## 11. Evolucao posterior

| Problema | Solucao |
|----------|---------|
| XML verboso | `@Component`, `@Service`, `@Repository` (Spring 2.5, 2007) |
| `@Autowired` manual | Spring Boot auto-configuration (2014) |
| Transacao com proxies complexos | `@Transactional` simplificado (Spring 2.5) |
| Deploy WAR externo | Spring Boot embedded Tomcat (2014) |
| Config manual de DataSource | `spring.datasource.*` em application.properties |
| Setup de projeto repetitivo | Spring Initializr (start.spring.io, 2014) |

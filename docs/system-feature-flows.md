# System Feature Flows

> Registro historico e incremental dos fluxos internos de cada modulo.
> Cada modulo implementa um dominio diferente, coerente com sua epoca.

---

## Indice

- [Modulo 01 — Applet AWT Browser Era](#modulo-01--applet-awt-browser-era)
- [Modulo 02 — Swing Desktop Backoffice](#modulo-02--swing-desktop-backoffice)
- [Modulo 03 — RMI Branch Office Distributed System](#modulo-03--rmi-branch-office-distributed-system)
- [Modulo 04 — Servlet JSP JDBC Intranet](#modulo-04--servlet-jsp-jdbc-intranet)
- [Modulo 05 — EJB Enterprise Transaction Era](#modulo-05--ejb-enterprise-transaction-era)
- [Modulo 06 — Java ME Field Service Mobile](#modulo-06--java-me-field-service-mobile)
- [Modulo 07 — Spring XML Service Layer](#modulo-07--spring-xml-service-layer)
- [Modulo 08 — Java5 Generics Annotations Concurrency](#modulo-08--java5-generics-annotations-concurrency)
- [Modulo 09 — Spring MVC REST JSON](#modulo-09--spring-mvc-rest-json)
- [Modulo 10 — Spring Boot Microservice](#modulo-10--spring-boot-microservice)
- [Modulo 11 — Cloud Native Observability Era](#modulo-11--cloud-native-observability-era)
- [Modulo 12 — Modern Java Language Evolution](#modulo-12--modern-java-language-evolution)

---

## Modulo 01 — Applet AWT Browser Era

**Dominio:** Mini catalogo interativo de produtos
**Tecnologia:** Applet + AWT
**Fluxo:** Usuario seleciona produto no Choice, digita quantidade, clica "Calcular" -> Applet calcula total e exibe no TextArea. "Adicionar" salva item no carrinho em memoria (Vector).

## Modulo 02 — Swing Desktop Backoffice

**Dominio:** Cadastro de clientes corporativo
**Tecnologia:** Swing + serializacao
**Fluxo:** Usuario preenche formulario (nome, email, telefone), clica "Salvar" -> RepositorioCliente adiciona a lista e persiste em clientes.dat. JTable atualiza automaticamente. Editar carrega dados no formulario. Excluir remove selecionado.

## Modulo 03 — RMI Branch Office Distributed System

**Dominio:** Consulta e reserva de estoque remoto
**Tecnologia:** RMI
**Fluxo:** ClienteFilial faz lookup no Estoque remoto -> lista produtos -> consulta disponibilidade por ID -> solicita reserva. Servidor manda estado centralizado.

## Modulo 04 — Servlet JSP JDBC Intranet

**Dominio:** Sistema de chamados internos
**Tecnologia:** Servlet + JSP + JDBC + HSQLDB
**Fluxo:** GET /chamados -> ChamadoServlet.doGet() -> ChamadoDao.listar() -> forward para listagem.jsp. POST /chamados -> doPost() -> valida campos -> ChamadoDao.inserir() -> redirect.

## Modulo 05 — EJB Enterprise Transaction Era

**Dominio:** Transferencia bancaria transacional
**Tecnologia:** EJB 2.x (simulado)
**Fluxo:** Simulador cria TransferenciaService -> debita conta origem -> credita conta destino. Se saldo insuficiente, excecao -> rollback simulado. Demonstra o boilerplate EJB 2.x nos comentarios.

## Modulo 06 — Java ME Field Service Mobile

**Dominio:** Vistoria tecnica offline
**Tecnologia:** MIDP 2.0 / RecordStore
**Fluxo:** TelaLista -> "Nova vistoria" -> TelaVistoria (codigo, status, observacao) -> Salvar -> serializa byte[] no RecordStore. "Detalhes" le do RecordStore e exibe Alert.

## Modulo 07 — Spring XML Service Layer

**Dominio:** Sistema de pedidos com camada de servico
**Tecnologia:** Spring + JDBC + XML config
**Fluxo:** Main carrega ApplicationContext -> obtem PedidoService -> service.criarPedido() (transacao automatica) -> PedidoRepository (JdbcTemplate) -> HSQLDB. Confirmar pagamento usando @Transactional.

## Modulo 08 — Java5 Generics Annotations Concurrency

**Dominio:** Processador de lote de tarefas concorrente
**Tecnologia:** Generics + Enums + ExecutorService
**Fluxo:** Main cria List<Tarefa> -> processador.processar(tarefas, threads) -> ExecutorService com Callable<Resultado> -> Future<Resultado> -> aguarda e coleta resultados. Gera relatorio com EnumMap.

## Modulo 09 — Spring MVC REST JSON

**Dominio:** API REST de catalogo de produtos
**Tecnologia:** Spring MVC + Jackson + XML config
**Fluxo:** HTTP request -> DispatcherServlet -> ProdutoController (@ResponseBody) -> Jackson serializa JSON -> response. CRUD completo com @RequestBody/@PathVariable.

## Modulo 10 — Spring Boot Microservice

**Dominio:** Microservico de contas a pagar/receber
**Tecnologia:** Spring Boot + JPA + H2 + Actuator
**Fluxo:** REST -> ContaController -> ContaService (@Transactional) -> ContaRepository (JPA) -> H2. Actuator endpoints para health/metrics. Dados iniciais carregados via CommandLineRunner.

## Modulo 11 — Cloud Native Observability Era

**Dominio:** Processamento assincrono de pedidos com observabilidade
**Tecnologia:** Spring Boot + Actuator + Micrometer + @Async
**Fluxo:** POST /api/pedidos -> PedidoController -> PedidoService (log estruturado, salva CRIADO) -> processamento async (worker simulado) -> atualiza status -> metricas. Health checks customizados.

## Modulo 12 — Modern Java Language Evolution

**Dominio:** Motor de regras para analise de solicitacoes
**Tecnologia:** Records + Sealed classes + Pattern matching
**Fluxo:** Cria List<Solicitacao> (records) -> forEach analisar() -> switch expression com pattern matching nas sealed subtypes -> retorna record Aprovada/Negada/RevisaoManual.

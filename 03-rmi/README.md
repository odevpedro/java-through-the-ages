# Módulo 03 — RMI (Remote Method Invocation)

> **Era:** meados de 1997 · **JDK:** 1.1+ · **Paradigma:** objetos distribuídos (RPC sobre TCP)

---

## 1. Contexto histórico

Em 1997 a Sun Microsystems tinha um problema elegante para resolver: como permitir
que objetos Java em JVMs diferentes se comunicassem como se estivessem no mesmo
processo? A resposta foi o RMI (Remote Method Invocation), introduzido no JDK 1.1.

A premissa era sedutora: **transparência de localização**. Um desenvolvedor escreveria
código que invoca métodos em objetos remotos exatamente como invoca métodos locais.
A rede se tornaria invisível. Distribuir um sistema seria tão simples quanto
dividir classes entre processos.

O RMI foi parte de uma tendência maior: a ascensão dos "objetos distribuídos" nos
anos 1990. CORBA (OMG, 1991), DCOM (Microsoft, 1996) e RMI (Sun, 1997) todos
prometiam resolver a computação distribuída com o paradigma orientado a objetos.
A Sun usou RMI como a espinha dorsal do EJB (Enterprise JavaBeans, 1998) — a
principal tecnologia de negócios da plataforma J2EE.

---

## 2. Cenário de uso real

RMI era usado em:

- **EJB (Enterprise JavaBeans)** — toda comunicação cliente-servidor em J2EE 1.x/2.x
  era baseada em RMI por baixo (JNDI lookup + cast para interface remota).
- **Servidores de aplicação** — JBoss, WebLogic e WebSphere usavam RMI internamente
  para clustering e comunicação entre nodes.
- **Ferramentas de monitoramento** — JMX (Java Management Extensions) usa RMI como
  protocolo de transporte padrão (`RMIConnector`). Ferramentas como JConsole e
  VisualVM ainda usam RMI/JMX hoje.
- **Sistemas financeiros distribuídos** — servidores de cotação, engines de cálculo
  de risco, e serviços de clearing usavam RMI para comunicação entre componentes.

---

## 3. Recursos do Java relevantes para a época

| Recurso | JDK | Relevância neste módulo |
|---|---|---|
| `java.rmi.Remote` | 1.1 | Interface marcadora; define o contrato de objeto remoto |
| `java.rmi.RemoteException` | 1.1 | Exceção checked em todo método remoto; representa falha de rede |
| `UnicastRemoteObject` | 1.1 | Classe base que exporta o objeto para receber chamadas TCP |
| `java.rmi.Naming` | 1.1 | API de alto nível para registrar e localizar serviços |
| `LocateRegistry` | 1.1 | Cria ou localiza um `rmiregistry` programaticamente |
| `rmic` | 1.1 | Compilador de stubs/skeletons; obrigatório em JDK < 5 |
| `java.io.Serializable` | 1.1 | Todo argumento/retorno de método remoto deve ser serializável |
| `java.rmi.server.hostname` | 1.1 | Propriedade de sistema para controle do IP anunciado |

---

## 4. Arquitetura e design

```
PROCESSO: Servidor
┌─────────────────────────────────────────────────────────────┐
│  Servidor.main()                                            │
│    ├── LocateRegistry.createRegistry(1099)  ← rmiregistry  │
│    ├── new ServicoMensagensImpl()            ← exporta obj  │
│    └── Naming.rebind("rmi://localhost:1099/ServicoMensagens")│
│                                                             │
│  ServicoMensagensImpl  (UnicastRemoteObject)                │
│    └── List mensagens  (estado em memória)                  │
└────────────────────────────┬────────────────────────────────┘
                             │ TCP (porta 1099 registry
                             │      + porta dinâmica objeto)
┌────────────────────────────▼────────────────────────────────┐
│  PROCESSO: Cliente                                          │
│  Cliente.main()                                             │
│    ├── Naming.lookup("rmi://localhost:1099/ServicoMensagens")│
│    │     └── retorna: ServicoMensagensImpl_Stub             │
│    │              (proxy local gerado pelo rmic)            │
│    ├── servico.adicionarMensagem(autor, conteudo)            │
│    │     └── stub serializa args → TCP → servidor executa   │
│    └── servico.listarMensagens()                            │
│          └── servidor serializa List → TCP → cliente recebe │
└─────────────────────────────────────────────────────────────┘

CONTRATO COMPARTILHADO (interface + entidade):
  ServicoMensagens.java   ← interface remota (conhecida pelos dois lados)
  Mensagem.java           ← Serializable (trafega pela rede)
```

**O papel do `rmic`:**

```
javac → ServicoMensagensImpl.class
            │
            ▼ (rmic)
ServicoMensagensImpl_Stub.class    ← vai para o cliente
ServicoMensagensImpl_Skel.class    ← fica no servidor (JDK 1.1/1.2 apenas)
```

---

## 5. Limitações e dificuldades

### A ilusão da transparência

Transparência de localização era uma promessa e uma mentira. Chamadas remotas **nunca
se comportam como chamadas locais** em aspectos críticos:

| Aspecto | Chamada local | Chamada RMI |
|---|---|---|
| Latência | microssegundos | milissegundos a segundos |
| Falha | impossível (mesmo processo) | pode falhar a qualquer momento (`RemoteException`) |
| Concorrência | sem surpresas de network | servidor pode processar chamadas de múltiplos clientes simultâneos |
| Semântica de falha | exatamente uma vez | at-most-once (sem retry automático) |
| Passagem de objetos | por referência | por valor (cópia via serialização) |

O artigo seminal "A Note on Distributed Computing" (Waldo et al., 1994) já
alertava que tratar objetos remotos como locais era fundamentalmente errado.
O RMI ignorou esse aviso. Os desenvolvedores aprenderam a lição em produção.

### `rmic`: a etapa que todos esqueciam

Em JDK 1.1–1.4, esquecer de executar o `rmic` após modificar a implementação
causava `ClassNotFoundException` em runtime ao tentar chamar um método remoto.
O erro aparecia no cliente em tempo de execução, não em tempo de compilação —
difícil de diagnosticar.

O processo correto era: `javac` → `rmic` → copiar stubs para o classpath do cliente.
Em sistemas com deploy automatizado isso era gerenciado por scripts Ant. Sem Ant,
era responsabilidade do desenvolvedor e frequentemente esquecido.

### Serialização como protocolo de transporte

Todo objeto que trafegava via RMI precisava ser `Serializable`. Isso criava
acoplamento estrutural entre cliente e servidor: qualquer mudança em campos de
`Mensagem` (adicionar um campo, renomear, mudar tipo) exigia atualização
sincronizada dos dois lados, com compatibilidade de `serialVersionUID`.

### Gerenciamento de ciclo de vida e GC distribuído

O RMI implementava um protocolo de Garbage Collection distribuído: o servidor
mantinha objetos vivos enquanto houvesse referências remotas a eles. Esse
mecanismo era baseado em "leases" (aluguéis temporais) com renovação periódica.
Em redes instáveis, clientes podiam perder o lease sem saber, e o objeto ser
coletado no servidor enquanto o cliente ainda achava ter uma referência válida.

### Configuração de rede

`java.rmi.server.hostname` precisava ser definido para o IP correto do servidor
em qualquer ambiente com múltiplas interfaces de rede (NAT, VPN, múltiplos NICs).
Sem isso, o servidor anunciava um IP interno que o cliente externo não conseguia
alcançar. Era a causa número 1 de `ConnectException` em deploys RMI em produção.

---

## 6. Build e execução

### Pré-requisito: JDK 8

`rmic` foi removido no JDK 15. Em JDK 5–14, stubs dinâmicos tornam `rmic` opcional
mas o comando ainda existe. Em JDK 8, o fluxo clássico funciona completamente.

### Fluxo de execução (3 terminais)

```
Terminal 1 (uma vez):     sh build.sh
Terminal 2 (manter aberto): sh start-servidor.sh
Terminal 3 (quantas vezes quiser): sh start-cliente.sh
```

**Windows:**
```bat
Terminal 1:  build.bat
Terminal 2:  start-servidor.bat
Terminal 3:  start-cliente.bat
```

### Passo a passo manual detalhado

```bash
# 1. Compilar
javac -d out -sourcepath src \
    src/mensagens/Mensagem.java \
    src/mensagens/ServicoMensagens.java \
    src/mensagens/ServicoMensagensImpl.java \
    src/mensagens/Servidor.java \
    src/mensagens/Cliente.java

# 2. Gerar stubs (JDK 1.1–1.4 obrigatório; JDK 5+ opcional)
rmic -d out -classpath out mensagens.ServicoMensagensImpl

# 3. Empacotar
jar cvfm mensagens-servidor.jar MANIFEST-srv.MF -C out .
jar cvfm mensagens-cliente.jar  MANIFEST-cli.MF -C out .

# 4. Iniciar servidor (terminal separado, manter rodando)
java -Djava.rmi.server.hostname=localhost -jar mensagens-servidor.jar

# 5. Executar cliente (outro terminal)
java -jar mensagens-cliente.jar
```

### Saída esperada do cliente

```
[Cliente] Conectando ao registry em: rmi://localhost:1099/ServicoMensagens
[Cliente] Stub obtido com sucesso.
[Cliente] Mensagens no servidor antes de adicionar: 0
[Cliente] Adicionando mensagens...
  [OK] Mensagem de 'Ada Lovelace' adicionada.
  [OK] Mensagem de 'Alan Turing' adicionada.
  [OK] Mensagem de 'Grace Hopper' adicionada.
  [ERRO] Autor nao pode ser vazio.
[Cliente] 3 mensagem(ns) recebida(s) do servidor:
  1. [...]  Ada Lovelace: A maquina analitica...
  2. [...]  Alan Turing: Uma maquina pode pensar...
  3. [...]  Grace Hopper: O navio mais perigoso...
```

### Estrutura após o build

```
03-rmi/
├── src/mensagens/
│   ├── Mensagem.java
│   ├── ServicoMensagens.java      ← interface remota
│   ├── ServicoMensagensImpl.java  ← implementação (servidor)
│   ├── Servidor.java
│   └── Cliente.java
├── out/mensagens/                 ← gerado pelo build
│   ├── Mensagem.class
│   ├── ServicoMensagens.class
│   ├── ServicoMensagensImpl.class
│   ├── ServicoMensagensImpl_Stub.class   ← gerado pelo rmic
│   ├── ServicoMensagensImpl_Skel.class   ← gerado pelo rmic (JDK < 5)
│   ├── Servidor.class
│   └── Cliente.class
├── mensagens-servidor.jar
├── mensagens-cliente.jar
├── build.bat / build.sh
├── start-servidor.bat / start-servidor.sh
└── start-cliente.bat  / start-cliente.sh
```

---

## 7. Evolução posterior

| Problema do RMI | Solução que veio depois |
|---|---|
| `rmic` obrigatório e esquecível | **Stubs dinâmicos (Java 5, 2004)** — gerados em runtime automaticamente |
| Serialização como protocolo de wire | **Web Services / SOAP (2000+)** — XML como formato interoperável; depois **REST + JSON (2005+)** |
| Transparência enganosa (local ≠ remoto) | **EJB 3.0 (2006)** e depois **microserviços** tornaram o limite local/remoto explícito |
| Protocolo proprietário (JRMP) | **gRPC (2015)** — RPC moderno com Protocol Buffers; interoperável entre linguagens |
| Gestão manual de stubs e skeletons | **CDI + @Remote (JEE 6+)** — injeção de dependência gerencia o ciclo de vida |
| GC distribuído com leases frágeis | **REST stateless** — servidor não mantém referências ao cliente; cada request é autossuficiente |
| Configuração de rede complexa | **Service Discovery (Consul, Kubernetes)** — serviços se registram automaticamente |

O legado do RMI é duplo. Por um lado, mostrou que "objetos distribuídos transparentes"
era uma abstração com vazamentos fatais — a lição foi incorporada no design dos
Web Services e depois dos microserviços. Por outro, o modelo de interface + implementação
separadas (contrato explícito), o tratamento de falhas de rede como exceções verificadas,
e o conceito de stub/proxy são padrões que sobreviveram e aparecem hoje em gRPC,
Spring `@FeignClient` e frameworks de RPC modernos — apenas com uma camada de
abstração mais honesta sobre a natureza distribuída da comunicação.

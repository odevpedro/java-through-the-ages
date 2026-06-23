# Modulo 03 — RMI Branch Office Distributed System

> Era: 1997–2000 · JDK: 1.1+ · Paradigma: objetos distribuidos (RPC sobre TCP)

## 1. Contexto historico

O RMI (Remote Method Invocation) foi introduzido no JDK 1.1 em 1997, em uma epoca em que a computacao distribuida era dominada por tecnologias proprietarias e complexas como CORBA (Common Object Request Broker Architecture) e DCOM (Distributed Component Object Model da Microsoft). O RMI prometia algo radicalmente simples: chamar metodos em objetos remotos como se fossem objetos locais. Era a era pre-REST (o termo REST so seria cunhado por Roy Fielding em 2000), pre-SOAP (SOAP 1.1 da Microsoft surgiria em 2000), pre-web services. O paradigma era de "objetos distribuidos": encapsular comportamento e estado em objetos que vivem em diferentes JVMs, possivelmente em maquinas diferentes.

## 2. Problema historico

Empresas com matriz (HQ) e filiais (branches) precisavam compartilhar dados entre locais: consultar estoque disponivel em tempo real, verificar precos, realizar reservas de produtos entre lojas. Nao havia REST, nao havia web services, nao havia filas de mensagens modernas. As opcoes eram: transferencia de arquivos via FTP (lenta, propensa a erros), conexao direta a banco de dados remoto (insegura, problematico para firewalls), ou RPC (Remote Procedure Call). O RMI oferecia RPC orientado a objetos com passagem de objetos serializados, coleta de lixo distribuida e registro de servicos via rmiregistry.

## 3. Aplicacao: Consulta e reserva de estoque remoto

A filial (cliente) consulta a disponibilidade de produtos no estoque da matriz (servidor) e solicita a reserva de uma quantidade. O servidor mantem o inventario em memoria e responde as chamadas remotas. E a analogia direta de um sistema de estoque compartilhado entre lojas.

## 4. Recursos do Java relevantes

| Recurso | Finalidade |
|---------|------------|
| `java.rmi.Remote` | Interface marcadora que identifica uma interface como remota. Todo metodo deve declarar `throws RemoteException` |
| `RemoteException` | Excecao que encapsula falhas de comunicacao, rede, serializacao ou protocolo |
| `UnicastRemoteObject` | Classe base para implementar objetos remotos que aceitam chamadas TCP. Gerencia exportacao do objeto, geracao de stub e comunicacao |
| `java.rmi.Naming` | API para registrar e localizar objetos remotos no rmiregistry. Metodos: `bind`, `rebind`, `lookup` |
| `java.rmi.registry.LocateRegistry` | Utilitario para criar ou localizar um registry em uma porta especifica (padrao: 1099) |
| `rmic` | Gerador de stubs. Em JDK 1.1/1.2, obrigatorio. Em JDK 5+, stubs sao gerados dinamicamente via reflection |
| `java.io.Serializable` | Necessario para objetos passados por valor entre JVM cliente e servidor |

## 5. Arquitetura

```
=== Servidor (matriz) ===
ServidorEstoque.main()
  └── LocateRegistry.createRegistry(1099)
        └── Naming.rebind("rmi://localhost:1099/EstoqueCentral")
              └── EstoqueImpl (extends UnicastRemoteObject, implements Estoque)
                    ├── List<Produto> estoque
                    ├── listarProdutos()
                    ├── consultarDisponibilidade(int id)
                    └── reservarProduto(int id, int qtd)

=== Cliente (filial) ===
ClienteFilial.main()
  └── Naming.lookup("rmi://localhost:1099/EstoqueCentral")
        └── Estoque servicoRemoto (stub)
              ├── listarProdutos()
              ├── consultarDisponibilidade(1)
              └── reservarProduto(1, 5)
```

## 6. Limitacoes

- **Ilusao de transparencia**: Chamar um metodo remoto parece igual a um metodo local, mas nao e. Latencia de rede, falhas de conexao, concorrencia, semantica de passagem de objetos (por copia, nao por referencia) — tudo e diferente. Desenvolvedores caiam na armadilha de tratar objetos remotos como locais.
- **Etapa rmic facilmente esquecida**: Em JDK 1.1/1.2, esquecer de executar `rmic` resultava em `ClassNotFoundException` para o stub em tempo de execucao. O processo de build era frágil e facilmente esquecido.
- **Serializacao como protocolo**: O RMI usa serializacao Java para codificar parametros e retornos. Isso cria acoplamento forte entre versoes do JDK e entre versoes da classe. Se o servidor atualizar uma classe, todos os clientes precisam ser recompilados.
- **Distributed GC com leases**: O RMI implementa coleta de lixo distribuida usando leases: o cliente envia "heartbeats" periodicos ao servidor informando que ainda referencia o objeto remoto. Se o heartbeat falha (rede instavel), o servidor pode desreferenciar o objeto prematuramente.
- **`java.rmi.server.hostname`**: Em maquinas com multiplas interfaces de rede, o RMI pode anunciar um IP inacessivel para o cliente. Configurar `java.rmi.server.hostname` era uma dor constante e uma das causas mais comuns de `ConnectException` misteriosas.
- **Sem seguranca por padrao**: RMI nao oferece criptografia ou autenticacao nativas. Qualquer um que alcance a porta 1099 pode chamar metodos remotos. Seguranca exigia configuracao manual de `RMISecurityManager` e politicas de acesso.

## 7. Peca de museu

Este modulo representa o auge do sonho dos "objetos distribuidos" — e seu fracasso. A licao duradoura que o RMI ensinou e que chamadas remotas sao fundamentalmente diferentes de chamadas locais, e fingir que sao iguais causa mais problemas do que resolve. Essa licao moldou como construimos sistemas distribuidos hoje: REST (chamadas HTTP explicitas, stateless), gRPC (protobuf, contratos claros), microservicos (comunicacao via rede explicita, tolerancia a falhas, circuit breakers). O RMI morreu nao por ser tecnicamente inferior, mas porque seu modelo de "transparencia" ocultava a complexidade real da distribuicao.

## 8. Build e execucao

Requer JDK 8 (rmic removido no JDK 15). Necessita de 3 terminais separados.

```bash
# Terminal 1: Compilar e iniciar servidor
./build.sh

# Terminal 2: Servidor RMI (manter rodando)
./start-servidor.sh

# Terminal 3: Cliente (executar apos servidor)
./start-cliente.sh
```

No Windows:
```
build.bat
start-servidor.bat
start-cliente.bat
```

## 9. Evolucao posterior

| Limitacao | Solucao posterior |
|-----------|-------------------|
| Serializacao como protocolo | SOAP/XML (2000) — interoperabilidade entre linguagens; REST/JSON (2000+) — leve, stateless |
| rmic obrigatorio | Geracao dinamica de stubs (JDK 5+) — dispensa rmic |
| Sem seguranca nativa | RMI over SSL (JDK 5+) — criptografia na camada de transporte |
| Distributed GC problematico | Stateless services (REST) — sem estado no servidor, sem GC distribuido |
| Acoplamento Java-Java | gRPC (2015) — contratos em protobuf, geracao de clientes em qualquer linguagem |
| Configuracao de hostname confusa | Service discovery moderno (Consul, Eureka, Kubernetes DNS) |

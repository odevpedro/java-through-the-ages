# Modulo 05 — EJB Enterprise Transaction Era

> Era: 2001–2005 · J2EE: 1.3/1.4 · Paradigma: container gerenciado

## 1. Contexto historico
Enterprise JavaBeans 2.x was the standard for transactional enterprise systems. Banks, insurance, governments required: ACID transactions, declarative security, distributed components, connection pooling.

## 2. Problema historico
Large enterprises needed distributed transaction processing across multiple resources (databases, queues, ERPs). Manual transaction management was error-prone. EJB promised: write business logic, container handles transactions, security, pooling, lifecycle.

## 3. Aplicacao: Mini sistema bancario de transferencia (simulado)
Account-to-account money transfer with transaction rollback on failure. Since real EJB 2.x deployment requires a full J2EE app server (JBoss, WebLogic), this module provides:
a) The EJB 2.x source files showing the real complexity
b) A simulation class that demonstrates the same behavior without the container

## 4. O que era EJB 2.x
7 files for one "Hello World": Home interface, Remote interface, Bean implementation, Deployment descriptor (ejb-jar.xml), JNDI lookup, Container config, Build script.

## 5. Recursos do J2EE

| Recurso | Uso |
|---------|-----|
| `javax.ejb.*` | Interfaces EJB (SessionBean, EntityBean) |
| `SessionBean` | Componente de logica de negocios |
| `EntityBean` | Mapeamento objeto-relacional (CMP/BMP) |
| CMT (Container-Managed Transactions) | Transacoes declarativas no descritor |
| JNDI | Localizacao de componentes no servidor |
| RMI under the hood | Comunicacao remota entre componentes |
| `ejb-jar.xml` | Descritor de deploy EJB |

## 6. Arquitetura (conceitual)

```
Cliente --> JNDI lookup --> Home --> create() --> Remote --> transferir()
                                                               |
                                                    Container (TX mgmt)
                                                               |
                                                          BancoDAO
```

## 7. Codigo EJB 2.x (simulado)

### TransferenciaHome.java (Home interface)
```java
package banco.ejb;

import javax.ejb.EJBHome;
import java.rmi.RemoteException;

public interface TransferenciaHome extends EJBHome {
    TransferenciaRemote create() throws RemoteException, javax.ejb.CreateException;
}
```

### TransferenciaRemote.java (Remote interface)
```java
package banco.ejb;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

public interface TransferenciaRemote extends EJBObject {
    void transferir(int origem, int destino, double valor)
        throws RemoteException, javax.ejb.EJBException;
}
```

### TransferenciaBean.java (SessionBean)
```java
package banco.ejb;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public class TransferenciaBean implements SessionBean {
    private SessionContext ctx;

    public void ejbCreate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}

    public void transferir(int origem, int destino, double valor) {
        // logica de transferencia com CMT
        BancoDAO dao = new BancoDAO();
        dao.debitar(origem, valor);
        dao.creditar(destino, valor);
    }
}
```

## 8. A simulacao
O projeto inclui `SimuladorTransferencia.java` que demonstra o mesmo fluxo sem dependencia de container. O saldo inicial e as transferencias mostram commit e rollback simulados.

## 9. Limitacoes
- Extreme boilerplate (7+ files per component)
- Deployment descriptors in XML (no annotations)
- Lock-in to app server vendor
- Complex testing (no main(), must deploy)
- Container-managed relationships (CMP) were fragile

## 10. Peca de museu
EJB 2.x is the best example of "enterprise overengineering" in Java history. It solved real problems (transactions, security, distribution) but at such high complexity cost that it created the demand for Spring. Understanding EJB is understanding WHY the Java community embraced Spring so enthusiastically.

## 11. Build e execucao
Simulation mode: compile and run SimuladorTransferencia directly.
EJB mode: requires JBoss 4.x or WebLogic 8.x — documented but impractical today.

## 12. Evolucao posterior
EJB 3.0 (annotations, POJOs), Spring (IoC, declarative TX), JPA (replaces Entity Beans)

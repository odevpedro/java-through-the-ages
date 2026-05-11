# Java Through the Ages

Um museu prático da evolução da plataforma Java, organizado como monorepo educacional.
Cada módulo implementa o **mesmo domínio funcional** — um sistema simples de mensagens —
usando a tecnologia, os idiomas e as restrições reais da época em que foi escrito.

O objetivo não é mostrar "código ruim vs código bom". É mostrar **como se desenvolveu
em cada era**, quais problemas os desenvolvedores enfrentavam, e como as limitações de
uma fase foram resolvidas (ou substituídas) nas fases seguintes.

---

## Pré-requisitos globais

| Módulo | JDK necessário | Observação |
|---|---|---|
| 01-applet-awt | JDK 1.1 / 1.4 | Suporte a Applet removido do JDK 11+. Use JDK 8 com `appletviewer` ou Docker legado. |
| 02-swing-desktop | JDK 1.2+ (recomendado JDK 8) | Totalmente funcional em JDKs modernos. |
| 03-rmi | JDK 1.1+ (recomendado JDK 8) | `rmiregistry` incluído no JDK. |
| 04-servlet-jsp-jdbc | JDK 8 + Tomcat 4.x / Servlet 2.3 | Pode rodar em Tomcat moderno com adaptações mínimas. |
| 05-javame | JDK legado + WTK 2.5 (Sun Wireless Toolkit) | Requer emulador MIDP. Ver README do módulo. |

> Todos os módulos são **autocontidos**: cada um tem seu próprio `build.bat` / `build.sh`
> e instruções de execução independentes no seu `README.md`.

---

## Estrutura do repositório

```
java-through-the-ages/
├── README.md                   ← este arquivo
├── docs/
│   ├── timeline.md             ← linha do tempo com marcos do Java
│   └── comparison-matrix.md   ← tabela comparativa entre os módulos
├── 01-applet-awt/
│   ├── README.md
│   ├── src/
│   ├── build.bat
│   └── build.sh
├── 02-swing-desktop/
│   ├── README.md
│   ├── src/
│   ├── build.bat
│   └── build.sh
├── 03-rmi/
│   ├── README.md
│   ├── src/
│   ├── build.bat
│   └── build.sh
├── 04-servlet-jsp-jdbc/
│   ├── README.md
│   ├── src/
│   ├── web/
│   ├── lib/
│   ├── build.bat
│   └── build.sh
└── 05-javame/
    ├── README.md
    ├── src/
    ├── build.bat
    └── build.sh
```

---

## Domínio funcional comum: sistema de mensagens

Todos os módulos implementam as mesmas operações básicas, adaptadas à plataforma:

1. **Adicionar uma mensagem** — campos `autor` (String) e `conteudo` (String).
2. **Listar mensagens armazenadas**.

A persistência varia por módulo (memória, arquivo, RecordStore, banco relacional),
mas o conceito de "mensagem" e os nomes dos campos são mantidos consistentes.

---

## Guia de navegação

Recomendamos seguir a **ordem cronológica** (módulo 1 a 5) para perceber a
evolução de forma comparativa. Cada módulo é independente, mas o efeito didático
é maior quando lidos em sequência.

| Módulo | Era | Tecnologia central | Destaques didáticos |
|---|---|---|---|
| [01-applet-awt](./01-applet-awt/README.md) | ~1996, JDK 1.0/1.1 | Applet + AWT | "Write Once, Run Anywhere" na prática; limitações do sandbox |
| [02-swing-desktop](./02-swing-desktop/README.md) | ~1998, JDK 1.2+ | Swing | Event dispatch thread; layout managers; serialização em arquivo |
| [03-rmi](./03-rmi/README.md) | ~1997, JDK 1.1+ | RMI | Objetos distribuídos; `rmiregistry`; stubs gerados por `rmic` |
| [04-servlet-jsp-jdbc](./04-servlet-jsp-jdbc/README.md) | ~2001, J2EE 1.2 | Servlet + JSP + JDBC | Ascensão do Java web; camadas manuais; sem framework |
| [05-javame](./05-javame/README.md) | ~2005, MIDP 2.0 | MIDlet + RecordStore | Java em dispositivos com 128 KB de RAM; restrições extremas |

---

## Como contribuir / estender

Este projeto é intencionalmente **sem dependências entre módulos**.
Para adicionar uma nova era (ex: Spring Framework 2.x, EJB 2.1, Android pré-Gradle):

1. Crie um diretório numerado sequencialmente (ex: `06-spring-xml/`).
2. Siga a mesma estrutura de `README.md` com as seções didáticas obrigatórias.
3. Implemente o mesmo domínio de mensagens.
4. Atualize `docs/timeline.md` e `docs/comparison-matrix.md`.

---

## Licença

Uso educacional livre. Nenhuma dependência proprietária; todas as ferramentas
usadas eram padrão do JDK ou de distribuição gratuita na época representada.

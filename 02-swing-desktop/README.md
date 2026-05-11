# Módulo 02 — Swing Desktop

> **Era:** final dos anos 1990 · **JDK:** 1.2+ · **Paradigma:** OO + evento (Swing / EDT)

---

## 1. Contexto histórico

Em dezembro de 1998, a Sun lançou o Java 2 (rebatizado de JDK 1.2), e com ele vieram
duas mudanças que redefiniriam o desenvolvimento desktop em Java: o **Collections
Framework** e o **Swing**.

O AWT, toolkit do JDK 1.0, tinha um problema fundamental: delegar a renderização para
os componentes nativos do sistema operacional. Isso criava comportamento inconsistente
— um layout perfeito no Windows 95 podia estar quebrado no Solaris/Motif. O Swing
resolveu isso de forma radical: **todos os componentes são desenhados em Java puro**,
pixel a pixel, sobre um Canvas. A aparência se tornou consistente entre plataformas;
o desenvolvedor podia escolher um "Look and Feel" — Metal (padrão Java), Windows ou
Motif — e a UI mudava inteira sem alterar uma linha de código de negócio.

O preço foi a necessidade de entender a **Event Dispatch Thread (EDT)**: uma thread
exclusiva responsável por processar eventos e repintar componentes. Todo acesso a
componentes Swing fora da EDT causa bugs sutis e intermitentes — um dos maiores
geradores de chamados de suporte em projetos Java desktop dos anos 2000.

---

## 2. Cenário de uso real

Swing foi a tecnologia dominante para aplicações desktop corporativas Java por mais
de uma década (1999–2012):

- **IDEs de desenvolvimento** — Eclipse (parcialmente), NetBeans e JDeveloper usavam
  Swing para suas interfaces.
- **Clientes de banco e corretoras** — terminais de home banking e plataformas de
  trading ricas, onde a empresa controlava as máquinas dos usuários.
- **Ferramentas de configuração e monitoramento** — consoles de gerenciamento de
  middleware (JBoss, WebLogic) e ferramentas de ETL (Talend) usavam Swing.
- **Aplicações científicas e de engenharia** — GUIs de simulação, editores de
  diagramas e ferramentas de análise onde o rich client era mais adequado que
  uma interface web.

Este sistema de mensagens representa uma aplicação interna simples com persistência
local: dados salvos em arquivo binário entre sessões, sem necessidade de servidor.

---

## 3. Recursos do Java relevantes para a época

| Recurso | JDK | Relevância neste módulo |
|---|---|---|
| `javax.swing.*` | 1.2 | Toda a UI: `JFrame`, `JList`, `JTextArea`, `JSplitPane`, `JOptionPane` |
| `DefaultListModel` | 1.2 | Modelo MVC da JList; notifica a view automaticamente ao mudar |
| `ListCellRenderer` | 1.2 | Renderização customizada de itens da JList |
| `GridBagLayout` | 1.0 (AWT) | Layout de formulário mais flexível; verboso porém poderoso |
| `SwingUtilities.invokeLater` | 1.2 | Submissão segura de trabalho para a EDT |
| `java.io.Serializable` | 1.1 | Persistência de objetos em arquivo binário |
| `ObjectOutputStream` / `ObjectInputStream` | 1.1 | Streams para serialização/deserialização |
| `java.util.ArrayList` | 1.2 | Substituto do `Vector`; não sincronizado (mais rápido em single-thread) |
| `UIManager.setLookAndFeel()` | 1.2 | Troca de aparência em tempo de execução |
| `WindowAdapter` | 1.1 | Implementação parcial de `WindowListener`; evita métodos vazios |

---

## 4. Arquitetura e design

```
main()
  └── SwingUtilities.invokeLater(Runnable)
            │
            ▼ (na EDT)
      JanelaPrincipal  (extends JFrame)
            │
            ├── JSplitPane (horizontal)
            │     ├── ESQUERDA: painel de listagem
            │     │     ├── JList (view)
            │     │     │     └── MensagemListRenderer (ListCellRenderer)
            │     │     └── botaoRemover (habilitado via ListSelectionListener)
            │     │
            │     └── DIREITA: painel de formulário
            │           ├── campoAutor (JTextField)
            │           ├── campoConteudo (JTextArea + JScrollPane)
            │           └── botaoAdicionar
            │
            ├── rotuloContador (JLabel no SOUTH)
            │
            ├── List<Mensagem> (estado em memória — lista Java simples)
            │
            └── RepositorioMensagens
                  └── mensagens.dat (arquivo binário de serialização)
```

**Separação de responsabilidades emergindo (mas ainda incompleta):**

Comparado ao módulo 01 (Applet, tudo numa classe), aqui há uma separação inicial:
- `Mensagem` — entidade de domínio (ainda anêmica)
- `RepositorioMensagens` — persistência isolada
- `MensagemListRenderer` — apresentação customizada
- `JanelaPrincipal` — controlador + view principal

Ainda não é MVC puro (a janela mistura lógica de negócio com eventos de UI), mas
a separação começa a emergir por necessidade prática: o renderer precisava ser uma
classe separada por exigência da API `ListCellRenderer`.

---

## 5. Limitações e dificuldades

### A EDT: a armadilha mais frequente do Swing

O Swing não é thread-safe por design. Qualquer acesso a um componente fora da EDT
pode corromper o estado interno do toolkit, causando:
- Telas que não atualizam até o próximo evento de mouse
- Deadlocks entre a EDT e threads de trabalho
- `ConcurrentModificationException` em iterações do modelo

O padrão correto era (`SwingWorker`, introduzido no JDK 6):
```java
// ERRADO: atualizar JLabel diretamente de uma thread de background
new Thread(() -> { label.setText("resultado"); }).start();

// CORRETO: submeter a atualização para a EDT
new Thread(() -> {
    String resultado = calcularAlgo();
    SwingUtilities.invokeLater(() -> label.setText(resultado));
}).start();
```

Antes do `SwingWorker` (JDK 6, 2006), os desenvolvedores gerenciavam isso manualmente,
o que era fonte de bugs persistentes e difíceis de reproduzir.

### GridBagLayout: poder e verbosidade

`GridBagLayout` com `GridBagConstraints` era notório pela quantidade de código
necessária para layouts simples. Um formulário com 4 campos exigia ~40 linhas só de
declaração de constraints. Isso levou ao surgimento de ferramentas como o **NetBeans
GUI Builder (Matisse)** em 2005, que gerava esse código automaticamente — mas o código
gerado era ilegível e difícil de manter manualmente.

### Serialização como persistência: fragilidade estrutural

Serializar objetos diretamente é frágil por design:
- **Renomear um campo** invalida todos os arquivos `.dat` existentes
- **Adicionar um campo obrigatório** quebra a leitura de versões antigas
- **Mudar o pacote ou o nome da classe** torna o arquivo irrecuperável

Isso era um problema real em sistemas em produção. A solução moderna é separar
o modelo de domínio do modelo de persistência (DTOs, banco de dados com migrations).

### Sem databinding automático

Toda sincronização entre o modelo de dados (`List<Mensagem>`) e o modelo de UI
(`DefaultListModel`) era manual. Cada operação de adicionar/remover exigia:
1. Modificar a lista Java
2. Salvar no disco
3. Limpar e repopular o `DefaultListModel`
4. Atualizar o contador de status

Frameworks modernos como JavaFX (com `ObservableList`) ou React/Vue no front-end
resolveram isso com binding reativo — a UI atualiza automaticamente quando o modelo
muda.

### SimpleDateFormat não é thread-safe

`SimpleDateFormat` mantém estado interno durante a formatação. Se dois threads
chamassem o mesmo objeto simultaneamente, o resultado seria corrompido. A solução
limpa veio com `DateTimeFormatter` do `java.time` no Java 8 — imutável e thread-safe
por design.

---

## 6. Build e execução

### Pré-requisito: JDK 8 ou superior

Swing não foi removido dos JDKs modernos (ao contrário de Applets). Este módulo
compila e executa normalmente em JDK 8, 11, 17 e 21.

**Verificar versão:**
```bash
java -version
```

### Linux/macOS
```bash
chmod +x build.sh
./build.sh
```

### Windows
```bat
build.bat
```

### Passo a passo manual
```bash
# 1. Compilar
javac -d out -sourcepath src \
    src/mensagens/Mensagem.java \
    src/mensagens/RepositorioMensagens.java \
    src/mensagens/MensagemListRenderer.java \
    src/mensagens/JanelaPrincipal.java

# 2. Empacotar com manifest
echo "Main-Class: mensagens.JanelaPrincipal" > MANIFEST.MF
jar cvfm mensagens-swing.jar MANIFEST.MF -C out .

# 3. Executar
java -jar mensagens-swing.jar
```

### Estrutura após o build
```
02-swing-desktop/
├── src/
│   └── mensagens/
│       ├── Mensagem.java
│       ├── RepositorioMensagens.java
│       ├── MensagemListRenderer.java
│       └── JanelaPrincipal.java
├── out/                        ← gerado pelo build
├── mensagens-swing.jar         ← gerado pelo build; executável
├── mensagens.dat               ← gerado na primeira execução; dados serializados
├── build.bat
└── build.sh
```

### Nota sobre display gráfico em servidores Linux
Swing exige um ambiente gráfico (X11 ou Wayland). Em servidores sem display:
```bash
# via SSH com X forwarding
ssh -X usuario@servidor
java -jar mensagens-swing.jar

# ou com display virtual
Xvfb :99 &
export DISPLAY=:99
java -jar mensagens-swing.jar
```

---

## 7. Evolução posterior

| Problema | Solução que veio depois |
|---|---|
| EDT manual e bug-prone | **`SwingWorker` (JDK 6, 2006)** — abstração para tarefas background com callback seguro na EDT |
| `GridBagLayout` verboso | **NetBeans Matisse (2005)** gerava o código; depois **JavaFX FXML (2011)** com layout declarativo em XML |
| Serialização frágil como persistência | **JPA/Hibernate** para banco relacional; **JSON/XML** para arquivos (Jackson, 2009; JAXB, Java 6) |
| Databinding manual | **JavaFX `ObservableList` + Properties (2011)** — binding reativo nativo |
| `SimpleDateFormat` não-thread-safe | **`DateTimeFormatter` do `java.time` (Java 8, 2014)** — imutável, thread-safe |
| Classes anônimas para listeners | **Lambdas (Java 8)**: `botao.addActionListener(e -> adicionarMensagem())` |
| `List` sem generics (cast explícito) | **Generics (Java 5, 2004)**: `List<Mensagem>` elimina o cast e erros em runtime |
| Aparência datada (Metal L&F) | **Nimbus L&F (JDK 6u10, 2008)** — visual moderno padrão; depois **JavaFX CSS** para estilização completa |

O Swing foi o toolkit dominante por mais de 10 anos, mas seu modelo de programação
imperativo (montar UI linha a linha) nunca foi elegante. O JavaFX (2011, integrado ao
JDK 8 em 2014) trouxe o modelo declarativo via FXML, separação de estilo via CSS e
binding reativo. Hoje, aplicações desktop Java novas geralmente usam JavaFX ou optam
por tecnologias web (Electron, Tauri) que rodam no browser como front-end.

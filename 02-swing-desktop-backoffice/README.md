# Modulo 02 — Swing Desktop Backoffice

> Era: 1998–2002 · JDK: 1.2+ · Paradigma: OO + evento (Swing/EDT)

## 1. Contexto historico

O Swing foi lancado com o JDK 1.2 em dezembro de 1998, marcando uma virada na historia do Java desktop. Diferente do AWT, que delegava a renderizacao ao toolkit nativo de cada sistema operacional, o Swing desenhava seus proprios componentes em Java puro — o que garantia aparencia consistente em qualquer plataforma. Junto com o JDK 1.2 veio o Collections Framework (List, Set, Map, Iterator), substituindo Vector e Enumeration. O conceito de Event Dispatch Thread (EDT) foi formalizado: toda manipulacao de componentes Swing deve ocorrer em uma unica thread dedicada.

## 2. Problema historico

Empresas precisavam de aplicacoes desktop internas multiplataforma: cadastro de clientes, controle de estoque, sistemas de backoffice em geral. As alternativas da epoca eram Visual Basic (Windows-only), Delphi (Windows-only) e PowerBuilder (Windows-only). O Java com Swing oferecia a promessa de escrever uma aplicacao uma unica vez e roda-la em Windows, Linux, Mac, Solaris — sem recompilar, sem reescrever. Para corporacoes com ambientes heterogeneos, isso era um argumento de venda poderoso.

## 3. Aplicacao: Sistema de cadastro de clientes corporativo

Uma aplicacao Swing completa para registrar, listar, editar e excluir clientes. Os dados sao persistidos via serializacao Java em arquivo binario. A interface possui formulario de entrada, tabela para visualizacao dos registros, botoes de acao e barra de menus.

## 4. Recursos do Java relevantes

| Recurso | Finalidade |
|---------|------------|
| `javax.swing.*` (JFrame, JTable, JTextField, JButton, JScrollPane, JMenuBar, JMenu, JMenuItem) | Componentes Swing puros — mesma aparencia em qualquer SO |
| `DefaultTableModel` | Modelo de tabela que gerencia os dados exibidos em uma JTable. Permite adicionar, remover e atualizar linhas |
| `GridBagLayout` | Gerenciador de layout flexivel e poderoso (mas verboso) para posicionamento preciso de componentes |
| `SwingUtilities.invokeLater` | Metodo para executar codigo na EDT. Essencial para garantir thread-safety na construcao e manipulacao da interface |
| `java.io.Serializable` | Interface marcadora que habilita a serializacao de objetos para persistencia binaria |
| `ObjectOutputStream` / `ObjectInputStream` | Streams para serializar e desserializar objetos Java |
| `UIManager.setLookAndFeel` | Permite trocar a aparencia visual da aplicacao (Metal, Nimbus, Windows, GTK+, Motif) |
| `WindowAdapter` | Classe adaptadora para eventos de janela (abrir, fechar, iconificar) |

## 5. Arquitetura

```
Main.main()
  └── CadastroClientesFrame (extends JFrame)
        ├── JMenuBar (Arquivo > Sair, Ajuda > Sobre)
        ├── JPanel formulario (NORTH)
        │     ├── JTextField nome
        │     ├── JTextField email
        │     ├── JTextField telefone
        │     └── JButton salvar / limpar
        ├── JScrollPane + JTable (CENTER)
        │     └── DefaultTableModel (colunas: Nome, Email, Telefone)
        ├── JPanel botoes (SOUTH)
        │     └── JButton editar / excluir
        └── JLabel status
```

O fluxo de dados: formulario <-> DefaultTableModel <-> RepositorioCliente (serializacao em clientes.dat).

## 6. Limitacoes

- **EDT: problema de thread-safety**: Qualquer operacao fora da EDT corrompe a interface. O desenvolvedor precisa lembrar de usar `SwingUtilities.invokeLater` em toda thread secundaria. Esquecimentos causam bugs intermitentes dificeis de reproduzir.
- **GridBagLayout verboso**: Configurar um GridBagLayout exige dezenas de linhas de codigo com constantes `GridBagConstraints`. Um layout simples em HTML exige 3 linhas; em Swing, 30 linhas.
- **Serializacao como persistencia**: Usar `ObjectOutputStream` para salvar dados em arquivo parece pratico, mas quebra silenciosamente se um campo for renomeado, adicionado ou removido — o `serialVersionUID` nao corresponde mais. Nao ha consultas, indices ou integridade referencial.
- **Databinding manual**: Sincronizar os campos do formulario com o `DefaultTableModel` e com o repositorio exige codigo manual de copia de dados. Qualquer framework moderno teria binding automatico bidirecional.
- **SimpleDateFormat nao thread-safe**: A classe `SimpleDateFormat` nao e segura para uso concorrente, causando resultados inconsistentes em aplicacoes com multiplas threads formatando datas.
- **Sem framework de injecao de dependencia**: As dependencias (RepositorioCliente, layout) sao instanciadas diretamente no frame. Trocar a implementacao exige editar o codigo fonte.

## 7. Peca de museu

Este modulo representa a era de ouro do Java desktop corporativo. Por quase uma decada (1998-2008), o Swing foi o padrao para ferramentas de backoffice internas. Bancos, seguradoras, governos e fabricas rodavam aplicacoes Swing em terminais Windows, Linux e Mac. A complexidade do databinding manual e os problemas de thread-safety da EDT mostram por que as aplicacoes web eventualmente substituiram os thick clients para a maioria dos casos de uso corporativo. O navegador como runtime venceu nao por ser mais capaz, mas por eliminar a复杂idade de deploy, atualizacao e gerenciamento de runtime desktop.

## 8. Build e execucao

Requer JDK 8+. O Swing funciona em JDKs modernos sem alteracoes.

```bash
# Linux/macOS
chmod +x build.sh
./build.sh

# Windows
build.bat
```

## Captura de tela

![Swing Desktop](docs/screenshot02.png)

## 9. Evolucao posterior

| Limitacao | Solucao posterior |
|-----------|-------------------|
| Thread-safety da EDT | `SwingWorker` (JDK 1.6) — executa tarefas em background e publica resultados na EDT |
| GridBagLayout verboso | NetBeans Matisse GUI Builder (2005) — layout visual |
| Serializacao quebra schema | JPA + Hibernate (2006) — mapeamento objeto-relacional com versionamento de schema |
| Databinding manual | JavaFX (2008) — binding bidirecional com `Bindings` e propriedades observaveis |
| SimpleDateFormat thread-safe | `DateTimeFormatter` (Java 8, 2014) — imutavel e thread-safe |
| Sem DI | Inversao de controle com Spring (2004) — injecao de dependencia via XML e depois annotations |
| Look and Feel limitado | JavaFX CSS (2008) — estilizacao via CSS moderna |

# Modulo 01 — Applet AWT Browser Era

> Era: 1995–1997 · JDK: 1.0/1.1 · Paradigma: OO + evento (AWT)

## 1. Contexto historico

Java 1.0 foi anunciado pela Sun Microsystems em 1995, em meio a explosao da web comercial. A Netscape Communications Corporation, dona do navegador dominante (Netscape Navigator 2.0), fez uma parceria historica para embutir a JVM no browser — nascia o suporte a Applets. A web da epoca era estaticamente simples: paginas HTML servidas por servidores HTTP 1.0, sem JavaScript moderno (Brendan Eich criou LiveScript em 1995, mas levaria anos para amadurecer), sem AJAX (XMLHttpRequest so surgiria no IE5 em 1999), sem SPAs (single-page applications). Tudo era navegacao entre paginas. A promessa "Write Once, Run Anywhere" (WORA) era revolucionaria em um mercado fragmentado entre Windows 95, Mac OS, varios sabores de Unix e o emergente Linux.

## 2. Problema historico

Empresas queriam levar aplicacoes interativas para dentro do navegador: calculadoras de orcamento, configuradores de produto, jogos, visualizacao de dados. Antes do JavaScript moderno e do CSS, a unica maneira de ter interatividade real dentro de uma pagina web era via plugin — e o unico plugin multiplataforma disponivel era o Java Applet. Shockwave/Flash existiam, mas eram focados em multimedia e fechados. O Applet prometia aplicacoes completas escritas em Java rodando dentro do browser, com acesso a interface grafica (AWT), rede e multimedia.

## 3. Aplicacao: Mini catalogo interativo de produtos

Este modulo implementa um catalogo de produtos onde o usuario seleciona itens de um dropdown (Choice), escolhe quantidades, calcula o preco total e ve o resultado em uma area de texto. E a analogia direta de um formulario de e-commerce que, em 1996, so podia ser feito com Applets.

## 4. Recursos do Java relevantes para a epoca

| Recurso | Finalidade |
|---------|------------|
| `java.applet.Applet` | Classe base que todo Applet deve estender. Gerencia o ciclo de vida (init, start, stop, destroy). |
| AWT (`java.awt.*`) | Abstract Window Toolkit — conjunto de componentes graficos nativos (Button, TextField, Label, Panel, Choice, TextArea). |
| `java.awt.event.ActionListener` | Modelo de delegacao de eventos introduzido no JDK 1.1. Antes disso, o JDK 1.0 usava o modelo antigo baseado em hierarquia de componentes e metodos como `action()`. |
| `java.util.Vector` | Array dinamico sincronizado. Era a unica colecao disponivel no JDK 1.0/1.1 (antes de Collections Framework, lancado no JDK 1.2 em 1998). |
| `java.util.Enumeration` | Interface de iteracao predecessor do Iterator. Usada para percorrer elementos de um Vector. |

## 5. Arquitetura

```
catalogo.html
  └── CatalogoApplet (extends Applet, implements ActionListener)
        ├── Vector<Produto> catalogo     (produtos cadastrados)
        ├── Choice seletorProduto        (dropdown de produtos)
        ├── TextField campoQuantidade
        ├── Button btnCalcular / btnAdicionar
        ├── TextArea areaResultado
        └── Label labelStatus
```

O applet carrega uma lista fixa de produtos no metodo `init()`, preenche o Choice com os nomes, e aguarda eventos do usuario. Quando o usuario clica em "Calcular", o applet le o produto selecionado, a quantidade digitada, calcula o total (quantidade * preco unitario) e exibe na TextArea.

## 6. Limitacoes da epoca

- **Sandbox de seguranca**: Applets so podiam conectar-se ao servidor de origem (same-origin policy). Nao tinham acesso ao sistema de arquivos local. Nao podiam executar comandos nativos. Qualquer operacao de I/O local era bloqueada pelo SecurityManager.
- **AWT inconsistente entre SOs**: Como o AWT delegava a renderizacao ao toolkit nativo de cada SO, um botao no Windows 95 parecia diferente do mesmo botao no Solaris. Na pratica: "Write Once, Debug Everywhere". A aparencia inconsistente fez a Sun desenvolver o Swing (puro Java) para o JDK 1.2.
- **Performance de carregamento**: Modems de 28.8 kbps eram comuns. Baixar um JAR de 50-100 KB levava minutos. O formato JAR (introduzido no JDK 1.1) ajudou a agregar dezenas de .class em um unico download, mas a experiencia ainda era frustrante.
- **Plugin Java instavel no browser**: O plugin Java para navegadores era notoriamente instavel. Conflitos de versao de JVM, crashes do navegador, incompatibilidades entre versoes do plugin e do JDK faziam parte do dia a dia dos desenvolvedores.

## 7. Peca de museu

Este modulo representa o sonho de "aplicacoes no navegador antes do navegador estar pronto". Foi a primeira tentativa real de transformar o browser em uma plataforma de aplicacoes, mas a sandbox, embora necessaria para seguranca, mutilava a funcionalidade que os desenvolvedores precisavam. Em 2007, o iPhone lancou sem suporte a Java Applets, sinalizando o inicio do fim. Em 2015, a Oracle oficialmente depreciou o Applet API no JDK 9 e removeu o appletviewer no JDK 11. A morte dos Applets ensina uma licao duradoura: plataformas morrem quando o ecossistema se afasta — navegadores pararam de suportar o plugin, desenvolvedores migraram para JavaScript/AJAX, e a Sun/Oracle nao conseguiu modernizar a tecnologia a tempo.

## 8. Build e execucao

Requer JDK 8 (ultimo JDK que inclui o appletviewer). O appletviewer foi removido a partir do JDK 11.

```bash
# Linux/macOS
chmod +x build.sh
./build.sh

# Windows
build.bat
```

O script compila os fontes, empacota em `catalogo.jar` e abre o appletviewer apontando para `catalogo.html`.

Para JDK 11+ (sem appletviewer), use Docker:
```bash
docker run -it --rm -v "$PWD":/app -w /app openjdk:8 ./build.sh
```

## 9. Evolucao posterior

| Limitacao | Solucao posterior |
|-----------|-------------------|
| AWT inconsistente | Swing (1998) — componentes desenhados em Java puro, mesma aparencia em qualquer SO |
| Plugin de browser instavel | AJAX + JavaScript (1999-2005) — dispensa completamente o plugin |
| Interface limitada | HTML5 + CSS3 (2008+) — canvas, video, audio, animacoes nativas |
| Carregamento lento (JAR) | Bundlers modernos (Webpack, Vite) — code splitting, lazy loading |
| Ausencia de generics | Generics no JDK 1.5 (2004) — type safety em colecoes |
| Modelo de eventos JDK 1.0 | ActionListener no JDK 1.1 — delegacao de eventos tipada |

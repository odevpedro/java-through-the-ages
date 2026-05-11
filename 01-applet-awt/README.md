# Módulo 01 — Applet/AWT

> **Era:** meados de 1996 · **JDK:** 1.0 / 1.1 · **Paradigma:** OO + evento (AWT)

---

## 1. Contexto histórico

Em maio de 1995, a Sun Microsystems apresentou o Java na conferência SunWorld com uma
demonstração que sacudiu a indústria: um browser rodando código Java baixado da rede,
dentro do próprio navegador, sem instalação prévia. A plateia viu um objeto 3D girar
em tempo real numa página HTML. Naquele momento, a web era estática — apenas HTML e
imagens. A promessa do Java era transformá-la num ambiente de aplicações.

O modelo Applet era o veículo dessa promessa. Um desenvolvedor escrevia uma subclasse
de `java.applet.Applet`, compilava para bytecode, e qualquer usuário com o plugin Java
instalado podia executar aquele código diretamente no browser — em qualquer sistema
operacional. "Write Once, Run Anywhere" não era slogan de marketing; era uma proposta
técnica concreta para um problema real: a fragmentação de SO que tornava distribuição
de software cara e trabalhosa.

O toolkit de UI disponível era o AWT (Abstract Window Toolkit), lançado com o JDK 1.0.
O AWT delegava a renderização para os componentes nativos de cada SO (Win32 no Windows,
Motif no Unix, Mac Toolbox no Mac). Isso garantia aparência nativa mas sacrificava
consistência: um layout que ficava perfeito no Windows podia estar completamente
quebrado no Solaris. Essa inconsistência ficou famosa como "Write Once, Debug
Everywhere" — uma piada amarga que circulava entre desenvolvedores Java dos anos 1990.

---

## 2. Cenário de uso real

Applets eram usados em:

- **Interfaces de banco online** — formulários com validação no cliente, antes de AJAX
  ou JavaScript ser considerado confiável para isso.
- **Jogos simples** — xadrez, cartas, puzzles embutidos em portais de entretenimento.
- **Visualizações científicas** — gráficos interativos em publicações acadêmicas.
- **Ferramentas de configuração** — interfaces de administração de roteadores e switches
  de rede (Cisco, 3Com) embarcavam consoles de gerenciamento como Applets.
- **Sistemas financeiros internos** — terminais de trading, painéis de cotação em tempo
  real, onde a empresa controlava o browser e o plugin Java instalado.

Este sistema de mensagens representa o caso mais simples: um formulário com persistência
em memória, típico de intranets corporativas onde os usuários tinham o plugin instalado
e o IT controlava o ambiente.

---

## 3. Recursos do Java relevantes para a época

| Recurso | JDK | Relevância neste módulo |
|---|---|---|
| `java.applet.Applet` | 1.0 | Classe base; ciclo de vida `init/start/stop/destroy` |
| AWT (`java.awt.*`) | 1.0 | Único toolkit de UI; `Button`, `TextField`, `TextArea`, `Panel` |
| Modelo de eventos AWT 1.0 | 1.0 | `handleEvent(Event)` — **não usado aqui** (substituído no 1.1) |
| Modelo de delegação | 1.1 | `ActionListener`, `addActionListener()` — **usado aqui** |
| `java.util.Vector` | 1.0 | Única coleção dinâmica; `ArrayList` ainda não existia |
| `java.util.Enumeration` | 1.0 | Única forma de iterar coleções; `Iterator` veio no 1.2 |
| `java.util.Date` | 1.0 | Representação de instante; imutável e sem suporte a timezone |
| `StringBuffer` | 1.0 | Concatenação eficiente; `StringBuilder` veio no Java 5 |
| JAR (Java ARchive) | 1.1 | Empacotamento de múltiplos `.class` em um único arquivo |

---

## 4. Arquitetura e design

```
mensagens.html
    └── <applet code="mensagens.MensagemApplet" archive="mensagens.jar">
            │
            ▼
    MensagemApplet  (extends Applet, implements ActionListener)
            │
            ├── Vector mensagens          (estado em memória)
            ├── Panel formulário (NORTH)  ← TextField autor
            │                            ← TextArea conteudo
            │                            ← Button adicionar / limpar
            ├── Panel lista (CENTER)      ← TextArea somente leitura
            └── Label status (SOUTH)
                    │
                    └── Mensagem  (entidade: autor + conteudo + Date)
```

**Decisões de design da época:**

- `MensagemApplet` implementa `ActionListener` diretamente. Em projetos maiores
  era comum criar classes anônimas ou internas (inner classes, JDK 1.1) para
  cada listener, mas era idiomático o applet ser seu próprio listener em exemplos
  simples.

- Não existe separação entre camadas. O applet faz tudo: UI, validação, lógica de
  negócio e gerenciamento de estado. Esse padrão "God Class" era comum e aceitável
  em Applets pela simplicidade dos domínios que eles costumavam tratar.

- A classe `Mensagem` é uma entidade anêmica — sem lógica, só dados. Nessa era
  não havia discussão sobre rich domain model vs anemic model; o modelo anêmico
  era o padrão natural.

---

## 5. Limitações e dificuldades

### Sandbox de segurança
O browser impunha restrições severas ao código do Applet por padrão:
- **Sem acesso ao sistema de arquivos local** — impossível salvar dados sem
  comunicação com servidor.
- **Comunicação de rede apenas com o servidor de origem** — política de
  Same-Origin que antecede o CORS por 15 anos.
- **Sem acesso à área de transferência (clipboard)** em versões antigas do plugin.

Contornar o sandbox exigia **assinar digitalmente** o JAR — processo trabalhoso e
caro (certificados custavam centenas de dólares/ano em 1996).

### Inconsistência visual entre SOs
O AWT delegava para os componentes nativos do SO. `Button`, `TextField` e `TextArea`
tinham aparência e tamanho diferentes no Windows 95, no Solaris/Motif e no Mac OS 9.
Layouts que funcionavam num SO podiam estar quebrados em outro.

### Performance de carregamento
Sem JAR, o browser fazia uma requisição HTTP por arquivo `.class`. Uma aplicação com
20 classes gerava 20 round-trips de rede — catastrófico em modems 28.8k. O JDK 1.1
resolveu parcialmente com o formato JAR.

### Instabilidade do plugin
O plugin Java no browser era notório por travar e crashar. Desenvolvedores precisavam
testar em Netscape Navigator, Internet Explorer e Opera separadamente. A mensagem
"Java plugin not responding" era familiar para qualquer usuário de web dos anos 1990.

### Fim do modelo
O iPhone (2007) e o Android (2008) foram lançados sem suporte a Applets. O HTML5
(2014) tornou possível fazer em JavaScript o que antes exigia Java. Em 2015 a Oracle
oficializou a depreciação do plugin Java nos browsers. Em 2018, todos os browsers
maiores removeram suporte a NPAPI (interface do plugin Java). O JDK 11 (2018) removeu
`appletviewer` do JDK.

---

## 6. Build e execução

### Pré-requisito: JDK 8

O `appletviewer` foi removido do JDK a partir da versão 11. Use o JDK 8.

**Verificar versão:**
```bash
java -version
# deve mostrar: java version "1.8.x_xxx"
```

**Instalar JDK 8 (se necessário):**
- https://adoptium.net/temurin/releases/?version=8
- ou via SDKMAN: `sdk install java 8.0.392-tem`

### Opção via Docker (sem instalar JDK 8 localmente)

```bash
# Linux/macOS
docker run -it --rm \
    -v $(pwd):/app \
    -w /app \
    openjdk:8 \
    sh build.sh

# Nota: appletviewer exige display grafico.
# Para rodar a UI, adicione -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix
# (Linux com X11) ou use VNC.
```

### Executar localmente (JDK 8)

**Linux/macOS:**
```bash
chmod +x build.sh
./build.sh
```

**Windows:**
```bat
build.bat
```

O script executa 4 passos:
1. Limpa o diretório `out/`
2. Compila `src/mensagens/*.java` → `out/`
3. Empacota `out/` → `mensagens.jar`
4. Abre `appletviewer mensagens.html`

### Executar sem o script (passo a passo manual)

```bash
# 1. Compilar
javac -d out -sourcepath src src/mensagens/Mensagem.java src/mensagens/MensagemApplet.java

# 2. Empacotar
jar cvf mensagens.jar -C out .

# 3. Abrir
appletviewer mensagens.html
```

### Estrutura de arquivos após o build

```
01-applet-awt/
├── src/
│   └── mensagens/
│       ├── Mensagem.java
│       └── MensagemApplet.java
├── out/                     ← gerado pelo build
│   └── mensagens/
│       ├── Mensagem.class
│       └── MensagemApplet.class
├── mensagens.jar            ← gerado pelo build
├── mensagens.html
├── build.bat
└── build.sh
```

---

## 7. Evolução posterior

| Problema | Solução que veio depois |
|---|---|
| AWT inconsistente entre SOs | **Swing (JDK 1.2, 1998)** — componentes em Java puro, aparência consistente; Look and Feel configurável |
| Persistência impossível no sandbox | **AJAX + JSON (2005+)** — aplicações web com comunicação assíncrona com servidor |
| Plugin instável e dependente do browser | **JavaFX (2007)** como substituto de Applets; depois **HTML5/JavaScript** como alternativa sem plugin |
| Sem generics: casts explícitos com risco de ClassCastException | **Generics (Java 5, 2004)** — `Vector<Mensagem>` elimina o cast e adiciona verificação em tempo de compilação |
| `Enumeration` verbosa | **Iterator (JDK 1.2)** e depois **for-each (Java 5)** — `for (Mensagem m : mensagens)` |
| `java.util.Date` mutável e sem timezone | **`java.time` (Java 8, 2014)** — `LocalDateTime`, `ZonedDateTime`, `Instant`; imutáveis e timezone-aware |
| `StringBuffer` sincronizado sem necessidade | **`StringBuilder` (Java 5)** — idêntico, mas sem sincronização; recomendado para single-thread |
| God Class sem separação de camadas | **MVC, Spring MVC, JSF** — padrões e frameworks forçando separação Controller / Service / Repository |

O Swing resolveu o problema de aparência, mas o modelo mental de construir UI
linha a linha (imperativo) permaneceu até o JavaFX introduzir FXML (declarativo)
em 2011. As aplicações web modernas, por outro lado, separam completamente a lógica
(servidor) da apresentação (HTML/CSS/JS no cliente) — o oposto do modelo Applet,
onde código Java rodava no cliente mas dependia de plugin proprietário.

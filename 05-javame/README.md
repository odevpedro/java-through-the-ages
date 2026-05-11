# Módulo 05 — Java ME (Micro Edition)

> **Era:** meados de 2005 · **Plataforma:** MIDP 2.0 / CLDC 1.1
> **Dispositivo-alvo:** celular com 128–512 KB de RAM, tela de 96×65 a 176×220 pixels

---

## 1. Contexto histórico

Em 2005, o Java ME (Micro Edition) era o Java dominante em dispositivos móveis.
Mais de **1 bilhão de celulares** tinham suporte à plataforma — Nokia, Motorola,
Sony Ericsson e Samsung distribuíam jogos e aplicativos como arquivos JAR que o
usuário baixava via WAP ou cabo USB. O modelo de distribuição era simples: o operador
hospedava os JARs em um portal WAP, e o celular baixava e instalava diretamente.

O Java ME não era uma versão reduzida do Java SE. Era uma **plataforma separada**,
com suas próprias APIs, suas próprias restrições e seus próprios padrões. Desenvolver
para ela exigia abandonar a maior parte do que se sabia sobre J2SE: sem `java.io.File`,
sem `java.util.ArrayList`, sem reflection, sem generics. O ambiente era tão restrito
quanto programar sistemas embarcados — só que com uma linguagem orientada a objetos.

A chegada do **iPhone** (junho de 2007) e do **Android** (outubro de 2008) decretaram
o fim do Java ME como plataforma de aplicativos móveis. O iOS não suportava Java. O
Android usava Java como linguagem mas rodava sobre a Dalvik VM (não CLDC), com SDK
completamente diferente e acesso a hardware muito mais rico. Em 3 anos o ecossistema
Java ME de distribuição de apps evaporou.

---

## 2. Cenário de uso real

Java ME era usado em:

- **Jogos mobile** — Snake Nokia (pré-ME), depois Bomberman, FIFA, Tetris como JARs MIDP.
  As operadoras tinham lojas de jogos que geravam receita significativa (downloads pagos).
- **Clientes de e-mail e mensagem** — apps como Email Client e Opera Mini (navegador web
  completo em ~100 KB de JAR).
- **Aplicativos bancários** — alguns bancos distribuíam clientes Java ME para consulta
  de saldo e pagamentos, anos antes do mobile banking iOS/Android.
- **Aplicativos corporativos de campo** — vendedores com terminais de inventário,
  técnicos com formulários de serviço — aparelhos Symbian com Java ME.
- **GPS e mapas** — Google Maps Mobile era um MIDlet antes do Android.

---

## 3. Recursos do Java relevantes para a época

### O que existe no CLDC 1.1 (disponível neste módulo)

| Recurso | Equivalente J2SE | Observação |
|---|---|---|
| `java.lang.*` (limitado) | `java.lang.*` | Sem reflection, sem `Runtime.exec()` |
| `java.util.Vector` | `java.util.ArrayList` | Sincronizado; sem generics |
| `java.util.Hashtable` | `java.util.HashMap` | Sincronizado; sem generics |
| `java.util.Enumeration` | `java.util.Iterator` | JDK 1.0 |
| `java.util.Calendar` | `java.util.Date` / `java.time` | Único mecanismo de data |
| `java.io.DataInputStream/OutputStream` | `java.io.ObjectInputStream` | Serialização manual |
| `javax.microedition.rms.*` | `java.io.File` / JDBC | RecordStore: único storage local |
| `javax.microedition.lcdui.*` | `javax.swing.*` | UI de alto nível sem controle visual |
| `javax.microedition.midlet.MIDlet` | `javax.swing.JFrame` / `Servlet` | Ponto de entrada + ciclo de vida |

### O que NÃO existe no CLDC 1.1

- `java.util.ArrayList`, `java.util.HashMap`, `java.util.List`
- `java.io.File`, `java.io.FileInputStream`
- `java.lang.reflect.*` (reflection)
- `java.lang.ref.*` (WeakReference, SoftReference)
- Generics (Java 1.3 bytecode)
- `java.util.Date` (presente de forma limitada via `Calendar`)
- `java.text.SimpleDateFormat`
- `float` e `double` no CLDC 1.0 (adicionados no CLDC 1.1)

---

## 4. Arquitetura e design

```
[JAR + JAD]
    │
    ▼ AMS (Application Management Software do dispositivo)
┌─────────────────────────────────────────────────────────────┐
│  MensagemMidlet  (extends MIDlet)                           │
│    ├── startApp()   → Display.setCurrent(telaLista)         │
│    ├── pauseApp()   → [sem ação]                            │
│    └── destroyApp() → [sem ação; dados já no RMS]           │
│                                                             │
│  Display  (gerenciado pelo MIDlet)                          │
│    └── setCurrent(Displayable) → troca de tela              │
│                                                             │
│  TELAS (Displayable)                                        │
│    ├── TelaLista  (extends List)                            │
│    │     └── CommandListener → cmdAdicionar / cmdDetalhes   │
│    │                           cmdLimpar / cmdSair          │
│    ├── TelaAdicionar  (extends Form)                        │
│    │     ├── TextField autor                                │
│    │     ├── TextField conteudo                             │
│    │     └── CommandListener → cmdSalvar / cmdCancelar      │
│    └── Alert  (criado dinamicamente para detalhes e erros)  │
│                                                             │
│  RepositorioRMS                                             │
│    ├── salvar()   → RecordStore.addRecord(byte[])           │
│    ├── carregar() → RecordStore.enumerateRecords()          │
│    └── limparTudo() → RecordStore.deleteRecord(id) × N      │
│                                                             │
│  Mensagem (entidade)                                        │
│    └── serialização: DataOutputStream → byte[] (manual)    │
└─────────────────────────────────────────────────────────────┘
         │
    RecordStore "MensagensStore"
    (byte arrays no filesystem do dispositivo)
```

**Padrão de navegação manual:**

```
TelaLista ──[Adicionar]──> TelaAdicionar ──[Salvar/Cancelar]──> TelaLista
    │                                                                ▲
    └──[Ver item]──────> Alert (detalhe) ──[dismiss]────────────────┘
    └──[Limpar]────────> Alert (confirmação) ──[Sim/Não]────────────┘
```

Não há back stack automático. `MensagemMidlet` guarda referências para todas as
telas e chama `Display.setCurrent()` explicitamente. Era o padrão universal em Java ME.

---

## 5. Limitações e dificuldades

### Restrições de memória: cada byte importa

128 KB de heap para a aplicação inteira (em dispositivos de entrada de 2003-2005).
Práticas obrigatórias:

- Reutilizar instâncias de `Displayable` — recriar uma `TelaLista` a cada navegação
  era luxo que dispositivos antigos não podiam pagar.
- Usar `StringBuffer` em qualquer loop que construísse strings — concatenação com
  `+` cria objetos intermediários.
- Preferir `int` a `Integer`, `boolean` a `Boolean` — wrappers tinham overhead de objeto.
- Liberar referências com `null` quando não mais necessárias — o GC das VMs CLDC era
  menos sofisticado e pausas de GC eram visíveis ao usuário.

### `preverify`: a etapa mais confusa do build

O build Java ME tinha 4 etapas obrigatórias: `javac` → `preverify` → `jar` → deploy.
Esquecer o `preverify` resultava em:

```
javax.microedition.midlet.MIDletStateChangeException: Verification error
```

...uma das mensagens de erro mais crípticas e mal documentadas da plataforma.
O `preverify` existia porque as KVMs (K Virtual Machines) dos celulares não tinham
CPU suficiente para verificar bytecode em runtime. A verificação era feita offline
no computador do desenvolvedor.

### Fragmentação: o pesadelo de compatibilidade

MIDP 2.0 definia um conjunto mínimo de APIs garantidas. Mas cada fabricante adicionava
extensões proprietárias e limitações próprias:

- Tamanho do RecordStore: 8 KB no Nokia 3100, 64 KB no Sony Ericsson K700i.
- Suporte a rede: operadoras bloqueavam conexões HTTP de alguns MIDlets.
- Teclas físicas: mapeamento de `Command` variava por modelo.
- Tamanho de tela: 96×65, 128×128, 176×208, 240×320 pixels — todos no mesmo "MIDP 2.0".

Times sérios mantinham uma "matriz de compatibilidade" e testavam em dezenas de
modelos. Alguns fabricantes (Nokia principalmente) disponibilizavam emuladores por
modelo. Era a fragmentação de Android multiplicada por 10 — sem ferramentas de teste
automatizado, sem CI/CD, testado manualmente com dispositivos físicos.

### Serialização manual: sem esquema, sem versionamento

O formato de byte[] no RecordStore era implícito: definido pela ordem de
`writeUTF/writeLong` em `serializar()`. Qualquer mudança — adicionar um campo,
trocar a ordem — corromperia silenciosamente todos os registros salvos de versões
anteriores. Não havia migração automática, não havia schema evolution.

Se um usuário atualizasse o app e o formato do RecordStore tivesse mudado, os dados
antigos seriam lidos como lixo. A prática era versionar o RecordStore com um número
de versão como primeiro byte e tratar cada versão explicitamente.

### Teclado T9 e UX minimalista

Inserir texto com teclado numérico T9 era a realidade do usuário. "Ada" exigia
pressionar 2 (A), 33 (d), 2 (a) — com pausas entre teclas para distinguir "33" de "d"
vs "3" de "e". Formulários longos eram dolorosos. A UX ideal para Java ME era
**ações em um toque**, listas de seleção, e mínima entrada de texto livre.

---

## 6. Build e execução

### Configuração de ambiente (passo mais trabalhoso)

O módulo 05 é o único que exige ferramentas fora do JDK padrão.

**Opção A: Sun Wireless Toolkit 2.5 (ambiente histórico completo)**

1. Baixe o WTK 2.5.2 em:
   `https://www.oracle.com/java/technologies/java-archive-downloads.html`
   (busque "Sun Java Wireless Toolkit 2.5")
2. Instale em `/opt/WTK2.5` (Linux/Mac) ou `C:\WTK2.5` (Windows)
3. Execute:
   ```bash
   export WTK_HOME=/opt/WTK2.5
   sh build.sh
   $WTK_HOME/bin/emulator -Xdescriptor:mensagens.jad
   ```

**Opção B: MicroEmu (emulador moderno, mais fácil)**

MicroEmu é um emulador Java ME de código aberto que roda como aplicação J2SE:
```bash
# 1. Baixe MicroEmu: https://github.com/barteo/microemu/releases
# 2. Coloque os JARs das APIs ME em lib/
wget https://repo1.maven.org/maven2/org/microemu/midp/2.0.4/midp-2.0.4.jar -O lib/midp-api.jar
wget https://repo1.maven.org/maven2/org/microemu/cldc/1.0.3/cldc-1.0.3.jar  -O lib/cldc-api.jar

# 3. Compilar (sem preverify para MicroEmu)
javac -bootclasspath "lib/cldc-api.jar:lib/midp-api.jar" \
      -d out -sourcepath src src/mensagens/*.java

jar cvfm mensagens.jar MANIFEST.MF -C out .

# 4. Executar no MicroEmu
java -jar microemu-javase-runner.jar mensagens.jar
```

**Opção C: Docker com ambiente legado**

```bash
docker run -it --rm \
    -v $(pwd):/app \
    -w /app \
    -e DISPLAY=$DISPLAY \
    -v /tmp/.X11-unix:/tmp/.X11-unix \
    openjdk:8 \
    sh build.sh
```

### Pipeline de build

```bash
# 1. Compilar (com bootclasspath ME — sem isso aceita APIs J2SE que não existem no dispositivo)
javac -source 1.3 -target 1.1 \
      -bootclasspath "$CLDC_JAR:$MIDP_JAR" \
      -d out -sourcepath src src/mensagens/*.java

# 2. Preverificar (obrigatório para dispositivos reais)
preverify -classpath "$CLDC_JAR:$MIDP_JAR:out" -d out-preverified out

# 3. Empacotar com MANIFEST
jar cvfm mensagens.jar MANIFEST.MF -C out-preverified .

# 4. Atualizar JAD com tamanho real do JAR
echo "MIDlet-Jar-Size: $(wc -c < mensagens.jar)" >> mensagens.jad
```

### Estrutura após o build

```
05-javame/
├── src/mensagens/
│   ├── Mensagem.java
│   ├── RepositorioRMS.java
│   ├── TelaLista.java
│   ├── TelaAdicionar.java
│   └── MensagemMidlet.java
├── out/                       ← .class compilados
├── out-preverified/           ← .class preverificados (deploy real)
├── mensagens.jar              ← MIDlet suite (deploy)
├── mensagens.jad              ← Descriptor (deploy junto com o JAR)
├── build.bat
└── build.sh
```

---

## 7. Evolução posterior

| Problema do Java ME | Solução que veio depois |
|---|---|
| CLDC/MIDP — APIs extremamente restritas | **Android SDK (2008)** com acesso completo a hardware, câmera, GPS, sensores |
| `RecordStore` sem schema nem SQL | **SQLite (Android)** via `android.database.sqlite` — banco completo no dispositivo |
| UI LCDUI sem controle de pixels | **Android XML layouts** (2008) → **Jetpack Compose** (2021) — UI declarativa e responsiva |
| `preverify` como etapa manual obrigatória | **Android Gradle** — build automatizado sem etapas manuais de verificação |
| Fragmentação extrema de dispositivos | **Android** reduziu (não eliminou) a fragmentação; **React Native / Flutter** abstraem ainda mais |
| Distribuição via portal WAP da operadora | **App Store** (2008) / **Google Play** (2012) — lojas centralizadas, descoberta e updates automáticos |
| Teclado T9 — entrada de texto dolorosa | **Telas touch** (iPhone 2007) tornaram a entrada de texto fluida; acabaram com o T9 |
| `javac -source 1.3` — sem generics, sem lambdas | **Android suporta Java 8+ (com desugaring)** desde 2017; **Kotlin** (2017) como linguagem principal |
| Heap de 128 KB | **Smartphones modernos** têm 4–16 GB de RAM — 100.000× mais |

O legado do Java ME é paradoxal. Tecnicamente, foi superado completamente pelo Android
em menos de 3 anos. Culturalmente, demonstrou que **Java em dispositivos móveis era
viável**, preparando o terreno para o Android. E didaticamente, não há nada que ensine
melhor sobre restrições de ambiente que escrever código onde cada objeto criado é uma
decisão consciente de trade-off.

O módulo 05 fecha o museu no ponto de inflexão: 2005 é o ano em que o Java ME atinge
seu pico de adoção — e 2007 é o ano em que o iPhone começa a torná-lo obsoleto. O
próximo passo histórico que este repositório não cobre — Android, Spring Boot, Jakarta
EE — é onde a maior parte da documentação moderna começa. Este museu termina exatamente
onde a maioria dos tutoriais começa.

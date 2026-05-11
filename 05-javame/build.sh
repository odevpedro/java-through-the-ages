#!/bin/sh
# =============================================================================
#  build.sh - Modulo 05: Java ME (MIDP 2.0 / CLDC 1.1)
#
#  Pipeline de build Java ME:
#    javac (com classpath ME) -> preverify -> jar -> atualizar .jad
#
#  =========================================================================
#  PRE-REQUISITOS
#  =========================================================================
#
#  1. JDK 8 ou inferior (javac)
#     O compilador deve gerar bytecode nivel 1.1 ou 1.2 (CLDC exige isso).
#     JDK 8 suporta: javac -source 1.3 -target 1.1
#
#  2. Sun Wireless Toolkit (WTK) 2.5 OU arquivo cldc-api.jar / midp-api.jar
#     O WTK inclui:
#       - cldc-api.jar     : APIs do CLDC 1.1 (java.lang, java.util limitado, etc.)
#       - midp-api.jar     : APIs do MIDP 2.0 (javax.microedition.*)
#       - preverify        : verificador de bytecode (obrigatorio)
#       - emulador         : para testar sem dispositivo fisico
#
#     Opcao A (WTK instalado):
#       Defina WTK_HOME=/caminho/para/wtk2.5
#       O script usa $WTK_HOME/lib/*.jar e $WTK_HOME/bin/preverify
#
#     Opcao B (JARs separados sem WTK):
#       Coloque cldc-api.jar e midp-api.jar em lib/
#       E o binario 'preverify' em lib/ ou no PATH
#
#     Onde obter:
#       - WTK 2.5.2: https://www.oracle.com/java/technologies/java-archive-downloads.html
#         (buscar por "Sun Java Wireless Toolkit")
#       - JARs alternatives no Maven:
#         mvn dependency:get -Dartifact=javax.microedition:midp:2.0
#         (pode nao estar disponivel no Central; tente jars.vip ou repositorios legados)
#       - Emuladores modernos: MicroEmu, J2ME Runner, ou Docker com WTK
#
#  3. NOTA SOBRE PREVERIFY:
#     O preverify e um verificador de bytecode extra exigido pelo CLDC.
#     Dispositivos MIDP tem JVMs simplificadas (KVM) que NAO verificam
#     bytecode em runtime (economizaria tempo de CPU e RAM). O preverify
#     faz essa verificacao offline, durante o build, gerando .class
#     com atributos extras que o KVM aceita sem verificar.
#     Sem o preverify, a aplicacao sera rejeitada pelo emulador ou
#     pelo dispositivo com "Verification error" ou "Invalid JAR".
# =============================================================================

set -e

# --- configuracao ---
WTK_HOME=${WTK_HOME:-/opt/WTK2.5}
CLDC_JAR=${CLDC_JAR:-$WTK_HOME/lib/cldcapi11.jar}
MIDP_JAR=${MIDP_JAR:-$WTK_HOME/lib/midpapi20.jar}
PREVERIFY=${PREVERIFY:-$WTK_HOME/bin/preverify}

# fallback: JARs em lib/ local
if [ ! -f "$CLDC_JAR" ] && [ -f "lib/cldc-api.jar" ]; then
    CLDC_JAR="lib/cldc-api.jar"
fi
if [ ! -f "$MIDP_JAR" ] && [ -f "lib/midp-api.jar" ]; then
    MIDP_JAR="lib/midp-api.jar"
fi
if [ ! -x "$PREVERIFY" ] && command -v preverify > /dev/null 2>&1; then
    PREVERIFY="preverify"
fi

# --- verificar pre-requisitos ---
if [ ! -f "$CLDC_JAR" ]; then
    echo "ERRO: cldc-api.jar nao encontrado."
    echo "  Esperado em: $CLDC_JAR"
    echo "  Ou coloque em lib/cldc-api.jar"
    echo "  Ver README.md secao 'Configuracao de ambiente' para instrucoes."
    exit 1
fi
if [ ! -f "$MIDP_JAR" ]; then
    echo "ERRO: midp-api.jar nao encontrado."
    echo "  Esperado em: $MIDP_JAR"
    echo "  Ou coloque em lib/midp-api.jar"
    exit 1
fi
if [ ! -x "$PREVERIFY" ] && [ "$PREVERIFY" = "$WTK_HOME/bin/preverify" ]; then
    echo "AVISO: preverify nao encontrado em $PREVERIFY"
    echo "  O JAR sera gerado SEM preverificacao."
    echo "  Funcionara no emulador MicroEmu mas pode falhar em dispositivos reais."
    PREVERIFY=""
fi

echo "[1/5] Limpando diretorios de saida..."
rm -rf out out-preverified mensagens.jar
mkdir -p out out-preverified

echo "[2/5] Compilando fontes Java..."
#
# -bootclasspath: SUBSTITUI o classpath do JDK host pelo classpath CLDC/MIDP.
# Isso impede que o compilador aceite classes J2SE que nao existem no CLDC
# (ex: java.io.File, java.util.ArrayList, java.lang.reflect.*).
# Sem isso, o codigo compilaria mas falharia em runtime no dispositivo.
#
# -source 1.3 -target 1.1:
# CLDC KVM executa bytecode de nivel 1.1/1.2.
# Generics (Java 5) e anotacoes (Java 5) seriam rejeitados pelo KVM.
# Em JDKs modernos (9+) essas flags podem nao ser suportadas —
# use JDK 8 para maxima compatibilidade.
#
javac \
    -source 1.3 \
    -target 1.1 \
    -bootclasspath "$CLDC_JAR:$MIDP_JAR" \
    -d out \
    -sourcepath src \
    src/mensagens/Mensagem.java \
    src/mensagens/RepositorioRMS.java \
    src/mensagens/TelaLista.java \
    src/mensagens/TelaAdicionar.java \
    src/mensagens/MensagemMidlet.java

echo "[3/5] Preverificando bytecode..."
#
# preverify analisa os .class e adiciona atributos "StackMap" que
# permitem ao KVM do dispositivo pular a verificacao em runtime.
# Entrada: diretorio 'out' (classes compiladas)
# Saida: diretorio 'out-preverified' (classes preverificadas)
#
if [ -n "$PREVERIFY" ]; then
    $PREVERIFY \
        -classpath "$CLDC_JAR:$MIDP_JAR:out" \
        -d out-preverified \
        out
    CLASS_DIR="out-preverified"
    echo "  Preverificacao concluida."
else
    echo "  AVISO: preverify nao disponivel. Usando classes nao preverificadas."
    CLASS_DIR="out"
fi

echo "[4/5] Empacotando JAR com MANIFEST.MF..."
#
# O MANIFEST.MF do JAR Java ME deve conter os mesmos campos do .jad.
# O AMS verifica a consistencia entre .jad e MANIFEST.MF.
# Qualquer discrepancia (ex: versao diferente) causa falha de instalacao.
#
# MIDlet-Name e MIDlet-1 sao obrigatorios no MANIFEST.MF.
# Sem eles, o JAR nao sera reconhecido como uma MIDlet suite.
#
cat > MANIFEST.MF << 'EOF'
MIDlet-Name: Mensagens
MIDlet-Version: 1.0.0
MIDlet-Vendor: Java Through the Ages
MIDlet-1: Mensagens,, mensagens.MensagemMidlet
MicroEdition-Profile: MIDP-2.0
MicroEdition-Configuration: CLDC-1.1
EOF

jar cvfm mensagens.jar MANIFEST.MF -C $CLASS_DIR .
rm MANIFEST.MF

echo "[5/5] Atualizando tamanho do JAR no .jad..."
#
# O campo MIDlet-Jar-Size no .jad deve conter o tamanho EXATO do JAR.
# O dispositivo verifica isso antes de baixar o JAR. Uma discrepancia
# resulta em: "Invalid JAR size" ou simplesmente rejeicao silenciosa.
#
# Esta e uma das particularidades mais irritantes do build Java ME:
# o .jad tinha que ser atualizado manualmente apos cada build.
# Ferramentas como o Ant tinham tarefas especificas para isso.
#
JAR_SIZE=$(wc -c < mensagens.jar | tr -d ' ')
# Substituir o valor de MIDlet-Jar-Size no .jad
sed -i.bak "s/^MIDlet-Jar-Size:.*/MIDlet-Jar-Size: $JAR_SIZE/" mensagens.jad
rm -f mensagens.jad.bak

echo ""
echo "============================================================"
echo " Build concluido!"
echo " JAR: mensagens.jar ($JAR_SIZE bytes)"
echo " JAD: mensagens.jad (atualizado)"
echo ""
echo " Para executar no emulador:"
echo "   $WTK_HOME/bin/emulator -Xdescriptor:mensagens.jad"
echo "   (ou use MicroEmu: java -jar microemu.jar mensagens.jar)"
echo ""
echo " Para executar com MicroEmu standalone:"
echo "   java -jar microemu-javase-runner.jar mensagens.jar"
echo "   (baixe em: https://github.com/barteo/microemu)"
echo "============================================================"

#!/bin/sh
# =============================================================================
#  build.sh - Modulo 01: Applet/AWT
#  Compila os fontes Java, empacota em JAR e abre o appletviewer.
#
#  Requisitos:
#    - JDK 8 (ultimo JDK com appletviewer incluido)
#    - java, javac, jar e appletviewer no PATH
#
#  Para verificar a versao do JDK:
#    java -version
#    appletviewer -version   (se retornar "command not found", use JDK 8)
# =============================================================================

set -e  # interrompe o script em qualquer erro

echo "[1/4] Limpando diretorio de saida..."
rm -rf out
mkdir -p out

echo "[2/4] Compilando fontes..."
#
# Em 1996 o desenvolvedor executava javac manualmente.
# Nao havia ferramenta de build automatizada — Ant foi lancado em 2000,
# Maven em 2004, Gradle em 2012.
#
# As flags -source 1.1 / -target 1.1 garantem fidelidade historica.
# Se o seu JDK rejeitar essas flags (JDK 9+), remova-as.
#
javac \
    -d out \
    -sourcepath src \
    src/mensagens/Mensagem.java \
    src/mensagens/MensagemApplet.java

echo "[3/4] Empacotando em JAR..."
#
# O formato JAR e basicamente um ZIP com um MANIFEST.MF.
# Foi introduzido no JDK 1.1 para permitir que o browser baixasse
# todos os .class de uma aplicacao em uma unica requisicao HTTP,
# reduzindo dramaticamente o tempo de carregamento em conexoes lentas.
#
jar cvf mensagens.jar -C out .

echo "[4/4] Abrindo appletviewer..."
#
# appletviewer le a tag <applet> do HTML e instancia o applet.
# Se o comando nao for encontrado, seu JDK e mais recente que a versao 8.
# Opcoes para JDK 11+:
#   a) Instalar JDK 8 paralelamente e usar JAVA_HOME=/path/to/jdk8
#   b) Usar Docker: docker run -it --rm openjdk:8 (montar o diretorio)
#
appletviewer mensagens.html

echo ""
echo "Build concluido. JAR gerado: mensagens.jar"
echo "Para reabrir o applet sem recompilar: appletviewer mensagens.html"

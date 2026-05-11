#!/bin/sh
# =============================================================================
#  build.sh - Modulo 02: Swing Desktop
#
#  Requisitos:
#    - JDK 8 ou superior
#    - javac e java no PATH
#
#  Diferente do modulo 01 (Applet), este modulo e uma aplicacao desktop
#  autonoma. Nao precisa de browser, plugin ou servidor.
#  Execute em qualquer maquina com JDK e variaveis DISPLAY configuradas
#  (necessario em servidores Linux sem interface grafica — use VNC ou
#  exporte DISPLAY para um X server).
# =============================================================================

set -e

echo "[1/4] Limpando diretorio de saida..."
rm -rf out
mkdir -p out

echo "[2/4] Compilando fontes..."
javac -d out -sourcepath src \
    src/mensagens/Mensagem.java \
    src/mensagens/RepositorioMensagens.java \
    src/mensagens/MensagemListRenderer.java \
    src/mensagens/JanelaPrincipal.java

echo "[3/4] Empacotando em JAR executavel..."
#
# Criar MANIFEST.MF temporario com a classe principal.
# O atributo Main-Class torna o JAR executavel via 'java -jar'.
# Introducao no JDK 1.2 como parte da especificacao do formato JAR.
#
echo "Main-Class: mensagens.JanelaPrincipal" > MANIFEST.MF
jar cvfm mensagens-swing.jar MANIFEST.MF -C out .
rm MANIFEST.MF

echo "[4/4] Iniciando aplicacao Swing..."
#
# Em servidores Linux sem display grafico, este comando falhara com:
#   "java.awt.HeadlessException: No X11 DISPLAY variable was set"
# Para rodar nesse ambiente:
#   export DISPLAY=:0  (se houver X server local)
#   ou use VNC / X forwarding via SSH: ssh -X usuario@host
#
java -jar mensagens-swing.jar

echo ""
echo "Build concluido."
echo "Para reabrir sem recompilar: java -jar mensagens-swing.jar"
echo "Dados persistidos em: mensagens.dat"

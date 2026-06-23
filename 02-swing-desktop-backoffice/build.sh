#!/bin/sh
# =============================================================================
#  build.sh - Modulo 02: Swing Desktop Backoffice
#
#  Requisitos: JDK 8+; javac e java no PATH.
# =============================================================================

set -e

echo "[1/4] Limpando diretorio de saida..."
rm -rf out
mkdir -p out

echo "[2/4] Compilando fontes..."
javac -d out -sourcepath src \
    src/mensagens/Cliente.java \
    src/mensagens/RepositorioCliente.java \
    src/mensagens/CadastroClientesFrame.java \
    src/mensagens/Main.java

echo "[3/4] Empacotando em JAR executavel..."
echo "Main-Class: mensagens.Main" > MANIFEST.MF
jar cvfm mensagens-swing.jar MANIFEST.MF -C out .
rm MANIFEST.MF

echo "[4/4] Iniciando aplicacao Swing..."
java -jar mensagens-swing.jar

echo ""
echo "Build concluido."
echo "Para reabrir sem recompilar: java -jar mensagens-swing.jar"
echo "Dados persistidos em: clientes.dat"

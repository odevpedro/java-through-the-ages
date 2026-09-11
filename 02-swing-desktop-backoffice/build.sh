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
    src/cadastro/Cliente.java \
    src/cadastro/RepositorioCliente.java \
    src/cadastro/CadastroClientesFrame.java \
    src/cadastro/Main.java

echo "[3/4] Empacotando em JAR executavel..."
echo "Main-Class: cadastro.Main" > MANIFEST.MF
jar cvfm cadastro-swing.jar MANIFEST.MF -C out .
rm MANIFEST.MF

echo "[4/4] Iniciando aplicacao Swing..."
java -jar cadastro-swing.jar

echo ""
echo "Build concluido."
echo "Para reabrir sem recompilar: java -jar cadastro-swing.jar"
echo "Dados persistidos em: clientes.dat"

#!/bin/sh
# =============================================================================
#  build.sh - Modulo 01: Applet/AWT — Catalogo de Produtos
#
#  Compila os fontes, empacota em JAR e abre o appletviewer.
#
#  Requisitos:
#    - JDK 8 (ultimo JDK com appletviewer incluido)
#    - java, javac, jar e appletviewer no PATH
# =============================================================================

set -e

echo "[1/4] Limpando diretorio de saida..."
rm -rf out
mkdir -p out

echo "[2/4] Compilando fontes..."
javac \
    -d out \
    -sourcepath src \
    src/mensagens/Produto.java \
    src/mensagens/CatalogoApplet.java

echo "[3/4] Empacotando em JAR..."
jar cvf catalogo.jar -C out .

echo "[4/4] Abrindo appletviewer..."
appletviewer catalogo.html

echo ""
echo "Build concluido. JAR gerado: catalogo.jar"
echo "Para reabrir sem recompilar: appletviewer catalogo.html"

#!/bin/sh
# build.sh - Compila e executa a simulacao do modulo 05
# Uso: ./build.sh

set -e

echo "=== Compilando classes ==="
rm -rf out
mkdir -p out
javac -d out -sourcepath src src/banco/*.java

echo ""
echo "=== Executando SimuladorTransferencia ==="
echo ""
java -cp out banco.SimuladorTransferencia

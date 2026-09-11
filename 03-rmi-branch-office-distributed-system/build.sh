#!/bin/sh
# =============================================================================
#  build.sh - Modulo 03: RMI Branch Office
#
#  Apenas compila e empacota. Para executar, use:
#    Terminal 1:  sh start-servidor.sh   (manter rodando)
#    Terminal 2:  sh start-cliente.sh    (executar apos o servidor)
#
#  Requisitos: JDK 8 ou superior; javac, jar, rmic, java no PATH.
# =============================================================================

set -e

echo "[1/4] Limpando diretorios de saida..."
rm -rf out estoque-servidor.jar estoque-cliente.jar
mkdir -p out

echo "[2/4] Compilando fontes..."
javac -d out -sourcepath src \
    src/estoque/Produto.java \
    src/estoque/Estoque.java \
    src/estoque/EstoqueImpl.java \
    src/estoque/ServidorEstoque.java \
    src/estoque/ClienteFilial.java

echo "[3/4] Gerando stubs com rmic (opcional em JDK 5+)..."
if command -v rmic > /dev/null 2>&1; then
    rmic -d out -classpath out estoque.EstoqueImpl
    echo "  rmic executado com sucesso."
else
    echo "  AVISO: rmic nao encontrado. Em JDK 5+ stubs sao dinamicos; continuando."
fi

echo "[4/4] Empacotando JARs..."

echo "Main-Class: estoque.ServidorEstoque" > MANIFEST-srv.MF
jar cvfm estoque-servidor.jar MANIFEST-srv.MF -C out .
rm MANIFEST-srv.MF

echo "Main-Class: estoque.ClienteFilial" > MANIFEST-cli.MF
jar cvfm estoque-cliente.jar MANIFEST-cli.MF -C out .
rm MANIFEST-cli.MF

echo ""
echo "============================================================"
echo " Build concluido!"
echo " Proximos passos (em terminais SEPARADOS):"
echo "   1. sh start-servidor.sh   (manter rodando)"
echo "   2. sh start-cliente.sh    (em outro terminal)"
echo "============================================================"

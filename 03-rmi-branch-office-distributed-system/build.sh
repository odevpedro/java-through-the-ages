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
rm -rf out mensagens-servidor.jar mensagens-cliente.jar
mkdir -p out

echo "[2/4] Compilando fontes..."
javac -d out -sourcepath src \
    src/mensagens/Produto.java \
    src/mensagens/Estoque.java \
    src/mensagens/EstoqueImpl.java \
    src/mensagens/ServidorEstoque.java \
    src/mensagens/ClienteFilial.java

echo "[3/4] Gerando stubs com rmic (opcional em JDK 5+)..."
if command -v rmic > /dev/null 2>&1; then
    rmic -d out -classpath out mensagens.EstoqueImpl
    echo "  rmic executado com sucesso."
else
    echo "  AVISO: rmic nao encontrado. Em JDK 5+ stubs sao dinamicos; continuando."
fi

echo "[4/4] Empacotando JARs..."

echo "Main-Class: mensagens.ServidorEstoque" > MANIFEST-srv.MF
jar cvfm mensagens-servidor.jar MANIFEST-srv.MF -C out .
rm MANIFEST-srv.MF

echo "Main-Class: mensagens.ClienteFilial" > MANIFEST-cli.MF
jar cvfm mensagens-cliente.jar MANIFEST-cli.MF -C out .
rm MANIFEST-cli.MF

echo ""
echo "============================================================"
echo " Build concluido!"
echo " Proximos passos (em terminais SEPARADOS):"
echo "   1. sh start-servidor.sh   (manter rodando)"
echo "   2. sh start-cliente.sh    (em outro terminal)"
echo "============================================================"

#!/bin/sh
# =============================================================================
#  build.sh - Modulo 03: RMI
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
    src/mensagens/Mensagem.java \
    src/mensagens/ServicoMensagens.java \
    src/mensagens/ServicoMensagensImpl.java \
    src/mensagens/Servidor.java \
    src/mensagens/Cliente.java

echo "[3/4] Gerando stubs com rmic..."
#
# rmic gera os stubs (e skeletons, em JDKs antigos).
# Em JDK 5+ os stubs sao gerados dinamicamente; rmic e opcional.
# Em JDK 9+ rmic foi depreciado; em JDK 15 skeletons foram removidos.
# Para maximo de fidelidade historica (JDK 1.1/1.2), rmic era obrigatorio.
#
if command -v rmic > /dev/null 2>&1; then
    rmic -d out -classpath out mensagens.ServicoMensagensImpl
    echo "  rmic executado com sucesso."
else
    echo "  AVISO: rmic nao encontrado. Em JDK 5+ stubs sao dinamicos; continuando."
fi

echo "[4/4] Empacotando JARs..."

echo "Main-Class: mensagens.Servidor" > MANIFEST-srv.MF
jar cvfm mensagens-servidor.jar MANIFEST-srv.MF -C out .
rm MANIFEST-srv.MF

echo "Main-Class: mensagens.Cliente" > MANIFEST-cli.MF
jar cvfm mensagens-cliente.jar MANIFEST-cli.MF -C out .
rm MANIFEST-cli.MF

echo ""
echo "============================================================"
echo " Build concluido!"
echo " Proximos passos (em terminais SEPARADOS):"
echo "   1. sh start-servidor.sh   (manter rodando)"
echo "   2. sh start-cliente.sh    (em outro terminal)"
echo "============================================================"

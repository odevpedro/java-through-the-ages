#!/bin/sh
# =============================================================================
#  start-servidor.sh - inicia o servidor RMI
#
#  Execute em um terminal dedicado e mantenha-o rodando.
#  Para encerrar: Ctrl+C
# =============================================================================

if [ ! -f estoque-servidor.jar ]; then
    echo "ERRO: estoque-servidor.jar nao encontrado."
    echo "Execute primeiro: sh build.sh"
    exit 1
fi

echo "Iniciando servidor RMI..."
echo "(Mantenha este terminal aberto. Use Ctrl+C para encerrar.)"
echo ""

java -Djava.rmi.server.hostname=localhost \
     -jar estoque-servidor.jar

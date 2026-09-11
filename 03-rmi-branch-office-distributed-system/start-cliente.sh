#!/bin/sh
# =============================================================================
#  start-cliente.sh - executa o cliente RMI
#
#  Requer que o servidor esteja rodando (start-servidor.sh em outro terminal).
#  Pode ser executado multiplas vezes.
# =============================================================================

if [ ! -f estoque-cliente.jar ]; then
    echo "ERRO: estoque-cliente.jar nao encontrado."
    echo "Execute primeiro: sh build.sh"
    exit 1
fi

echo "Executando cliente RMI..."
echo ""

java -jar estoque-cliente.jar

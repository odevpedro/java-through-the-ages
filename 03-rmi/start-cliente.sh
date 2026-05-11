#!/bin/sh
# =============================================================================
#  start-cliente.sh - executa o cliente RMI
#
#  Requer que o servidor esteja rodando (start-servidor.sh em outro terminal).
#  Pode ser executado multiplas vezes; cada execucao e independente.
# =============================================================================

if [ ! -f mensagens-cliente.jar ]; then
    echo "ERRO: mensagens-cliente.jar nao encontrado."
    echo "Execute primeiro: sh build.sh"
    exit 1
fi

echo "Executando cliente RMI..."
echo ""

java -jar mensagens-cliente.jar

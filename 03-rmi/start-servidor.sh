#!/bin/sh
# =============================================================================
#  start-servidor.sh - inicia o servidor RMI
#
#  Execute em um terminal dedicado e mantenha-o rodando.
#  O servidor imprime logs de cada chamada recebida.
#
#  Para encerrar: Ctrl+C
# =============================================================================

if [ ! -f mensagens-servidor.jar ]; then
    echo "ERRO: mensagens-servidor.jar nao encontrado."
    echo "Execute primeiro: sh build.sh"
    exit 1
fi

echo "Iniciando servidor RMI..."
echo "(Mantenha este terminal aberto. Use Ctrl+C para encerrar.)"
echo ""

#
# -Djava.rmi.server.hostname=localhost
#   Instrui o servidor a anunciar 'localhost' como seu endereco no stub.
#   Sem isso, em algumas configuracoes de rede o servidor anuncia um IP
#   que o cliente nao consegue alcançar, causando ConnectException mesmo
#   com servidor e cliente na mesma maquina.
#   Era uma das configuracoes de rede mais confusas do RMI.
#
java -Djava.rmi.server.hostname=localhost \
     -jar mensagens-servidor.jar

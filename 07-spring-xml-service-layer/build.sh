#!/bin/bash
# Module 07 - Spring XML Service Layer
# Downloads Spring 4.3.x JARs e compila/executa via Maven

echo "=== Module 07: Spring XML Service Layer ==="

# Verifica se Maven esta disponivel
if ! command -v mvn &> /dev/null; then
    echo "Maven nao encontrado. Instale o Maven ou baixe as dependencias manualmente."
    exit 1
fi

echo "Baixando dependencias e compilando..."
mvn compile -q

echo "Executando aplicacao..."
mvn exec:java -q

#!/bin/sh
# =============================================================================
#  download-deps.sh — Baixa as dependencias necessarias para o modulo 04
#
#  Requer: curl ou wget no PATH, e conexao com a internet.
#
#  Os JARs sao salvos em lib/ e NAO devem ser commitados no repositorio.
#  Em projetos reais de 2001, os JARs eram frequentemente commitados
#  (nao havia Maven centralizado) — pratica que causava repositorios
#  enormes e conflitos de versao entre projetos.
# =============================================================================

set -e
mkdir -p lib

echo "Baixando HSQLDB 2.5.2..."
curl -L -o lib/hsqldb.jar \
    "https://repo1.maven.org/maven2/org/hsqldb/hsqldb/2.5.2/hsqldb-2.5.2.jar"

echo "Baixando Servlet API 3.1.0..."
#
# Usamos Servlet 3.1 para compatibilidade com Tomcat 8/9 modernos.
# O codigo usa apenas APIs Servlet 2.3, entao e totalmente compativel.
# Servlet API 2.3 original nao esta disponivel no Maven Central facilmente.
#
curl -L -o lib/servlet-api.jar \
    "https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar"

echo ""
echo "Dependencias baixadas em lib/:"
ls -lh lib/
echo ""
echo "Execute agora: sh build.sh"

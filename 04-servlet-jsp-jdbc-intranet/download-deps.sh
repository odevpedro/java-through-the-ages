#!/bin/sh
# download-deps.sh - Baixa HSQLDB para o modulo 04

set -e

mkdir -p lib

echo "=== Baixando HSQLDB 2.7.1 ==="
curl -L -o lib/hsqldb.zip \
    "https://sourceforge.net/projects/hsqldb/files/hsqldb/hsqldb_2_7/hsqldb-2.7.1.zip/download"

echo "=== Extraindo JAR ==="
unzip -o -j lib/hsqldb.zip "hsqldb-2.7.1/hsqldb/lib/hsqldb.jar" -d lib/

echo "=== Limpando zip ==="
rm -f lib/hsqldb.zip

echo "=== HSQLDB pronto em lib/hsqldb.jar ==="

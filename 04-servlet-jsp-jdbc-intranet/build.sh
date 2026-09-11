#!/bin/sh
# build.sh - Compila o modulo 04 e gera o WAR
# Uso: ./build.sh

set -e

MODULO="04-servlet-jsp-jdbc-intranet"
TOMCAT_LIB="$HOME/tomcat/lib"
HSQLDB_JAR="lib/hsqldb.jar"

echo "=== Limpando builds anteriores ==="
rm -rf build
mkdir -p build/WEB-INF/classes build/WEB-INF/lib

echo "=== Baixando dependencias ==="
if [ ! -f "$HSQLDB_JAR" ]; then
    ./download-deps.sh
fi

echo "=== Compilando classes ==="
javac -d build/WEB-INF/classes \
    -cp "$TOMCAT_LIB/servlet-api.jar:$HSQLDB_JAR" \
    src/chamados/*.java

echo "=== Copiando dependencias ==="
cp "$HSQLDB_JAR" build/WEB-INF/lib/

echo "=== Copiando web.xml e views ==="
cp -r web/WEB-INF/web.xml build/WEB-INF/
cp -r web/WEB-INF/views build/WEB-INF/

echo "=== Gerando WAR ==="
cd build
jar -cf "../$MODULO.war" WEB-INF/
cd ..

echo "=== WAR gerado: $MODULO.war ==="
echo "Copie para webapps/ do Tomcat e acesse /chamados"

#!/bin/sh
# build.sh - Compila o modulo 06 (Java ME)
# Uso: ./build.sh
# Requer WTK 2.5 ou MicroEmu para execucao

set -e

MODULO="06-javame-field-service-mobile"
WTK_HOME="${WTK_HOME:-/opt/WTK2.5.2}"

echo "=== Limpando builds anteriores ==="
rm -rf out
mkdir -p out/classes

echo "=== Compilando com bootclasspath do CLDC/MIDP ==="
javac -d out/classes \
    -bootclasspath "$WTK_HOME/lib/cldcapi11.jar:$WTK_HOME/lib/midpapi20.jar" \
    -sourcepath src \
    src/vistoria/*.java

echo "=== (Opcional) Rodando preverify ==="
if command -v preverify >/dev/null 2>&1; then
    mkdir -p out/preverified
    preverify -classpath "$WTK_HOME/lib/cldcapi11.jar:$WTK_HOME/lib/midpapi20.jar" \
        -d out/preverified out/classes
    CLASSE_DIR="out/preverified"
else
    echo "  preverify nao encontrado — pulando (apenas compilacao)"
    CLASSE_DIR="out/classes"
fi

echo "=== Gerando JAR ==="
mkdir -p out/jar
cp -r "$CLASSE_DIR/vistoria" out/jar/
cd out/jar
jar -cf "../../$MODULO.jar" vistoria/
cd ../..

echo "=== Atualizando JAD com tamanho do JAR ==="
JAR_SIZE=$(stat -c%s "$MODULO.jar" 2>/dev/null || stat -f%z "$MODULO.jar" 2>/dev/null)
sed -i "s/MIDlet-Jar-Size: .*/MIDlet-Jar-Size: $JAR_SIZE/" vistoria.jad

echo "=== Build concluido ==="
echo "JAR: $MODULO.jar"
echo "JAD: vistoria.jad"

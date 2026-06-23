#!/bin/sh
# Module 08 - Java 5 Generics Annotations Concurrency

echo "=== Module 08: Java 5 Generics Annotations Concurrency ==="
echo "Compilando..."
rm -rf out
mkdir -p out
javac -d out -sourcepath src src/processador/*.java

echo -e "\nExecutando..."
java -cp out processador.Main

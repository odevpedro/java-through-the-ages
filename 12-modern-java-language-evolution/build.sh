#!/bin/sh
rm -rf out
mkdir -p out
javac -d out -sourcepath src/main/java src/main/java/regras/*.java
java -cp out regras.Main

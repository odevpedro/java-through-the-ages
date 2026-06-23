@echo off
REM Module 08 - Java 5 Generics Annotations Concurrency

echo === Module 08: Java 5 Generics Annotations Concurrency ===
echo Compilando...
if exist out rmdir /s /q out
mkdir out
javac -d out -sourcepath src src/processador/*.java

echo.
echo Executando...
java -cp out processador.Main

@echo off
REM build.bat - Compila e executa a simulacao do modulo 05

echo === Compilando classes ===
if exist out rmdir /s /q out
mkdir out
javac -d out -sourcepath src src\banco\*.java

echo.
echo === Executando SimuladorTransferencia ===
echo.
java -cp out banco.SimuladorTransferencia

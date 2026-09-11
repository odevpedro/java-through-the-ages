@echo off
REM start-cliente.bat - executa o cliente RMI

if not exist estoque-cliente.jar (
    echo ERRO: estoque-cliente.jar nao encontrado.
    echo Execute primeiro: build.bat
    exit /b 1
)

echo Executando cliente RMI...
java -jar estoque-cliente.jar

@echo off
REM start-cliente.bat - executa o cliente RMI

if not exist mensagens-cliente.jar (
    echo ERRO: mensagens-cliente.jar nao encontrado.
    echo Execute primeiro: build.bat
    exit /b 1
)

echo Executando cliente RMI...
java -jar mensagens-cliente.jar

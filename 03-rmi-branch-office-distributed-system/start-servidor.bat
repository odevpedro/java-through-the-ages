@echo off
REM start-servidor.bat - inicia o servidor RMI

if not exist estoque-servidor.jar (
    echo ERRO: estoque-servidor.jar nao encontrado.
    echo Execute primeiro: build.bat
    exit /b 1
)

echo Iniciando servidor RMI...
java -Djava.rmi.server.hostname=localhost -jar estoque-servidor.jar

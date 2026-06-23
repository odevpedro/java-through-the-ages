@echo off
REM start-servidor.bat - inicia o servidor RMI

if not exist mensagens-servidor.jar (
    echo ERRO: mensagens-servidor.jar nao encontrado.
    echo Execute primeiro: build.bat
    exit /b 1
)

echo Iniciando servidor RMI...
java -Djava.rmi.server.hostname=localhost -jar mensagens-servidor.jar

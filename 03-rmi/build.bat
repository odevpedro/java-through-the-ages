@echo off
REM build.bat - Modulo 03: RMI

echo [1/4] Limpando diretorios de saida...
if exist out rmdir /s /q out
if exist mensagens-servidor.jar del mensagens-servidor.jar
if exist mensagens-cliente.jar del mensagens-cliente.jar
mkdir out

echo [2/4] Compilando fontes...
javac -d out -sourcepath src ^
    src\mensagens\Mensagem.java ^
    src\mensagens\ServicoMensagens.java ^
    src\mensagens\ServicoMensagensImpl.java ^
    src\mensagens\Servidor.java ^
    src\mensagens\Cliente.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/4] Gerando stubs com rmic, se disponivel...
where rmic >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    rmic -d out -classpath out mensagens.ServicoMensagensImpl
) else (
    echo AVISO: rmic nao encontrado. Em JDK 5+ stubs dinamicos permitem continuar.
)

echo [4/4] Empacotando JARs...
echo Main-Class: mensagens.Servidor > MANIFEST-srv.MF
jar cvfm mensagens-servidor.jar MANIFEST-srv.MF -C out .
del MANIFEST-srv.MF

echo Main-Class: mensagens.Cliente > MANIFEST-cli.MF
jar cvfm mensagens-cliente.jar MANIFEST-cli.MF -C out .
del MANIFEST-cli.MF

echo.
echo Build concluido.
echo Execute start-servidor.bat e depois start-cliente.bat em outro terminal.

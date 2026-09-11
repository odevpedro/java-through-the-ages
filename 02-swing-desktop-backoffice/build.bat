@echo off
REM build.bat - Modulo 02: Swing Desktop Backoffice

echo [1/4] Limpando diretorio de saida...
if exist out rmdir /s /q out
mkdir out

echo [2/4] Compilando fontes...
javac -d out -sourcepath src ^
    src\cadastro\Cliente.java ^
    src\cadastro\RepositorioCliente.java ^
    src\cadastro\CadastroClientesFrame.java ^
    src\cadastro\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/4] Empacotando em JAR executavel...
echo Main-Class: cadastro.Main > MANIFEST.MF
jar cvfm cadastro-swing.jar MANIFEST.MF -C out .
del MANIFEST.MF

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Empacotamento falhou.
    exit /b 1
)

echo [4/4] Iniciando aplicacao Swing...
java -jar cadastro-swing.jar

echo.
echo Build concluido. Para reabrir: java -jar cadastro-swing.jar
echo Dados salvos em: clientes.dat

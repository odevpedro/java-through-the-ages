@echo off
REM build.bat - Modulo 01: Applet/AWT — Catalogo de Produtos

echo [1/4] Limpando diretorio de saida...
if exist out rmdir /s /q out
mkdir out

echo [2/4] Compilando fontes...
javac -d out -sourcepath src src\produtos\Produto.java src\produtos\CatalogoApplet.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/4] Empacotando em JAR...
jar cvf catalogo.jar -C out .

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Empacotamento falhou.
    exit /b 1
)

echo [4/4] Abrindo appletviewer...
appletviewer catalogo.html

echo.
echo Build concluido. JAR: catalogo.jar

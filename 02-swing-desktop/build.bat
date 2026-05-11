@echo off
REM =============================================================================
REM  build.bat - Modulo 02: Swing Desktop
REM
REM  Requisitos:
REM    - JDK 8 ou superior (Swing nao foi removido dos JDKs modernos)
REM    - javac e java no PATH
REM
REM  Nota: ao contrario do modulo 01 (Applet), este modulo nao precisa de
REM  appletviewer nem de nenhum servidor. E uma aplicacao desktop pura,
REM  executada diretamente via 'java'. Essa e uma das vantagens do Swing
REM  sobre Applets: sem dependencia de browser ou plugin.
REM =============================================================================

echo [1/4] Limpando diretorio de saida...
if exist out rmdir /s /q out
mkdir out

echo [2/4] Compilando fontes...
javac -d out -sourcepath src ^
    src\mensagens\Mensagem.java ^
    src\mensagens\RepositorioMensagens.java ^
    src\mensagens\MensagemListRenderer.java ^
    src\mensagens\JanelaPrincipal.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/4] Empacotando em JAR executavel...
REM
REM  O MANIFEST.MF define a classe principal (Main-Class).
REM  Isso torna o JAR executavel via 'java -jar'.
REM  Em JDK 1.2 o atributo Main-Class no manifest foi padronizado.
REM
echo Main-Class: mensagens.JanelaPrincipal > MANIFEST.MF
jar cvfm mensagens-swing.jar MANIFEST.MF -C out .
del MANIFEST.MF

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Empacotamento falhou.
    exit /b 1
)

echo [4/4] Iniciando aplicacao Swing...
java -jar mensagens-swing.jar

echo.
echo Build concluido. Para reabrir: java -jar mensagens-swing.jar
echo Dados salvos em: mensagens.dat (arquivo binario de serializacao)

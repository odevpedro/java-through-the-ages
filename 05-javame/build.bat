@echo off
REM build.bat - Modulo 05: Java ME
REM Requer JDK 8 ou inferior e WTK 2.5, ou cldc-api.jar/midp-api.jar em lib\.

set WTK_HOME=%WTK_HOME%
if "%WTK_HOME%"=="" set WTK_HOME=C:\WTK2.5

set CLDC_JAR=%CLDC_JAR%
if "%CLDC_JAR%"=="" set CLDC_JAR=%WTK_HOME%\lib\cldcapi11.jar

set MIDP_JAR=%MIDP_JAR%
if "%MIDP_JAR%"=="" set MIDP_JAR=%WTK_HOME%\lib\midpapi20.jar

set PREVERIFY=%PREVERIFY%
if "%PREVERIFY%"=="" set PREVERIFY=%WTK_HOME%\bin\preverify.exe

if not exist "%CLDC_JAR%" if exist lib\cldc-api.jar set CLDC_JAR=lib\cldc-api.jar
if not exist "%MIDP_JAR%" if exist lib\midp-api.jar set MIDP_JAR=lib\midp-api.jar

if not exist "%CLDC_JAR%" (
    echo ERRO: cldc-api.jar nao encontrado.
    exit /b 1
)

if not exist "%MIDP_JAR%" (
    echo ERRO: midp-api.jar nao encontrado.
    exit /b 1
)

echo [1/5] Limpando diretorios de saida...
if exist out rmdir /s /q out
if exist out-preverified rmdir /s /q out-preverified
if exist mensagens.jar del mensagens.jar
mkdir out
mkdir out-preverified

echo [2/5] Compilando fontes Java...
javac -source 1.3 -target 1.1 -bootclasspath "%CLDC_JAR%;%MIDP_JAR%" -d out -sourcepath src ^
    src\mensagens\Mensagem.java ^
    src\mensagens\RepositorioRMS.java ^
    src\mensagens\TelaLista.java ^
    src\mensagens\TelaAdicionar.java ^
    src\mensagens\MensagemMidlet.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/5] Preverificando bytecode...
if exist "%PREVERIFY%" (
    "%PREVERIFY%" -classpath "%CLDC_JAR%;%MIDP_JAR%;out" -d out-preverified out
    set CLASS_DIR=out-preverified
) else (
    echo AVISO: preverify nao encontrado. Usando classes nao preverificadas.
    set CLASS_DIR=out
)

echo [4/5] Empacotando JAR...
echo MIDlet-Name: Mensagens> MANIFEST.MF
echo MIDlet-Version: 1.0.0>> MANIFEST.MF
echo MIDlet-Vendor: Java Through the Ages>> MANIFEST.MF
echo MIDlet-1: Mensagens,, mensagens.MensagemMidlet>> MANIFEST.MF
echo MicroEdition-Profile: MIDP-2.0>> MANIFEST.MF
echo MicroEdition-Configuration: CLDC-1.1>> MANIFEST.MF
jar cvfm mensagens.jar MANIFEST.MF -C %CLASS_DIR% .
del MANIFEST.MF

echo [5/5] Atualize MIDlet-Jar-Size em mensagens.jad com o tamanho de mensagens.jar.
echo Build concluido.

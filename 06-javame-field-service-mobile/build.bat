@echo off
REM build.bat - Compila o modulo 06 (Java ME)
REM Requer WTK 2.5 para execucao

set MODULO=06-javame-field-service-mobile
set WTK_HOME=%WTK_HOME%\C:\WTK2.5.2

echo === Limpando builds anteriores ===
if exist out rmdir /s /q out
mkdir out\classes

echo === Compilando com bootclasspath do CLDC/MIDP ===
javac -d out\classes -bootclasspath "%WTK_HOME%\lib\cldcapi11.jar;%WTK_HOME%\lib\midpapi20.jar" -sourcepath src src\vistoria\*.java

echo === Gerando JAR ===
mkdir out\jar
xcopy out\classes\vistoria out\jar\vistoria\ /e /i
cd out\jar
jar -cf ..\..\%MODULO%.jar vistoria\
cd ..\..

echo === Atualizando JAD ===
echo Lembre-se de atualizar MIDlet-Jar-Size manualmente no vistoria.jad
echo === Build concluido ===
echo JAR: %MODULO%.jar

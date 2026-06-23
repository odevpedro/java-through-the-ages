@echo off
REM build.bat - Compila o modulo 04 e gera o WAR

set MODULO=04-servlet-jsp-jdbc-intranet
set TOMCAT_LIB=%USERPROFILE%\tomcat\lib
set HSQLDB_JAR=lib\hsqldb.jar

echo === Limpando builds anteriores ===
if exist build rmdir /s /q build
mkdir build\WEB-INF\classes
mkdir build\WEB-INF\lib

echo === Compilando classes ===
javac -d build\WEB-INF\classes -cp "%TOMCAT_LIB%\servlet-api.jar;%HSQLDB_JAR%" src\mensagens\*.java

echo === Copiando dependencias ===
copy "%HSQLDB_JAR%" build\WEB-INF\lib\

echo === Copiando web.xml e views ===
copy web\WEB-INF\web.xml build\WEB-INF\
xcopy web\WEB-INF\views build\WEB-INF\views\ /e /i

echo === Gerando WAR ===
cd build
jar -cf ..\%MODULO%.war WEB-INF\
cd ..

echo === WAR gerado: %MODULO%.war ===
echo Copie para webapps/ do Tomcat e acesse /chamados

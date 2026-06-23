@echo off
REM Module 09 - Spring MVC REST JSON

echo === Module 09: Spring MVC REST JSON ===

where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven nao encontrado. Instale o Maven primeiro.
    exit /b 1
)

echo Compilando e gerando WAR...
mvn clean package

echo.
echo === BUILD CONCLUIDO ===
echo WAR gerado em: target/catalogo.war
echo.
echo Para implantar no Tomcat:
echo   1. Copie target/catalogo.war para webapps/ do Tomcat
echo   2. Inicie o Tomcat
echo   3. Acesse: http://localhost:8080/catalogo/api/produtos

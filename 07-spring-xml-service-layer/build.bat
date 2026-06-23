@echo off
REM Module 07 - Spring XML Service Layer
REM Downloads Spring 4.3.x JARs e compila/executa via Maven

echo === Module 07: Spring XML Service Layer ===

where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven nao encontrado. Instale o Maven ou baixe as dependencias manualmente.
    exit /b 1
)

echo Baixando dependencias e compilando...
mvn compile -q

echo Executando aplicacao...
mvn exec:java -q

@echo off
REM build.bat - Modulo 03: RMI Branch Office

echo [1/4] Limpando diretorios de saida...
if exist out rmdir /s /q out
if exist estoque-servidor.jar del estoque-servidor.jar
if exist estoque-cliente.jar del estoque-cliente.jar
mkdir out

echo [2/4] Compilando fontes...
javac -d out -sourcepath src ^
    src\estoque\Produto.java ^
    src\estoque\Estoque.java ^
    src\estoque\EstoqueImpl.java ^
    src\estoque\ServidorEstoque.java ^
    src\estoque\ClienteFilial.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/4] Gerando stubs com rmic, se disponivel...
where rmic >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    rmic -d out -classpath out estoque.EstoqueImpl
) else (
    echo AVISO: rmic nao encontrado. Em JDK 5+ stubs dinamicos permitem continuar.
)

echo [4/4] Empacotando JARs...
echo Main-Class: estoque.ServidorEstoque > MANIFEST-srv.MF
jar cvfm estoque-servidor.jar MANIFEST-srv.MF -C out .
del MANIFEST-srv.MF

echo Main-Class: estoque.ClienteFilial > MANIFEST-cli.MF
jar cvfm estoque-cliente.jar MANIFEST-cli.MF -C out .
del MANIFEST-cli.MF

echo.
echo Build concluido.
echo Execute start-servidor.bat e depois start-cliente.bat em outro terminal.

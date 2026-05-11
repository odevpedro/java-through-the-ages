@echo off
REM build.bat - Modulo 04: Servlet/JSP/JDBC

if not exist lib\hsqldb.jar (
    echo ERRO: lib\hsqldb.jar nao encontrado.
    echo Execute download-deps.sh no Git Bash/WSL ou coloque o JAR manualmente.
    exit /b 1
)

if not exist lib\servlet-api.jar (
    echo ERRO: lib\servlet-api.jar nao encontrado.
    echo Copie de %%TOMCAT_HOME%%\lib\servlet-api.jar ou coloque o JAR manualmente.
    exit /b 1
)

echo [1/5] Limpando diretorios de saida...
if exist out rmdir /s /q out
if exist war rmdir /s /q war
if exist mensagens.war del mensagens.war
mkdir out

echo [2/5] Compilando fontes Java...
javac -d out -sourcepath src -cp "lib\servlet-api.jar;lib\hsqldb.jar" ^
    src\mensagens\Mensagem.java ^
    src\mensagens\ConexaoFactory.java ^
    src\mensagens\MensagemDao.java ^
    src\mensagens\InicializadorListener.java ^
    src\mensagens\MensagemServlet.java ^
    src\mensagens\RedirectServlet.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/5] Montando estrutura do WAR...
mkdir war\WEB-INF\classes
mkdir war\WEB-INF\lib
xcopy /E /I /Y out\* war\WEB-INF\classes\
copy web\WEB-INF\web.xml war\WEB-INF\web.xml
xcopy /E /I /Y web\WEB-INF\views war\WEB-INF\views\
copy lib\hsqldb.jar war\WEB-INF\lib\hsqldb.jar

echo [4/5] Empacotando WAR...
jar cvf mensagens.war -C war .
rmdir /s /q war

echo [5/5] Build concluido: mensagens.war

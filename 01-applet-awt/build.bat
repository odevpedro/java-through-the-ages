@echo off
REM =============================================================================
REM  build.bat - Modulo 01: Applet/AWT
REM  Compila os fontes Java, empacota em JAR e abre o appletviewer.
REM
REM  Requisitos:
REM    - JDK 8 (ultimo JDK com appletviewer incluido)
REM    - JAVA_HOME configurado no PATH, ou javac/jar/appletviewer acessiveis
REM
REM  Nota historica:
REM    Em 1996 nao havia Maven, Ant ou Gradle. O desenvolvedor executava
REM    javac e jar diretamente no terminal. Este script reproduz esse fluxo.
REM =============================================================================

echo [1/4] Limpando diretorio de saida...
if exist out rmdir /s /q out
mkdir out

echo [2/4] Compilando fontes...
REM
REM  -d out         -> diretorio de destino dos .class
REM  -sourcepath    -> raiz dos fontes para resolucao de dependencias entre pacotes
REM  -source 1.1    -> garante que nenhuma sintaxe posterior ao JDK 1.1 seja usada
REM  -target 1.1    -> gera bytecode compativel com JVM 1.1
REM
REM  Nota: javac mais recentes podem rejeitar -source 1.1 / -target 1.1.
REM  Nesse caso, remova as flags -source e -target; o comportamento funcional
REM  e o mesmo, apenas o bytecode gerado sera de versao mais recente.
REM
javac -d out -sourcepath src src\mensagens\Mensagem.java src\mensagens\MensagemApplet.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Compilacao falhou.
    exit /b 1
)

echo [3/4] Empacotando em JAR...
REM
REM  jar cvf -> c=criar, v=verbose, f=nome do arquivo
REM  -C out . -> inclui tudo a partir do diretorio 'out'
REM
REM  Em JDK 1.1, o JAR foi introduzido como formato de distribuicao.
REM  Antes disso, os .class eram baixados individualmente pelo browser
REM  (uma requisicao HTTP por arquivo — muito lento em modems 28.8k).
REM
jar cvf mensagens.jar -C out .

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Empacotamento falhou.
    exit /b 1
)

echo [4/4] Abrindo appletviewer...
REM
REM  appletviewer simula o ambiente do browser sem precisar de um browser real.
REM  Ele le a tag <applet> do HTML e instancia o applet diretamente.
REM  Foi incluido no JDK desde a versao 1.0 e removido no JDK 11.
REM  Se voce usa JDK 11+, use JDK 8 ou o Docker legado descrito no README.
REM
appletviewer mensagens.html

echo.
echo Build concluido. JAR: mensagens.jar

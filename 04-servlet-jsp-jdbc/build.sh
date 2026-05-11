#!/bin/sh
# =============================================================================
#  build.sh - Modulo 04: Servlet/JSP/JDBC
#
#  Compila os fontes Java e empacota um arquivo WAR pronto para deploy
#  em qualquer container Servlet (Tomcat 4.x, 5.x, 6.x, 9.x, 10.x).
#
#  PRE-REQUISITOS:
#    1. JDK 8 ou superior no PATH (javac, jar, java)
#    2. Os JARs das dependencias no diretorio lib/:
#         lib/hsqldb.jar       (testado com hsqldb-2.5.2.jar ou 1.8.x)
#         lib/servlet-api.jar  (Servlet 2.3+; incluido no Tomcat)
#
#  OBTENDO OS JARS:
#    HSQLDB: https://hsqldb.org/
#    Servlet API: incluida no Tomcat. Copie de:
#      $TOMCAT_HOME/lib/servlet-api.jar
#    Alternativa Maven (sem Maven no projeto, apenas para download):
#      mvn dependency:get -Dartifact=org.hsqldb:hsqldb:2.5.2
#      mvn dependency:get -Dartifact=javax.servlet:javax.servlet-api:3.1.0
#
#  EXECUTANDO:
#    Apos o build, deploy o WAR no Tomcat:
#      cp mensagens.war $TOMCAT_HOME/webapps/
#      $TOMCAT_HOME/bin/startup.sh
#    Acesse: http://localhost:8080/mensagens/
# =============================================================================

set -e

# --- verificar JARs necessarios ---
if [ ! -f lib/hsqldb.jar ]; then
    echo "ERRO: lib/hsqldb.jar nao encontrado."
    echo "Baixe em https://hsqldb.org e coloque em lib/hsqldb.jar"
    echo "Ou execute: sh download-deps.sh"
    exit 1
fi
if [ ! -f lib/servlet-api.jar ]; then
    echo "ERRO: lib/servlet-api.jar nao encontrado."
    echo "Copie de: \$TOMCAT_HOME/lib/servlet-api.jar"
    echo "Ou execute: sh download-deps.sh"
    exit 1
fi

echo "[1/5] Limpando diretorios de saida..."
rm -rf out mensagens.war
mkdir -p out

echo "[2/5] Compilando fontes Java..."
#
# -cp inclui servlet-api.jar (necessario para compilar HttpServlet etc.)
# e hsqldb.jar (necessario para compilar ConexaoFactory).
# O WAR resultante incluira o hsqldb.jar mas NAO o servlet-api.jar:
# o container (Tomcat) ja provem a Servlet API em seu classloader.
#
javac \
    -d out \
    -sourcepath src \
    -cp "lib/servlet-api.jar:lib/hsqldb.jar" \
    src/mensagens/Mensagem.java \
    src/mensagens/ConexaoFactory.java \
    src/mensagens/MensagemDao.java \
    src/mensagens/InicializadorListener.java \
    src/mensagens/MensagemServlet.java \
    src/mensagens/RedirectServlet.java

echo "[3/5] Montando estrutura do WAR..."
#
# Um WAR (Web Application Archive) segue uma estrutura especifica:
#   /           -> arquivos acessiveis diretamente pelo browser (HTML, CSS, JS)
#   /WEB-INF/   -> arquivos nao acessiveis pelo browser
#   /WEB-INF/web.xml    -> deployment descriptor (obrigatorio)
#   /WEB-INF/classes/   -> bytecode (.class)
#   /WEB-INF/lib/       -> JARs de dependencias
#
# Esta estrutura foi definida pela Servlet API 2.2 (1998) e permanece
# identica ate a Servlet API 6.0 (Jakarta EE 10, 2022).
#
mkdir -p war/WEB-INF/classes
mkdir -p war/WEB-INF/lib

# bytecode
cp -r out/* war/WEB-INF/classes/

# deployment descriptor e views
cp -r web/WEB-INF/web.xml       war/WEB-INF/web.xml
cp -r web/WEB-INF/views         war/WEB-INF/views

# dependencias: hsqldb vai dentro do WAR; servlet-api NAO vai
cp lib/hsqldb.jar war/WEB-INF/lib/

echo "[4/5] Empacotando WAR..."
#
# jar com extensao .war e reconhecido automaticamente pelo Tomcat.
# O Tomcat usa o nome do arquivo como context path:
#   mensagens.war -> http://localhost:8080/mensagens/
#
jar cvf mensagens.war -C war .
rm -rf war

echo "[5/5] Verificando estrutura do WAR..."
jar tf mensagens.war | grep -E "(web\.xml|\.class|\.jsp|hsqldb)" | head -20

echo ""
echo "============================================================"
echo " Build concluido: mensagens.war"
echo ""
echo " Deploy no Tomcat:"
echo "   cp mensagens.war \$TOMCAT_HOME/webapps/"
echo "   \$TOMCAT_HOME/bin/startup.sh"
echo "   # Acesse: http://localhost:8080/mensagens/"
echo ""
echo " Para Tomcat embutido via Maven (alternativa rapida):"
echo "   mvn tomcat7:run  (se pom.xml estiver configurado)"
echo "============================================================"

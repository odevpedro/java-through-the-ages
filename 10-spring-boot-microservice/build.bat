@echo off
call mvn clean package -DskipTests
echo Para executar: java -jar target\contas-1.0.0.jar

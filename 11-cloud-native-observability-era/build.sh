#!/bin/sh
mvn clean package -DskipTests
echo "Para executar: java -jar target/pedidos-1.0.0.jar"

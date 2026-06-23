@echo off
if exist out rmdir /s /q out
mkdir out
javac -d out -sourcepath src\main\java src\main\java\regras\*.java
java -cp out regras.Main

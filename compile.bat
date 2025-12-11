@echo off
echo Compiling Java files...
javac -cp ".;mysql-connector-j-9.5.0.jar" -d bin src\model\*.java src\util\*.java src\dao\*.java src\service\*.java src\ui\*.java
echo Compilation complete!
pause


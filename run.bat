@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo ====================================================
echo Compilation de l'application de Gestion des Salles...
echo ====================================================
if not exist target\classes mkdir target\classes
javac -encoding UTF-8 -d target/classes src\main\java\model\*.java src\main\java\service\*.java src\main\java\ui\*.java src\main\java\main\*.java
if %ERRORLEVEL% neq 0 (
    echo Erreur lors de la compilation.
    pause
    exit /b %ERRORLEVEL%
)
echo Lancement de l'interface graphique Swing...
start javaw -cp target/classes main.Main
exit /b 0

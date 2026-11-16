@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo ====================================================
echo Exécution des 20 tests de validation métier...
echo ====================================================
java -cp target/classes main.TestValidationMetier
pause

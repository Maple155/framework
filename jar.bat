@echo off
:: ======================================
:: Script de compilation et création de JAR (librairie pour Tomcat)
:: ======================================

:: ===== CONFIGURATION =====
set BUILD_DIR=build
set SRC_DIR=src
set SERVLET_API_JAR=C:\Program Files\Apache Software Foundation\Tomcat 10.1\lib\servlet-api.jar
set JAR_NAME=framework.jar
set DEPLOY_DIR=D:\IT.University\S5\Mr_Naina\framework_test\src\main\webapp\WEB-INF\lib
set LIB_DIR=D:\IT.University\S5\Mr_Naina\framework_test\lib

echo ======================================
echo Déploiement de projet Java (.jar)
echo ======================================

:: ===== Étape 0 - Nettoyer le dossier build s'il existe =====
if exist "%BUILD_DIR%" (
    echo Le dossier %BUILD_DIR% existe déjà. Suppression...
    rmdir /s /q "%BUILD_DIR%"
)

:: ===== Étape 1 - Créer le dossier build =====
echo Création du dossier %BUILD_DIR%...
mkdir "%BUILD_DIR%"

:: ===== Étape 2 - Compilation des fichiers Java =====
echo Compilation des fichiers Java...

:: Générer la liste des fichiers avec chemins courts pour éviter les espaces
(for /r "%SRC_DIR%" %%f in (*.java) do @echo %%~sf) > sources.txt

:: Compilation
javac -parameters -cp "%SERVLET_API_JAR%" -d "%BUILD_DIR%" @sources.txt
if errorlevel 1 (
    echo Erreur lors de la compilation. Vérifiez vos fichiers Java.
    del sources.txt
    pause
    exit /b 1
)
del sources.txt

:: ===== Étape 3 - Création du fichier JAR (librairie, pas exécutable) =====
echo Création du fichier "%JAR_NAME%"...
cd "%BUILD_DIR%"
jar -cvf "%JAR_NAME%" *
cd ..

:: ===== Étape 4 - Copier le JAR vers le dossier de déploiement =====
echo Déploiement du fichier "%JAR_NAME%" vers "%DEPLOY_DIR%"...
if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
copy "%BUILD_DIR%\%JAR_NAME%" "%DEPLOY_DIR%"

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"
copy "%BUILD_DIR%\%JAR_NAME%" "%LIB_DIR%"

:: ===== Étape 5 - Terminé =====
echo ======================================
echo Déploiement terminé avec succès !
echo Le fichier .jar a été copié dans "%DEPLOY_DIR%".
echo Et aussi dans "%LIB_DIR%".
echo ======================================
pause

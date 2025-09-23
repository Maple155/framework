@echo off
@REM :: Configuration des variables
set BUILD_DIR=build
set WEBAPP_DIR=src/main/webapp
@REM set SERVLET_API_JAR="C:\Program Files\Apache Software Foundation\apache-tomcat-10.1.28\lib\servlet-api.jar" 
@REM set TOMCAT_WEBAPPS_DIR="C:\Program Files\Apache Software Foundation\apache-tomcat-10.1.28\webapps" 

set SERVLET_API_JAR="C:\Program Files\Apache Software Foundation\Tomcat 10.1\lib\servlet-api.jar" 
set TOMCAT_WEBAPPS_DIR="C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps" 

set WAR_NAME=prevision.war

@REM :: etape 0 - Verifier si le dossier build existe deja
if exist %BUILD_DIR% (
    echo Le dossier %BUILD_DIR% existe deja. Suppression...
    rmdir /s /q %BUILD_DIR%
)

@REM :: etape 1 - Creer le dossier build
echo Creation du dossier %BUILD_DIR%...
mkdir %BUILD_DIR%
mkdir %BUILD_DIR%\WEB-INF
mkdir %BUILD_DIR%\WEB-INF\classes

@REM :: etape 2 - Compilation des fichiers Java
echo Compilation des fichiers Java...   
@REM :: Ajouter tous les fichiers .java dans une liste
dir /b /s *.java > sources.txt

@REM :: Compilation
javac -cp %SERVLET_API_JAR% -d %BUILD_DIR%\WEB-INF\classes @sources.txt
if errorlevel 1 (
    echo Erreur lors de la compilation. Verifiez vos fichiers Java.
    del sources.txt
)

@REM :: Suppression de sources.txt
del sources.txt

@REM :: etape 3 - Copier les fichiers webapp dans le dossier de build
echo Copie des fichiers du dossier %WEBAPP_DIR% vers %BUILD_DIR%...
xcopy  /e /i  "%WEBAPP_DIR%" "%BUILD_DIR%" 

@REM :: etape 4 - Creation du fichier WAR
echo Creation du fichier .war...
cd %BUILD_DIR%
jar -cvf %WAR_NAME% *
cd ..

@REM :: etape 5 - Copier le fichier .war dans le dossier webapps de Tomcat
echo Deploiement du fichier .war vers Tomcat...
copy  %BUILD_DIR%\%WAR_NAME% %TOMCAT_WEBAPPS_DIR%

@REM :: etape 6 - Terminer
echo Deploiement termine avec succes !
echo Le fichier .war a ete copie dans %TOMCAT_WEBAPPS_DIR%.
echo Vous pouvez maintenant redemarrer Tomcat pour tester votre application.
pause

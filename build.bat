@echo off
setlocal enabledelayedexpansion

:: Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    exit /b 1
)

echo [INFO] Checking Java installation...
java -version

:: Get Java version and extract major version
for /f "tokens=2" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_FULL=%%i
)

:: Try to extract major version - handle different formats
for /f "tokens=2 delims=.^" %%a in ("%JAVA_VERSION_FULL%") do set JAVA_MAJOR=%%a
if "%JAVA_MAJOR%"=="" (
    for /f "tokens=1 delims=." %%a in ("%JAVA_VERSION_FULL%") do set JAVA_MAJOR=%%a
)

:: Remove quotes if present
set JAVA_MAJOR=!JAVA_MAJOR:"=!

echo [INFO] Detected Java major version: !JAVA_MAJOR!

:: Check if Java version >= 21
set JAVA_MAJOR_INT=!JAVA_MAJOR!
set /a JAVA_MAJOR_INT=!JAVA_MAJOR_INT!

if !JAVA_MAJOR_INT! lss 21 (
    echo [ERROR] Java 21 or higher is required. Current version: !JAVA_MAJOR_INT!
    echo [INFO] Please install Java 21+ from:
    echo https://adoptium.net/temurin/releases/
    exit /b 1
)

:: Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo [INFO] Please install Maven or add it to your PATH
    exit /b 1
)

echo [INFO] Cleaning and building all modules...
call mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo [ERROR] Build failed with exit code %errorlevel%
    exit /b %errorlevel%
)

echo [INFO] Build finished successfully.


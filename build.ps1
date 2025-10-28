#!/usr/bin/env pwsh

Write-Host "[INFO] Starting build process..." -ForegroundColor Green

# Prefer GraalVM Java 21 if available
$preferredJava = "C:\Program Files\Java\graalvm-jdk-21.0.6+8.1"
if (Test-Path $preferredJava) {
    $env:JAVA_HOME = $preferredJava
    $env:Path = "$preferredJava\bin;$env:Path"
    Write-Host "[INFO] Using preferred JAVA_HOME: $preferredJava" -ForegroundColor Cyan
}

# Prefer local Maven if available
$preferredMaven = "C:\apache-maven-3.9.11"
if (Test-Path $preferredMaven) {
    $env:Path = "$preferredMaven\bin;$env:Path"
    Write-Host "[INFO] Using preferred Maven: $preferredMaven" -ForegroundColor Cyan
}

# Check if Java is installed
Write-Host "[INFO] Checking Java installation..." -ForegroundColor Cyan

# Run java -version and capture ALL output (including errors)
$ErrorActionPreference = "Continue"
$javaVersionOutput = java -version 2>&1 | Out-String

Write-Host "[INFO] Java version output:" -ForegroundColor Cyan
Write-Host $javaVersionOutput

# Check if java command exists
if (!$javaVersionOutput -or $javaVersionOutput.Trim() -eq "") {
    Write-Host "[ERROR] Java is not installed or not in PATH" -ForegroundColor Red
    exit 1
}

# Try multiple regex patterns to get Java version
$javaMajorVersion = $null

# Pattern for format like "openjdk version "1.8.0_462" or java version "17.0.1"
if ($javaVersionOutput -match 'version "(\d+)\.(\d+)') {
    $firstDigit = [int]$matches[1]
    $secondDigit = [int]$matches[2]
    
    # Java 9+ uses single version number (e.g., version "17")
    # Java 8 and below use version "1.8" or "1.7"
    if ($firstDigit -eq 1) {
        # It's Java 8 or earlier, extract the second digit
        $javaMajorVersion = $secondDigit
    } else {
        # It's Java 9+, first digit is the version
        $javaMajorVersion = $firstDigit
    }
}

Write-Host "[INFO] Extracted version parts - first: $firstDigit, second: $secondDigit" -ForegroundColor Cyan

if ($javaMajorVersion) {
    Write-Host "[INFO] Detected Java major version: $javaMajorVersion" -ForegroundColor Cyan
    
    if ($javaMajorVersion -lt 21) {
        Write-Host "[ERROR] Java 21 or higher is required. Current version: $javaMajorVersion" -ForegroundColor Red
        Write-Host "[INFO] Please install Java 21+ from:" -ForegroundColor Yellow
        Write-Host "      https://adoptium.net/temurin/releases/" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "[WARN] Could not determine Java version, proceeding anyway..." -ForegroundColor Yellow
}

# Check if Maven is installed
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "[INFO] Maven detected:" -ForegroundColor Cyan
    Write-Host $mvnVersion
} catch {
    Write-Host "[ERROR] Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "[INFO] Please install Maven or add it to your PATH" -ForegroundColor Yellow
    exit 1
}

Write-Host "[INFO] Cleaning and building all modules..." -ForegroundColor Yellow

# Build the project
mvn clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[INFO] Build finished successfully." -ForegroundColor Green


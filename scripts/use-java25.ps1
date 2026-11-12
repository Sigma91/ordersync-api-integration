# Dot-source this file to select the project-local JDK for the current terminal.
$jdkRoot = Join-Path $PSScriptRoot '../.tools/jdk25'
$projectJdk = Get-ChildItem -LiteralPath $jdkRoot -Directory -ErrorAction SilentlyContinue |
    Where-Object { Test-Path (Join-Path $_.FullName 'bin/javac.exe') } |
    Select-Object -First 1

if (-not $projectJdk) {
    throw 'Extract a Windows x64 JDK 25 ZIP into .tools/jdk25 first. See docs/RUN_LOCAL.md.'
}

$jdkRelease = Get-Content -LiteralPath (Join-Path $projectJdk.FullName 'release')
if (-not ($jdkRelease -match '^JAVA_VERSION="25[.\"]')) {
    throw 'The project-local JDK must be Java 25.'
}

$env:JAVA_HOME = $projectJdk.FullName
$env:Path = "$(Join-Path $env:JAVA_HOME 'bin');$env:Path"
Write-Host "JAVA_HOME=$env:JAVA_HOME"

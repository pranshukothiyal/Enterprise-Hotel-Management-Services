$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "EHMS .NET 10 System Tests" -ForegroundColor Cyan
Write-Host "==========================" -ForegroundColor Cyan

$version = dotnet --version

if (-not $version.StartsWith("10.")) {
    throw ".NET 10 SDK is required. Current SDK: $version"
}

Write-Host "Using .NET SDK $version"

dotnet restore

dotnet build --no-restore

$playwright = ".\bin\Debug\net10.0\playwright.ps1"

if (-not (Test-Path $playwright)) {
    throw "Playwright installer was not generated: $playwright"
}

pwsh $playwright install chromium

dotnet test --no-build --settings .runsettings

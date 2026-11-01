$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$src = Join-Path $root "src"
$out = Join-Path $root "out"

if (-not (Test-Path $out)) {
    New-Item -ItemType Directory -Path $out | Out-Null
}

$preferredJdkBin = "C:\Program Files\Eclipse_DEV\Jdks\jdk11\bin"
$javac = "javac"
if (Test-Path (Join-Path $preferredJdkBin "javac.exe")) {
    $javac = Join-Path $preferredJdkBin "javac.exe"
} elseif (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw "Nem talalhato javac. Telepitett JDK szukseges (JAVA_HOME beallitasa, vagy modositsd a build.ps1-et)."
}

$sources = Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -d $out @sources
if ($LASTEXITCODE -ne 0) {
    throw "Forditasi hiba."
}
Write-Output "Build kesz -> $out"

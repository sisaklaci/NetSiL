# Leforditja a forrast es natív Windows .exe-t (app-image) keszit belole jpackage-dzsel.
# Eredmeny: release\NetSiL\NetSiL.exe (onallo, hordozhato, beagyazott Java futtatokornyezettel).

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$src = Join-Path $root "src"
$out = Join-Path $root "out"
$dist = Join-Path $root "dist"
$release = Join-Path $root "release"

$jdk11Bin = "C:\Program Files\Eclipse_DEV\Jdks\jdk11\bin"
$jpackageBin = "C:\Program Files\Eclipse_DEV\Eclipse-4-23-0-JavaXtd\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.2.v20220201-1208\jre\bin"

if (-not (Test-Path (Join-Path $jdk11Bin "javac.exe"))) {
    throw "Nem talalhato a JDK 11 (javac.exe) a vart helyen: $jdk11Bin"
}
if (-not (Test-Path (Join-Path $jpackageBin "jpackage.exe"))) {
    throw "Nem talalhato a jpackage.exe a vart helyen: $jpackageBin"
}

Remove-Item -Recurse -Force $out, $dist, $release -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $out, $dist | Out-Null

Write-Output "Forditas (JDK 11, hogy illeszkedjen a beagyazott runtime-hoz)..."
$sources = Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& "$jdk11Bin\javac.exe" -encoding UTF-8 -d $out @sources
if ($LASTEXITCODE -ne 0) { throw "Forditasi hiba." }

Write-Output "JAR csomagolas..."
& "$jdk11Bin\jar.exe" --create --file (Join-Path $dist "netsil.jar") --main-class netsil.Main -C $out .
if ($LASTEXITCODE -ne 0) { throw "JAR csomagolasi hiba." }

Write-Output "Natív .exe keszitese (jpackage)..."
& "$jpackageBin\jpackage.exe" `
    --type app-image `
    --input $dist `
    --dest $release `
    --name NetSiL `
    --main-jar netsil.jar `
    --main-class netsil.Main `
    --app-version 1.0.0 `
    --runtime-image "C:\Program Files\Eclipse_DEV\Jdks\jdk11"
if ($LASTEXITCODE -ne 0) { throw "jpackage hiba." }

Write-Output ""
Write-Output "Kesz: $release\NetSiL\NetSiL.exe"

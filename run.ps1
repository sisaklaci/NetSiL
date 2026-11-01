$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$out = Join-Path $root "out"

$preferredJdkBin = "C:\Program Files\Eclipse_DEV\Jdks\jdk11\bin"
$java = "java"
if (Test-Path (Join-Path $preferredJdkBin "java.exe")) {
    $java = Join-Path $preferredJdkBin "java.exe"
}

& $java -cp $out netsil.Main

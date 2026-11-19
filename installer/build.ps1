# Builds TideTempo-Setup.exe : compile -> jar -> jlink runtime -> jpackage app-image -> Inno Setup.
# Usage: powershell -ExecutionPolicy Bypass -File installer\build.ps1
$ErrorActionPreference = 'Stop'
$root    = Split-Path -Parent $PSScriptRoot
$version = '1.0.0'
$jdk     = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "$env:USERPROFILE\.jdks\openjdk-17.0.2" }
$iscc    = "$env:LOCALAPPDATA\Programs\Inno Setup 6\ISCC.exe"
$stage   = Join-Path $env:TEMP 'tidetempo_build'
$classes = "$stage\classes"; $input = "$stage\input"; $image = "$stage\image"; $rt = "$stage\runtime"

Remove-Item $stage -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory $classes, $input | Out-Null

# 1. compile (skip the stray scratch classes in the default package)
$src = "$root\src"
$java = Get-ChildItem $src -Recurse -Filter *.java | Where-Object { $_.DirectoryName -ne $src } | ForEach-Object FullName
& "$jdk\bin\javac.exe" -encoding UTF-8 -proc:none -Xlint:-options --release 17 -cp "$root\lib\*" -d $classes $java
if ($LASTEXITCODE) { throw 'javac failed' }
Get-ChildItem $src -Recurse -File | Where-Object { ($_.Extension -notin '.java', '.form') -and (($_.DirectoryName -ne $src) -or ($_.Name -eq 'log4j2.xml')) } |
    ForEach-Object {
        $rel = $_.FullName.Substring($src.Length + 1)
        $dst = Join-Path $classes $rel
        New-Item -ItemType Directory (Split-Path $dst) -Force | Out-Null
        Copy-Item $_.FullName $dst -Force
    }

# 2. jar + dependencies
"Main-Class: com.wigerlabs.tidetempo.gui.AuthScreen" | Set-Content "$stage\manifest.txt" -Encoding ascii
& "$jdk\bin\jar.exe" --create --file "$input\TideTempo.jar" --manifest "$stage\manifest.txt" -C $classes .
Copy-Item "$root\lib\*.jar" $input

# 3. icon from the app logo
Add-Type -AssemblyName System.Drawing
$png = [System.Drawing.Image]::FromFile("$root\src\com\wigerlabs\tidetempo\img\logo.png")
$bmp = New-Object System.Drawing.Bitmap 256, 256
$g = [System.Drawing.Graphics]::FromImage($bmp); $g.InterpolationMode = 'HighQualityBicubic'
$scale = [Math]::Min(256 / $png.Width, 256 / $png.Height)
$w = [int]($png.Width * $scale); $h = [int]($png.Height * $scale)
$g.DrawImage($png, [int]((256 - $w) / 2), [int]((256 - $h) / 2), $w, $h); $g.Dispose(); $png.Dispose()
$ms = New-Object IO.MemoryStream; $bmp.Save($ms, [System.Drawing.Imaging.ImageFormat]::Png); $bytes = $ms.ToArray(); $bmp.Dispose()
$ico = New-Object IO.MemoryStream; $bw = New-Object IO.BinaryWriter $ico
$bw.Write([uint16]0); $bw.Write([uint16]1); $bw.Write([uint16]1)
$bw.Write([byte]0); $bw.Write([byte]0); $bw.Write([byte]0); $bw.Write([byte]0)
$bw.Write([uint16]1); $bw.Write([uint16]32); $bw.Write([uint32]$bytes.Length); $bw.Write([uint32]22); $bw.Write($bytes); $bw.Flush()
[IO.File]::WriteAllBytes("$stage\TideTempo.ico", $ico.ToArray())

# 4. minimal runtime
$modules = 'java.base,java.desktop,java.sql,java.naming,java.logging,java.xml,java.management,java.prefs,java.scripting,java.compiler,java.datatransfer,java.security.jgss,java.security.sasl,java.rmi,java.transaction.xa,jdk.unsupported,jdk.crypto.ec,jdk.xml.dom,jdk.charsets,jdk.localedata'
& "$jdk\bin\jlink.exe" --add-modules $modules --strip-debug --no-header-files --no-man-pages --compress=2 --output $rt
if ($LASTEXITCODE) { throw 'jlink failed' }

# 5. app-image (a plain folder with TideTempo.exe; no WiX needed)
& "$jdk\bin\jpackage.exe" --type app-image --name TideTempo --app-version $version --vendor 'WigerLabs' `
    --input $input --main-jar TideTempo.jar --main-class com.wigerlabs.tidetempo.gui.AuthScreen `
    --runtime-image $rt --icon "$stage\TideTempo.ico" --java-options '-Dfile.encoding=UTF-8' --dest $image
if ($LASTEXITCODE) { throw 'jpackage failed' }

# 6. installer
& $iscc "/DAppVersion=$version" "/DSourceDir=$image\TideTempo" "/DIconFile=$stage\TideTempo.ico" "/O$root\installer\output" "$root\installer\TideTempo.iss"
if ($LASTEXITCODE) { throw 'ISCC failed' }

Remove-Item $stage -Recurse -Force
Write-Host "Done: $root\installer\output\TideTempo-Setup-$version.exe"

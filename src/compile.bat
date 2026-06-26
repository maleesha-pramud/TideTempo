@echo off
setlocal enabledelayedexpansion

del sources.txt 2>nul
for /f "delims=" %%i in ('dir /s /b *.java') do (
    echo "%%i" >> sources.txt
)

javac -d "..\build\classes" -cp "..\lib\*" @sources.txt

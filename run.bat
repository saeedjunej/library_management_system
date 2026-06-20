@echo off
if not exist out mkdir out
dir /s /b src\main\java\*.java > sources.txt
javac -d out @sources.txt
java -cp out com.example.library.DesktopApp

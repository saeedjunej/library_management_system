#!/usr/bin/env sh
set -eu

mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.example.library.DesktopApp

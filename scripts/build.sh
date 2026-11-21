#!/bin/bash

cd "$(dirname "$0")/.." || exit

rm -rf out

javac -d out --module-source-path src $(find src -name "*.java")
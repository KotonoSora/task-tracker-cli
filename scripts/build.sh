#!/bin/bash

cd "$(dirname "$0")/.." || exit 1

rm -rf out dist
mkdir -p dist

javac -d out --module-source-path src $(find src -name "*.java") || exit 1

jar --create \
    --file dist/task-cli.jar \
    --main-class com.app.MainApp \
    -C out/com.app . \
    -C out/com.cli . \
    -C out/com.jsonutils . \
    -C out/com.tasktracker .
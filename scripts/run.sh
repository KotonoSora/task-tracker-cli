#!/bin/bash

cd "$(dirname "$0")/.." || exit 1

if [ ! -f "dist/task-cli.jar" ]; then
    exit 1
fi

java -jar dist/task-cli.jar "$@"
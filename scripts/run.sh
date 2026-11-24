#!/bin/bash

cd "$(dirname "$0")/.." || exit

if [ ! -d "out" ]; then
    exit 1
fi

java --module-path out -m com.app/com.app.MainApp "$@"
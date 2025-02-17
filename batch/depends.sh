#!/bin/sh
DIR=$(dirname "$0")
exec java -jar "$DIR/depends.jar" "$@"

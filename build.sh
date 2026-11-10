#!/usr/bin/env bash
# Compila servidor y cliente en out/
set -e
mkdir -p out/server out/client
javac -d out/server $(find server/src -name "*.java")
javac -d out/client $(find client/src -name "*.java")
echo "Build OK"
echo "  server: java -cp out/server edu.ucam.server.ServerApp"
echo "  client: java -cp out/client edu.ucam.cliente.AppUniversidad"

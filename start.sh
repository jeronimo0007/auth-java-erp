#!/bin/bash

echo "========================================"
echo "   AUTH API - Spring Boot Application"
echo "========================================"
echo

# Verifica se foi passada uma porta como parâmetro
if [ -z "$1" ]; then
    echo "Usando porta padrão: 8080"
    PORT=8080
else
    echo "Usando porta: $1"
    PORT=$1
fi

echo
echo "Iniciando aplicação na porta $PORT..."
echo

# Inicia a aplicação com a porta especificada
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=$PORT

echo
echo "Aplicação finalizada."

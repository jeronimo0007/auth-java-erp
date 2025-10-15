#!/bin/bash

# Script para testar especificamente o algoritmo MD5
# Este script testa se o algoritmo MD5 está sendo executado corretamente

echo "=== Teste do Algoritmo MD5 ==="
echo ""

echo "Algoritmo MD5 corrigido no Java:"
echo "1. Primeira iteração: md5(salt + password)"
echo "2. Iterações seguintes: md5(hash + password)"
echo "3. Número de iterações: 2^8 = 256"
echo ""

# Senhas de teste com diferentes características
PASSWORD1="123456"
PASSWORD2="minhasenha"
PASSWORD3="teste123"
PASSWORD4="a"
PASSWORD5="abcdefghijklmnopqrstuvwxyz"

echo "1. Testando com senha simples: '$PASSWORD1'"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Teste MD5 1",
    "email": "md5test1@email.com",
    "password": "'$PASSWORD1'",
    "firstName": "MD5",
    "lastName": "Teste1",
    "phoneNumber": "(11) 11111-1111"
}'

echo -e "\n\n2. Testando com senha média: '$PASSWORD2'"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Teste MD5 2",
    "email": "md5test2@email.com",
    "password": "'$PASSWORD2'",
    "firstName": "MD5",
    "lastName": "Teste2",
    "phoneNumber": "(11) 22222-2222"
}'

echo -e "\n\n3. Testando com senha longa: '$PASSWORD5'"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Teste MD5 3",
    "email": "md5test3@email.com",
    "password": "'$PASSWORD5'",
    "firstName": "MD5",
    "lastName": "Teste3",
    "phoneNumber": "(11) 33333-3333"
}'

echo -e "\n\n4. Testando login com senha simples: '$PASSWORD1'"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "md5test1@email.com",
    "password": "'$PASSWORD1'"
}'

echo -e "\n\n5. Testando login com senha média: '$PASSWORD2'"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "md5test2@email.com",
    "password": "'$PASSWORD2'"
}'

echo -e "\n\n6. Testando login com senha longa: '$PASSWORD5'"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "md5test3@email.com",
    "password": "'$PASSWORD5'"
}'

echo -e "\n\n=== Análise dos Resultados ==="
echo ""
echo "Verifique se todos os logins funcionaram."
echo "Se funcionarem, o algoritmo MD5 está correto."
echo ""
echo "=== Consultas SQL para Investigar ==="
echo "SELECT email, password FROM tblcontacts WHERE email LIKE '%md5test%' ORDER BY id DESC LIMIT 3;"
echo ""
echo "Os hashes devem ter:"
echo "- Formato: \$P\$[caractere][salt][hash]"
echo "- Tamanho: 34 caracteres"
echo "- Caractere de força: deve ser 9 (8 + 5 = 13, mas limitado a 30, então 9)"
echo ""
echo "=== Comparação com PHP ==="
echo "Para testar a compatibilidade exata:"
echo "1. Crie usuários no PHP com as mesmas senhas"
echo "2. Compare os hashes gerados"
echo "3. Se forem idênticos, a compatibilidade está perfeita"

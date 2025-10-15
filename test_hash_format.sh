#!/bin/bash

# Script para testar o formato do hash gerado
# Este script verifica se o hash está no formato correto para compatibilidade com PHP

echo "=== Teste de Formato de Hash ==="
echo ""

# Senhas de teste
PASSWORD1="123456"
PASSWORD2="minhasenha"
PASSWORD3="teste123"

echo "1. Criando usuário com senha '123456':"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Teste Hash 1",
    "email": "hash1@email.com",
    "password": "'$PASSWORD1'",
    "firstName": "Hash",
    "lastName": "Teste1",
    "phoneNumber": "(11) 11111-1111"
}'

echo -e "\n\n2. Criando usuário com senha 'minhasenha':"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Teste Hash 2",
    "email": "hash2@email.com",
    "password": "'$PASSWORD2'",
    "firstName": "Hash",
    "lastName": "Teste2",
    "phoneNumber": "(11) 22222-2222"
}'

echo -e "\n\n3. Criando usuário com senha 'teste123':"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Teste Hash 3",
    "email": "hash3@email.com",
    "password": "'$PASSWORD3'",
    "firstName": "Hash",
    "lastName": "Teste3",
    "phoneNumber": "(11) 33333-3333"
}'

echo -e "\n\n=== Análise do Formato de Hash ==="
echo ""
echo "Execute as seguintes consultas SQL para verificar o formato dos hashes:"
echo ""
echo "SELECT email, password FROM tblcontacts WHERE email LIKE '%hash%' ORDER BY id DESC LIMIT 3;"
echo ""
echo "Os hashes devem ter o formato:"
echo "- Iniciar com \$P\$ (para PHPass)"
echo "- Seguido de um caractere (índice de força)"
echo "- Seguido de 8 caracteres (salt)"
echo "- Seguido de 22 caracteres (hash MD5)"
echo ""
echo "Exemplo de hash válido: \$P\$9abcdefghijklmnopqrstuvwxyz"
echo ""
echo "Se os hashes estiverem no formato correto, a compatibilidade com PHP deve funcionar."
echo ""
echo "=== Teste de Login ==="
echo "Teste os logins para verificar se funcionam:"
echo ""

echo "4. Testando login com senha '123456':"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "hash1@email.com",
    "password": "'$PASSWORD1'"
}'

echo -e "\n\n5. Testando login com senha 'minhasenha':"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "hash2@email.com",
    "password": "'$PASSWORD2'"
}'

echo -e "\n\n6. Testando login com senha 'teste123':"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "hash3@email.com",
    "password": "'$PASSWORD3'"
}'

echo -e "\n\n=== Teste Concluído ==="
echo "Verifique se todos os logins funcionaram."
echo "Se funcionarem, o sistema Java está gerando hashes compatíveis com PHP."

#!/bin/bash

# Script para testar especificamente o hash da senha
# Este script cria usuários com senhas conhecidas e testa o login

echo "=== Teste de Hash de Senha ==="
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

echo -e "\n\n3. Criando usuário via register/site com senha 'teste123':"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'preference="descricao"' \
--form 'description_site="Teste de hash via register/site"' \
--form 'type_site="site"' \
--form 'nome_site="Site Hash Teste"' \
--form 'dominio="www.sitehashteste.com"' \
--form 'firstName="Hash"' \
--form 'lastName="Site"' \
--form 'phoneNumber="(11) 33333-3333"' \
--form 'email="hash3@email.com"' \
--form 'password="'$PASSWORD3'"'

echo -e "\n\n4. Testando login com senha '123456':"
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

echo -e "\n\n=== Teste de Hash Concluído ==="
echo "Verifique se todos os logins funcionaram."
echo "Se algum login falhar, há problema no processamento do hash da senha."
echo ""
echo "Consultas SQL para verificar os hashes:"
echo "SELECT id, email, password FROM tblcontacts WHERE email LIKE '%hash%' ORDER BY id DESC LIMIT 3;"
echo ""
echo "Os hashes devem ser diferentes para senhas diferentes."

#!/bin/bash

# Script para debugar problemas de senha
# Este script testa se as senhas estão sendo recebidas e processadas corretamente

echo "=== Debug de Senhas - Teste de Recebimento e Processamento ==="
echo ""

# Senha de teste
TEST_PASSWORD="minhasenha123"
TEST_EMAIL="debug.senha@email.com"

echo "1. Testando auth/register com senha específica:"
echo "   - Senha enviada: $TEST_PASSWORD"
echo "   - Email: $TEST_EMAIL"
echo ""

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Debug",
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'",
    "firstName": "Debug",
    "lastName": "Senha",
    "phoneNumber": "(11) 99999-9999",
    "zip": "01234567",
    "city": "São Paulo",
    "state": "SP",
    "address": "Rua Debug, 123"
}'

echo -e "\n\n2. Testando register/site com senha específica:"
echo "   - Senha enviada: $TEST_PASSWORD"
echo "   - Email: debug.site@email.com"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'preference="descricao"' \
--form 'description_site="Teste de debug de senha no registro de site"' \
--form 'type_site="site"' \
--form 'nome_site="Site Debug Senha"' \
--form 'dominio="www.sitedebugsenha.com"' \
--form 'firstName="Debug"' \
--form 'lastName="Site"' \
--form 'phoneNumber="(11) 88888-8888"' \
--form 'email="debug.site@email.com"' \
--form 'password="'$TEST_PASSWORD'"'

echo -e "\n\n3. Testando login com a senha enviada:"
echo "   - Tentando fazer login com a senha: $TEST_PASSWORD"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'"
}'

echo -e "\n\n4. Testando login com senha diferente (deve falhar):"
echo "   - Tentando fazer login com senha errada: senhaerrada"
echo ""

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "'$TEST_EMAIL'",
    "password": "senhaerrada"
}'

echo -e "\n\n=== Debug Concluído ==="
echo "Verifique os resultados:"
echo "1. Registro auth/register: deve funcionar"
echo "2. Registro register/site: deve funcionar"
echo "3. Login com senha correta: deve funcionar"
echo "4. Login com senha errada: deve falhar"
echo ""
echo "Se o login com senha correta falhar, há problema no processamento da senha."
echo "Se o login com senha errada funcionar, há problema na validação."
echo ""
echo "Consultas SQL para verificar:"
echo "SELECT id, userid, email, password FROM tblcontacts WHERE email LIKE '%debug%' ORDER BY id DESC LIMIT 2;"
echo ""
echo "Compare os hashes das senhas para ver se são diferentes."

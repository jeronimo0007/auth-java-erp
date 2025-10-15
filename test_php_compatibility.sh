#!/bin/bash

# Script para testar compatibilidade com sistema PHP
# Este script testa se as senhas geradas no Java são compatíveis com o PHP

echo "=== Teste de Compatibilidade com Sistema PHP ==="
echo ""

echo "Configurações PHP:"
echo "- PHPASS_HASH_STRENGTH = 8"
echo "- PHPASS_HASH_PORTABLE = false"
echo ""
echo "Configurações Java (após ajuste):"
echo "- iterationCountLog2 = 8"
echo "- portableHashes = false"
echo ""

# Senha de teste
TEST_PASSWORD="senha123"
TEST_EMAIL="compatibilidade.php@email.com"

echo "1. Criando usuário no sistema Java:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: $TEST_PASSWORD"
echo ""

REGISTER_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Compatibilidade PHP",
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'",
    "firstName": "Compatibilidade",
    "lastName": "PHP",
    "phoneNumber": "(11) 99999-9999"
}')

echo "$REGISTER_RESPONSE"

echo -e "\n\n2. Testando login no sistema Java:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: $TEST_PASSWORD"
echo ""

LOGIN_JAVA_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'"
}')

echo "$LOGIN_JAVA_RESPONSE"

echo -e "\n\n3. Testando login com senha ligeiramente diferente:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: senha124 (um caractere diferente)"
echo ""

LOGIN_WRONG_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "'$TEST_EMAIL'",
    "password": "senha124"
}')

echo "$LOGIN_WRONG_RESPONSE"

echo -e "\n\n=== Análise dos Resultados ==="
echo ""

# Verificar se o registro foi bem-sucedido
if echo "$REGISTER_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Registro no sistema Java foi bem-sucedido"
else
    echo "❌ Registro no sistema Java falhou"
fi

# Verificar se o login no Java foi bem-sucedido
if echo "$LOGIN_JAVA_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Login no sistema Java foi bem-sucedido"
else
    echo "❌ Login no sistema Java falhou"
fi

# Verificar se o login com senha errada falhou
if echo "$LOGIN_WRONG_RESPONSE" | grep -q '"success":false'; then
    echo "✅ Login com senha errada falhou corretamente"
else
    echo "❌ Login com senha errada funcionou (problema de segurança!)"
fi

echo -e "\n\n=== Próximos Passos ==="
echo "1. Teste o login no sistema PHP com as mesmas credenciais:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: $TEST_PASSWORD"
echo ""
echo "2. Se o login no PHP funcionar, a compatibilidade foi estabelecida"
echo "3. Se não funcionar, pode ser necessário ajustar mais configurações"
echo ""
echo "=== Consultas SQL para Investigar ==="
echo "SELECT id, email, password FROM tblcontacts WHERE email = '$TEST_EMAIL';"
echo ""
echo "Compare o hash gerado com os hashes do sistema PHP."
echo "O formato deve ser similar: \$P\$[caractere][salt][hash]"

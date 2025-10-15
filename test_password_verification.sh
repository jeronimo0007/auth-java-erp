#!/bin/bash

# Script para verificar especificamente o problema de autenticação
# Este script testa diferentes cenários para identificar onde está o problema

echo "=== Teste de Verificação de Senha ==="
echo ""

# Dados de teste
TEST_EMAIL="verificacao@email.com"
TEST_PASSWORD="senha123"

echo "1. Criando usuário via auth/register:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: $TEST_PASSWORD"
echo ""

REGISTER_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Verificação",
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'",
    "firstName": "Verificação",
    "lastName": "Senha",
    "phoneNumber": "(11) 99999-9999"
}')

echo "$REGISTER_RESPONSE"

echo -e "\n\n2. Tentando login imediatamente após registro:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: $TEST_PASSWORD"
echo ""

LOGIN_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'"
}')

echo "$LOGIN_RESPONSE"

echo -e "\n\n3. Testando com senha ligeiramente diferente:"
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

echo -e "\n\n4. Testando com senha completamente diferente:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: outrasenha"
echo ""

LOGIN_DIFFERENT_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "'$TEST_EMAIL'",
    "password": "outrasenha"
}')

echo "$LOGIN_DIFFERENT_RESPONSE"

echo -e "\n\n=== Análise dos Resultados ==="
echo ""

# Verificar se o registro foi bem-sucedido
if echo "$REGISTER_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Registro foi bem-sucedido"
else
    echo "❌ Registro falhou"
    echo "Resposta: $REGISTER_RESPONSE"
fi

# Verificar se o login foi bem-sucedido
if echo "$LOGIN_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Login com senha correta foi bem-sucedido"
else
    echo "❌ Login com senha correta falhou"
    echo "Resposta: $LOGIN_RESPONSE"
fi

# Verificar se o login com senha errada falhou
if echo "$LOGIN_WRONG_RESPONSE" | grep -q '"success":false'; then
    echo "✅ Login com senha errada falhou corretamente"
else
    echo "❌ Login com senha errada funcionou (problema de segurança!)"
    echo "Resposta: $LOGIN_WRONG_RESPONSE"
fi

echo -e "\n\n=== Diagnóstico ==="
echo "Se o login com senha correta falhar:"
echo "- Problema no hash da senha durante o registro"
echo "- Problema na verificação da senha durante o login"
echo "- Incompatibilidade entre os algoritmos de hash"
echo ""
echo "Se o login com senha errada funcionar:"
echo "- Problema crítico na validação de senha"
echo "- Hash não está sendo verificado corretamente"
echo ""
echo "Consultas SQL para investigar:"
echo "SELECT id, email, password FROM tblcontacts WHERE email = '$TEST_EMAIL';"
echo ""
echo "Verifique se o hash da senha foi salvo corretamente no banco."

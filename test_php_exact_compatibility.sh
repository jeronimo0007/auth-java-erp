#!/bin/bash

# Script para testar compatibilidade exata com PHP
# Este script testa se as senhas geradas no Java são exatamente compatíveis com o PHP

echo "=== Teste de Compatibilidade Exata com PHP ==="
echo ""

echo "Ajustes realizados no Java para corresponder ao PHP:"
echo "1. Configuração: PasswordHash(8, false) - igual ao PHP"
echo "2. gensaltPrivate: usa +5 (equivalente a PHP 5+)"
echo "3. cryptPrivate: loop MD5 corrigido para hash + password"
echo ""

# Senha de teste
TEST_PASSWORD="senha123"
TEST_EMAIL="compatibilidade.exata@email.com"

echo "1. Criando usuário no sistema Java:"
echo "   - Email: $TEST_EMAIL"
echo "   - Senha: $TEST_PASSWORD"
echo ""

REGISTER_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Compatibilidade Exata",
    "email": "'$TEST_EMAIL'",
    "password": "'$TEST_PASSWORD'",
    "firstName": "Compatibilidade",
    "lastName": "Exata",
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
echo "3. Se não funcionar, verifique o formato do hash no banco"
echo ""
echo "=== Consultas SQL para Investigar ==="
echo "SELECT id, email, password FROM tblcontacts WHERE email = '$TEST_EMAIL';"
echo ""
echo "O hash deve ter o formato: \$P\$[caractere][salt][hash]"
echo "Exemplo: \$P\$9abcdefghijklmnopqrstuvwxyz"
echo ""
echo "=== Teste Adicional ==="
echo "Crie um usuário no sistema PHP com a mesma senha e compare os hashes:"
echo "1. Hash do Java: [consulte o banco]"
echo "2. Hash do PHP: [consulte o banco do PHP]"
echo "3. Se forem idênticos, a compatibilidade está perfeita"

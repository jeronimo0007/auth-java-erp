#!/bin/bash

# Script para testar comportamento com cliente existente
# Este script verifica se a senha não é alterada quando user_id existe

echo "=== Teste de Cliente Existente - Preservação de Senha ==="
echo ""

# Primeiro, vamos criar um cliente via auth/register
echo "1. Criando cliente inicial via auth/register:"
echo "   - Email: cliente.existente@email.com"
echo "   - Senha: senhaoriginal123"
echo ""

REGISTER_RESPONSE=$(curl -s --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "company": "Empresa Cliente Existente",
    "email": "cliente.existente@email.com",
    "password": "senhaoriginal123",
    "firstName": "Cliente",
    "lastName": "Existente",
    "phoneNumber": "(11) 11111-1111"
}')

echo "$REGISTER_RESPONSE"

# Extrair o user_id da resposta
USER_ID=$(echo "$REGISTER_RESPONSE" | grep -o '"userId":[0-9]*' | grep -o '[0-9]*')

if [ -z "$USER_ID" ]; then
    echo "❌ Não foi possível obter o user_id. Usando ID fixo para teste."
    USER_ID="1"
fi

echo -e "\n\n2. Testando login com senha original:"
echo "   - Email: cliente.existente@email.com"
echo "   - Senha: senhaoriginal123"
echo ""

LOGIN_ORIGINAL=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "cliente.existente@email.com",
    "password": "senhaoriginal123"
}')

echo "$LOGIN_ORIGINAL"

echo -e "\n\n3. Criando site para cliente existente (user_id=$USER_ID):"
echo "   - NÃO deve alterar a senha do cliente"
echo "   - Deve manter a senha original"
echo ""

SITE_RESPONSE=$(curl -s --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="'$USER_ID'"' \
--form 'preference="descricao"' \
--form 'description_site="Teste de preservação de senha para cliente existente"' \
--form 'type_site="site"' \
--form 'nome_site="Site Cliente Existente"' \
--form 'dominio="www.siteclienteexistente.com"' \
--form 'firstName="Cliente"' \
--form 'lastName="Atualizado"' \
--form 'phoneNumber="(11) 22222-2222"' \
--form 'email="cliente.existente@email.com"')

echo "$SITE_RESPONSE"

echo -e "\n\n4. Testando login APÓS criação do site (deve funcionar com senha original):"
echo "   - Email: cliente.existente@email.com"
echo "   - Senha: senhaoriginal123"
echo ""

LOGIN_AFTER_SITE=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "cliente.existente@email.com",
    "password": "senhaoriginal123"
}')

echo "$LOGIN_AFTER_SITE"

echo -e "\n\n5. Testando login com senha diferente (deve falhar):"
echo "   - Email: cliente.existente@email.com"
echo "   - Senha: senhadiferente"
echo ""

LOGIN_DIFFERENT=$(curl -s --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "cliente.existente@email.com",
    "password": "senhadiferente"
}')

echo "$LOGIN_DIFFERENT"

echo -e "\n\n=== Análise dos Resultados ==="
echo ""

# Verificar se o registro inicial foi bem-sucedido
if echo "$REGISTER_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Registro inicial foi bem-sucedido"
else
    echo "❌ Registro inicial falhou"
fi

# Verificar se o login original funcionou
if echo "$LOGIN_ORIGINAL" | grep -q '"success":true'; then
    echo "✅ Login com senha original funcionou"
else
    echo "❌ Login com senha original falhou"
fi

# Verificar se a criação do site foi bem-sucedida
if echo "$SITE_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Criação de site para cliente existente foi bem-sucedida"
else
    echo "❌ Criação de site para cliente existente falhou"
fi

# Verificar se o login após criação do site ainda funciona
if echo "$LOGIN_AFTER_SITE" | grep -q '"success":true'; then
    echo "✅ Login após criação do site ainda funciona (senha preservada)"
else
    echo "❌ Login após criação do site falhou (senha foi alterada!)"
fi

# Verificar se o login com senha diferente falha
if echo "$LOGIN_DIFFERENT" | grep -q '"success":false'; then
    echo "✅ Login com senha diferente falha corretamente"
else
    echo "❌ Login com senha diferente funcionou (problema de segurança!)"
fi

echo -e "\n\n=== Diagnóstico ==="
echo "Se o login após criação do site falhar:"
echo "- A senha do cliente existente foi alterada incorretamente"
echo "- O sistema não está preservando a senha original"
echo ""
echo "Se o login com senha diferente funcionar:"
echo "- Há problema na validação de senha"
echo "- O hash não está sendo verificado corretamente"
echo ""
echo "Consultas SQL para investigar:"
echo "SELECT id, userid, email, password FROM tblcontacts WHERE email = 'cliente.existente@email.com';"
echo ""
echo "O hash da senha deve ser o mesmo antes e depois da criação do site."

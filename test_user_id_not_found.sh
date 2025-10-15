#!/bin/bash

# Script para testar quando user_id não é encontrado
# Este script testa se o sistema cria um novo cliente quando o user_id não existe

echo "=== Teste de user_id Não Encontrado ==="
echo ""

# ID que provavelmente não existe
NON_EXISTENT_ID=99999

echo "1. Testando com user_id que não existe ($NON_EXISTENT_ID):"
echo "   - Deve criar um novo cliente automaticamente"
echo "   - Deve usar o ID do cliente recém-criado"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form "user_id=\"$NON_EXISTENT_ID\"" \
--form 'preference="descricao"' \
--form 'description_site="crie um site de teste para validar criação automática de cliente quando user_id não existe. O site deve ser moderno e responsivo, com foco em usabilidade e design atrativo."' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste ID Não Encontrado"' \
--form 'dominio="www.sitetesteidnaoencontrado.com"' \
--form 'firstName="Cliente"' \
--form 'lastName="Novo"' \
--form 'phoneNumber="(11) 77777-7777"' \
--form 'email="cliente.novo@email.com"'

echo -e "\n\n2. Testando com user_id válido (para comparação):"
echo "   - Deve usar o cliente existente"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="46"' \
--form 'preference="descricao"' \
--form 'description_site="crie um site de teste para cliente existente. Deve ser moderno e responsivo."' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Cliente Existente"' \
--form 'dominio="www.sitetesteclienteexistente.com"' \
--form 'firstName="Cliente"' \
--form 'lastName="Existente"' \
--form 'phoneNumber="(11) 66666-6666"' \
--form 'email="cliente.existente@email.com"'

echo -e "\n\n=== Teste Concluído ==="
echo "Verifique no banco de dados:"
echo "1. Se um novo cliente foi criado com ID diferente de $NON_EXISTENT_ID"
echo "2. Se o site foi associado ao cliente correto"
echo ""
echo "Consultas SQL para verificar:"
echo "SELECT userid, company FROM tblclients WHERE company LIKE '%Cliente%' ORDER BY userid DESC LIMIT 2;"
echo "SELECT site_id, client_id, nome_site FROM tblsites WHERE nome_site LIKE '%Teste%' ORDER BY site_id DESC LIMIT 2;"

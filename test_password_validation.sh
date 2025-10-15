#!/bin/bash

# Script para testar validação de password obrigatório
# Este script testa diferentes cenários de password

echo "=== Teste de Validação de Password ==="
echo ""

echo "1. Testando com user_id existente (password não obrigatório):"
echo "   - Deve usar senha padrão '123456'"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="46"' \
--form 'preference="descricao"' \
--form 'description_site="Teste com cliente existente - password não obrigatório"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Cliente Existente"' \
--form 'dominio="www.sitetesteclienteexistente.com"' \
--form 'firstName="Cliente"' \
--form 'lastName="Existente"' \
--form 'phoneNumber="(11) 55555-5555"' \
--form 'email="cliente.existente@email.com"'

echo -e "\n\n2. Testando com user_id não existente SEM password (deve falhar):"
echo "   - Deve retornar erro: 'Senha é obrigatória para novos clientes'"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="99999"' \
--form 'preference="descricao"' \
--form 'description_site="Teste sem password - deve falhar"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Sem Password"' \
--form 'dominio="www.sitetestesempassword.com"' \
--form 'firstName="Teste"' \
--form 'lastName="SemPassword"' \
--form 'phoneNumber="(11) 44444-4444"' \
--form 'email="teste.sempassword@email.com"'

echo -e "\n\n3. Testando com user_id não existente COM password (deve funcionar):"
echo "   - Deve criar novo cliente com senha fornecida"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="99998"' \
--form 'preference="descricao"' \
--form 'description_site="Teste com password - deve funcionar"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Com Password"' \
--form 'dominio="www.sitetestecompassword.com"' \
--form 'firstName="Teste"' \
--form 'lastName="ComPassword"' \
--form 'phoneNumber="(11) 33333-3333"' \
--form 'email="teste.compassword@email.com"' \
--form 'password="minhasenha123"'

echo -e "\n\n4. Testando sem user_id SEM password (deve falhar):"
echo "   - Deve retornar erro: 'Senha é obrigatória para novos clientes'"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'preference="descricao"' \
--form 'description_site="Teste sem user_id e sem password - deve falhar"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Sem User ID e Password"' \
--form 'dominio="www.sitetestesemuseridpassword.com"' \
--form 'firstName="Teste"' \
--form 'lastName="SemUserIDPassword"' \
--form 'phoneNumber="(11) 22222-2222"' \
--form 'email="teste.semuseridpassword@email.com"'

echo -e "\n\n5. Testando sem user_id COM password (deve funcionar):"
echo "   - Deve criar novo cliente com senha fornecida"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'preference="descricao"' \
--form 'description_site="Teste sem user_id mas com password - deve funcionar"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Sem User ID Mas Com Password"' \
--form 'dominio="www.sitetestesemuseridmascompassword.com"' \
--form 'firstName="Teste"' \
--form 'lastName="SemUserIDMasComPassword"' \
--form 'phoneNumber="(11) 11111-1111"' \
--form 'email="teste.semuseridmascompassword@email.com"' \
--form 'password="outrasenha456"'

echo -e "\n\n=== Teste Concluído ==="
echo "Verifique os resultados:"
echo "1. Cliente existente: deve funcionar sem password"
echo "2. Novo cliente sem password: deve falhar com erro de senha obrigatória"
echo "3. Novo cliente com password: deve funcionar"
echo "4. Sem user_id sem password: deve falhar com erro de senha obrigatória"
echo "5. Sem user_id com password: deve funcionar"
echo ""
echo "Consultas SQL para verificar:"
echo "SELECT userid, company FROM tblclients WHERE company LIKE '%Teste%' ORDER BY userid DESC LIMIT 3;"
echo "SELECT id, userid, email FROM tblcontacts WHERE email LIKE '%teste%' ORDER BY id DESC LIMIT 3;"

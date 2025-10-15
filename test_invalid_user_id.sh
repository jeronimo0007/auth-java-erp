#!/bin/bash

# Script para testar conversão segura de user_id inválido
# Este script testa diferentes tipos de user_id inválidos

echo "=== Teste de Conversão Segura de user_id ==="
echo ""

echo "1. Testando com user_id string inválida (caracteres especiais):"
echo "   - Deve converter para null e criar novo cliente"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="m"' \
--form 'preference="descricao"' \
--form 'description_site="Teste com user_id inválido - deve criar novo cliente automaticamente"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste ID Inválido 1"' \
--form 'dominio="www.sitetesteidinvalido1.com"' \
--form 'firstName="Teste"' \
--form 'lastName="Inválido1"' \
--form 'phoneNumber="(11) 11111-1111"' \
--form 'email="teste.invalido1@email.com"'

echo -e "\n\n2. Testando com user_id string vazia:"
echo "   - Deve converter para null e criar novo cliente"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id=""' \
--form 'preference="descricao"' \
--form 'description_site="Teste com user_id vazio - deve criar novo cliente automaticamente"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste ID Vazio"' \
--form 'dominio="www.sitetesteidvazio.com"' \
--form 'firstName="Teste"' \
--form 'lastName="Vazio"' \
--form 'phoneNumber="(11) 22222-2222"' \
--form 'email="teste.vazio@email.com"'

echo -e "\n\n3. Testando com user_id string não numérica:"
echo "   - Deve converter para null e criar novo cliente"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="abc123"' \
--form 'preference="descricao"' \
--form 'description_site="Teste com user_id não numérico - deve criar novo cliente automaticamente"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste ID Não Numérico"' \
--form 'dominio="www.sitetesteidnaonumerico.com"' \
--form 'firstName="Teste"' \
--form 'lastName="NãoNumérico"' \
--form 'phoneNumber="(11) 33333-3333"' \
--form 'email="teste.naonumerico@email.com"'

echo -e "\n\n4. Testando com user_id válido (para comparação):"
echo "   - Deve usar o cliente existente ou criar novo se não existir"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="46"' \
--form 'preference="descricao"' \
--form 'description_site="Teste com user_id válido - deve funcionar normalmente"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste ID Válido"' \
--form 'dominio="www.sitetesteidvalido.com"' \
--form 'firstName="Teste"' \
--form 'lastName="Válido"' \
--form 'phoneNumber="(11) 44444-4444"' \
--form 'email="teste.valido@email.com"'

echo -e "\n\n=== Teste Concluído ==="
echo "Verifique no banco de dados se todos os sites foram criados com sucesso:"
echo "SELECT site_id, client_id, nome_site FROM tblsites WHERE nome_site LIKE '%Teste ID%' ORDER BY site_id DESC LIMIT 4;"
echo ""
echo "Todos os testes devem ter sucesso, criando novos clientes quando necessário."

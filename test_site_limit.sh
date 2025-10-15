#!/bin/bash

# Script de teste para validar o limite de sites por cliente
# Este script testa se um cliente pode criar mais de 3 sites

echo "=== Teste de Limite de Sites por Cliente ==="
echo "Testando se cliente pode criar mais de 3 sites..."
echo ""

# Cliente ID para teste (substitua por um ID válido)
CLIENT_ID=46

echo "Tentando criar o 4º site para o cliente ID: $CLIENT_ID"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form "user_id=\"$CLIENT_ID\"" \
--form 'preference="descricao"' \
--form 'description_site="Site de teste para validar limite - Site 4"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste 4"' \
--form 'dominio="www.siteteste4.com"' \
--form 'firstName="Teste"' \
--form 'lastName="Limite"' \
--form 'phoneNumber="(11) 99999-9999"' \
--form 'email="teste.limite@email.com"'

echo -e "\n\n=== Teste Concluído ==="
echo "Se o limite estiver funcionando, você deve ver uma mensagem de erro"
echo "indicando que o limite de sites foi excedido."

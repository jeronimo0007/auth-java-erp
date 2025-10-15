#!/bin/bash

# Script para testar o endpoint de contagem de sites
# Este script testa o endpoint GET /auth/client/{id}/sites/count

echo "=== Teste de Contagem de Sites por Cliente ==="
echo ""

# Cliente ID para teste (substitua por um ID válido)
CLIENT_ID=46

echo "Verificando quantos sites o cliente ID $CLIENT_ID possui..."
echo ""

curl --location "http://localhost:8080/auth/client/$CLIENT_ID/sites/count" \
--header 'Accept: */*'

echo -e "\n\n=== Resposta Esperada ==="
echo "Se o cliente existir, você deve ver:"
echo '{"success": true, "message": "Contagem de sites obtida com sucesso", "data": {"clientId": '$CLIENT_ID', "sitesCount": X}}'
echo ""
echo "Onde X é o número de sites que o cliente possui."
echo ""
echo "Se o cliente não existir, você deve ver:"
echo '{"success": false, "message": "Cliente não encontrado"}'

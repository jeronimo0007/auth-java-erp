#!/bin/bash

# Script para testar se o problema de compilação foi resolvido
# Este script testa o endpoint register/site após a correção

echo "=== Teste de Correção de Compilação ==="
echo ""

echo "1. Testando endpoint register/site com novo cliente:"
echo "   - Deve funcionar sem erros de compilação"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'preference="descricao"' \
--form 'description_site="Teste de correção de compilação"' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Compilação"' \
--form 'dominio="www.sitetestecompilacao.com"' \
--form 'firstName="Teste"' \
--form 'lastName="Compilação"' \
--form 'phoneNumber="(11) 99999-9999"' \
--form 'email="teste.compilacao@email.com"' \
--form 'password="senha123"'

echo -e "\n\n2. Testando endpoint register/site com cliente existente:"
echo "   - Deve funcionar sem erros de compilação"
echo "   - Deve preservar a senha do cliente existente"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="1"' \
--form 'preference="descricao"' \
--form 'description_site="Teste com cliente existente"' \
--form 'type_site="site"' \
--form 'nome_site="Site Cliente Existente"' \
--form 'dominio="www.siteclienteexistente.com"' \
--form 'firstName="Cliente"' \
--form 'lastName="Existente"' \
--form 'phoneNumber="(11) 88888-8888"' \
--form 'email="cliente.existente@email.com"'

echo -e "\n\n3. Testando endpoint register/site sem senha (novo cliente):"
echo "   - Deve retornar erro: 'Senha é obrigatória para novos clientes'"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'preference="descricao"' \
--form 'description_site="Teste sem senha"' \
--form 'type_site="site"' \
--form 'nome_site="Site Sem Senha"' \
--form 'dominio="www.sitesemsenha.com"' \
--form 'firstName="Teste"' \
--form 'lastName="SemSenha"' \
--form 'phoneNumber="(11) 77777-7777"' \
--form 'email="teste.semsenha@email.com"'

echo -e "\n\n=== Teste Concluído ==="
echo "Verifique se:"
echo "1. Não há erros de compilação (SiteRegisterRequest cannot be resolved)"
echo "2. Novo cliente com senha funciona"
echo "3. Cliente existente preserva senha"
echo "4. Novo cliente sem senha retorna erro apropriado"
echo ""
echo "Se todos os testes passarem, o problema de compilação foi resolvido!"

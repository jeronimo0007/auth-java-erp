#!/bin/bash

# Script de teste para o endpoint de registro de site
# Testa o cenário com user_id existente e preference="descricao"

echo "Testando registro de site com user_id existente..."

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'email="teste@email.com"' \
--form 'user_id="46"' \
--form 'preference="descricao"' \
--form 'description_site="crie um site de conveniência moderno e fácil de usar. O site deve ter uma página inicial com destaque para promoções e novidades, uma seção com os produtos disponíveis organizados por categorias (alimentos, bebidas, itens de higiene, etc.), e uma opção de compra rápida. Deve incluir também informações de contato, horário de funcionamento e localização, além de um design limpo, atrativo e responsivo, compatível com dispositivos móveis. Inclua elementos visuais que transmitam praticidade e conveniência, com cores alegres e intuitivas."' \
--form 'type_site="site"' \
--form 'nome_site="Site de Teste"' \
--form 'dominio="www.testesite.com"' \
--form 'firstName="João"' \
--form 'lastName="Silva"' \
--form 'phoneNumber="(11) 99999-9999"'

echo -e "\n\nTeste concluído!"

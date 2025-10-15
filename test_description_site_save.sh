#!/bin/bash

# Script para testar se o contexto está sendo salvo no campo description_site
# Este script testa tanto a jornada "descricao" quanto a jornada padrão

echo "=== Teste de Salvamento do Contexto em description_site ==="
echo ""

echo "1. Testando jornada com preference='descricao':"
echo "   - Deve salvar o description_site fornecido no campo description_site da tabela"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="46"' \
--form 'preference="descricao"' \
--form 'description_site="crie um site de conveniência moderno e fácil de usar. O site deve ter uma página inicial com destaque para promoções e novidades, uma seção com os produtos disponíveis organizados por categorias (alimentos, bebidas, itens de higiene, etc.), e uma opção de compra rápida. Deve incluir também informações de contato, horário de funcionamento e localização, além de um design limpo, atrativo e responsivo, compatível com dispositivos móveis. Inclua elementos visuais que transmitam praticidade e conveniência, com cores alegres e intuitivas."' \
--form 'type_site="site"' \
--form 'nome_site="Site Teste Descrição"' \
--form 'dominio="www.sitetestedescricao.com"' \
--form 'firstName="Teste"' \
--form 'lastName="Descrição"' \
--form 'phoneNumber="(11) 99999-9999"' \
--form 'email="teste.descricao@email.com"'

echo -e "\n\n2. Testando jornada padrão (sem preference):"
echo "   - Deve salvar o contexto completo gerado no campo description_site da tabela"
echo ""

curl --location 'http://localhost:8080/register/site' \
--header 'Accept: */*' \
--form 'user_id="46"' \
--form 'nome_site="Site Teste Padrão"' \
--form 'dominio="www.sitetestepadrao.com"' \
--form 'tipo_site="site"' \
--form 'descricao_negocio="Empresa de tecnologia focada em soluções inovadoras"' \
--form 'publico_alvo="Empresas de médio e grande porte"' \
--form 'banner_texto="Soluções Tecnológicas para o Futuro"' \
--form 'quem_somos="Somos uma empresa especializada em desenvolvimento de software"' \
--form 'servicos="Desenvolvimento de aplicações web e mobile"' \
--form 'email_empresa="contato@empresa.com"' \
--form 'telefone_empresa="(11) 3333-4444"' \
--form 'endereco_empresa="Rua das Flores, 123 - São Paulo/SP"' \
--form 'secao1_titulo="Nossos Serviços"' \
--form 'secao1_conteudo="Oferecemos soluções completas em tecnologia"' \
--form 'secao2_titulo="Nossa Equipe"' \
--form 'secao2_conteudo="Profissionais altamente qualificados"' \
--form 'cor_principal="#0066cc"' \
--form 'cor_secundaria="#ffffff"' \
--form 'estilo="moderno"' \
--form 'firstName="Teste"' \
--form 'lastName="Padrão"' \
--form 'phoneNumber="(11) 88888-8888"' \
--form 'email="teste.padrao@email.com"'

echo -e "\n\n=== Teste Concluído ==="
echo "Verifique no banco de dados se o campo 'description_site' foi preenchido corretamente:"
echo "SELECT site_id, nome_site, description_site FROM tblsites WHERE nome_site LIKE '%Teste%' ORDER BY site_id DESC LIMIT 2;"

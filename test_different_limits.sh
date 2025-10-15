#!/bin/bash

# Script para testar diferentes limites de sites
# Este script mostra como configurar diferentes limites via variável de ambiente

echo "=== Teste de Diferentes Limites de Sites ==="
echo ""

echo "1. Testando com limite padrão (3 sites):"
echo "   MAX_SITES_PER_CLIENT=3 ./mvnw spring-boot:run"
echo ""

echo "2. Testando com limite de 5 sites:"
echo "   MAX_SITES_PER_CLIENT=5 ./mvnw spring-boot:run"
echo ""

echo "3. Testando com limite de 1 site:"
echo "   MAX_SITES_PER_CLIENT=1 ./mvnw spring-boot:run"
echo ""

echo "4. Testando com limite ilimitado (999):"
echo "   MAX_SITES_PER_CLIENT=999 ./mvnw spring-boot:run"
echo ""

echo "=== Como Testar ==="
echo "1. Pare o servidor atual"
echo "2. Execute um dos comandos acima"
echo "3. Teste criando sites até atingir o limite"
echo "4. Verifique se a mensagem de erro aparece corretamente"
echo ""

echo "=== Mensagem de Erro Esperada ==="
echo "Limite de sites excedido. Cada cliente pode criar no máximo X sites. Cliente atual possui Y sites."

#!/bin/bash

# Script de deploy para produção
# Este script será executado no VPS via GitHub Actions

set -e

echo "🚀 Iniciando deploy de produção..."

# Variáveis
IMAGE_NAME="ghcr.io/jeronimo0007/auth-java-erp:latest"
CONTAINER_NAME="auth-prod"
PORT="8080"

# Login no GitHub Container Registry
echo "🔐 Fazendo login no GitHub Container Registry..."
echo "$GITHUB_TOKEN" | docker login ghcr.io -u "$GITHUB_ACTOR" --password-stdin

# Parar e remover container existente
echo "🛑 Parando container de produção existente..."
docker stop "$CONTAINER_NAME" 2>/dev/null || true
docker rm "$CONTAINER_NAME" 2>/dev/null || true

# Remover imagem antiga
echo "🗑️ Removendo imagem antiga..."
docker rmi "$IMAGE_NAME" 2>/dev/null || true

# Baixar nova imagem
echo "📥 Baixando nova imagem..."
docker pull "$IMAGE_NAME"

# Criar diretório de uploads se não existir
echo "📁 Criando diretório de uploads..."
sudo mkdir -p /var/app/uploads
sudo chown -R 1000:1000 /var/app/uploads

# Executar novo container
echo "🏃 Executando novo container de produção..."
docker run -d \
  --name "$CONTAINER_NAME" \
  --restart unless-stopped \
  -p "$PORT:8080" \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="$DB_URL" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  -e JWT_SECRET="$JWT_SECRET" \
  -e JWT_EXPIRATION="$JWT_EXPIRATION" \
  -e STORAGE_S3_NAME_SPACE="$STORAGE_S3_NAME_SPACE" \
  -e STORAGE_S3_ACCESS_KEY="$STORAGE_S3_ACCESS_KEY" \
  -e STORAGE_S3_SECRET_KEY="$STORAGE_S3_SECRET_KEY" \
  -e RABBITMQ_HOST="$RABBITMQ_HOST" \
  -e RABBITMQ_USERNAME="$RABBITMQ_USERNAME" \
  -e RABBITMQ_PASSWORD="$RABBITMQ_PASSWORD" \
  -v /var/app/uploads:/app/uploads \
  "$IMAGE_NAME"

# Aguardar aplicação inicializar
echo "⏳ Aguardando aplicação inicializar..."
sleep 15

# Verificar se o container está rodando
echo "🔍 Verificando status do container..."
if docker ps | grep -q "$CONTAINER_NAME"; then
    echo "✅ Container de produção está rodando!"
    echo "🌐 Aplicação disponível em: http://$(curl -s ifconfig.me):$PORT"
else
    echo "❌ Erro: Container não está rodando!"
    echo "📋 Logs do container:"
    docker logs "$CONTAINER_NAME"
    exit 1
fi

# Limpar imagens não utilizadas
echo "🧹 Limpando imagens não utilizadas..."
docker image prune -f

echo "🎉 Deploy de produção concluído com sucesso!"

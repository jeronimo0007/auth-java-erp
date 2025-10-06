#!/bin/bash

# Script para configurar o VPS para receber deploys
# Execute este script uma vez no seu VPS

set -e

echo "🔧 Configurando VPS para deploys automáticos..."

# Atualizar sistema
echo "📦 Atualizando sistema..."
sudo apt update && sudo apt upgrade -y

# Instalar Docker
echo "🐳 Instalando Docker..."
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    rm get-docker.sh
    echo "✅ Docker instalado com sucesso!"
else
    echo "✅ Docker já está instalado!"
fi

# Instalar Docker Compose
echo "🐙 Instalando Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "✅ Docker Compose instalado com sucesso!"
else
    echo "✅ Docker Compose já está instalado!"
fi

# Criar diretórios necessários
echo "📁 Criando diretórios..."
sudo mkdir -p /var/app/uploads
sudo mkdir -p /var/app/uploads-dev
sudo chown -R $USER:$USER /var/app

# Configurar firewall
echo "🔥 Configurando firewall..."
sudo ufw allow 22/tcp   # SSH
sudo ufw allow 8080/tcp # Produção
sudo ufw allow 8081/tcp # Desenvolvimento
sudo ufw --force enable

# Configurar Docker para iniciar automaticamente
echo "🔄 Configurando Docker para iniciar automaticamente..."
sudo systemctl enable docker
sudo systemctl start docker

# Instalar ferramentas úteis
echo "🛠️ Instalando ferramentas úteis..."
sudo apt install -y curl wget git htop

# Configurar logrotate para containers Docker
echo "📋 Configurando rotação de logs..."
sudo tee /etc/logrotate.d/docker-containers > /dev/null <<EOF
/var/lib/docker/containers/*/*.log {
    rotate 7
    daily
    compress
    size=1M
    missingok
    delaycompress
    copytruncate
}
EOF

echo "🎉 Configuração do VPS concluída com sucesso!"
echo ""
echo "📋 Próximos passos:"
echo "1. Faça logout e login novamente para aplicar as permissões do Docker"
echo "2. Configure as variáveis de ambiente no GitHub Secrets"
echo "3. Teste o deploy fazendo um push para a branch develop ou main"
echo ""
echo "🌐 Portas configuradas:"
echo "- Produção: 8080"
echo "- Desenvolvimento: 8081"

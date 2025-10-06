#!/bin/bash

# Script para gerenciar containers de produção e desenvolvimento

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para mostrar ajuda
show_help() {
    echo -e "${BLUE}🐳 Gerenciador de Containers${NC}"
    echo ""
    echo "Uso: $0 [COMANDO] [AMBIENTE]"
    echo ""
    echo "Comandos:"
    echo "  start     - Iniciar containers"
    echo "  stop      - Parar containers"
    echo "  restart   - Reiniciar containers"
    echo "  status    - Mostrar status dos containers"
    echo "  logs      - Mostrar logs dos containers"
    echo "  clean     - Limpar containers e imagens não utilizadas"
    echo "  backup    - Fazer backup dos uploads"
    echo ""
    echo "Ambientes:"
    echo "  prod      - Apenas produção"
    echo "  dev       - Apenas desenvolvimento"
    echo "  all       - Ambos (padrão)"
    echo ""
    echo "Exemplos:"
    echo "  $0 start prod"
    echo "  $0 logs dev"
    echo "  $0 restart all"
}

# Função para mostrar status
show_status() {
    echo -e "${BLUE}📊 Status dos Containers:${NC}"
    echo ""
    
    if docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(auth-prod|auth-dev|nginx-proxy)"; then
        echo ""
        echo -e "${GREEN}✅ Containers encontrados${NC}"
    else
        echo -e "${YELLOW}⚠️ Nenhum container encontrado${NC}"
    fi
}

# Função para mostrar logs
show_logs() {
    local env=$1
    
    case $env in
        "prod")
            echo -e "${BLUE}📋 Logs de Produção:${NC}"
            docker logs --tail=50 -f auth-prod
            ;;
        "dev")
            echo -e "${BLUE}📋 Logs de Desenvolvimento:${NC}"
            docker logs --tail=50 -f auth-dev
            ;;
        "all")
            echo -e "${BLUE}📋 Logs de Todos os Containers:${NC}"
            docker logs --tail=20 auth-prod auth-dev nginx-proxy
            ;;
    esac
}

# Função para iniciar containers
start_containers() {
    local env=$1
    
    case $env in
        "prod")
            echo -e "${GREEN}🚀 Iniciando container de produção...${NC}"
            docker-compose -f docker-compose.prod.yml up -d
            ;;
        "dev")
            echo -e "${GREEN}🚀 Iniciando container de desenvolvimento...${NC}"
            docker-compose -f docker-compose.dev.yml up -d
            ;;
        "all")
            echo -e "${GREEN}🚀 Iniciando todos os containers...${NC}"
            docker-compose -f docker-compose.prod.yml up -d
            docker-compose -f docker-compose.dev.yml up -d
            ;;
    esac
}

# Função para parar containers
stop_containers() {
    local env=$1
    
    case $env in
        "prod")
            echo -e "${YELLOW}🛑 Parando container de produção...${NC}"
            docker stop auth-prod || true
            ;;
        "dev")
            echo -e "${YELLOW}🛑 Parando container de desenvolvimento...${NC}"
            docker stop auth-dev || true
            ;;
        "all")
            echo -e "${YELLOW}🛑 Parando todos os containers...${NC}"
            docker stop auth-prod auth-dev nginx-proxy || true
            ;;
    esac
}

# Função para reiniciar containers
restart_containers() {
    local env=$1
    
    case $env in
        "prod")
            echo -e "${BLUE}🔄 Reiniciando container de produção...${NC}"
            docker restart auth-prod
            ;;
        "dev")
            echo -e "${BLUE}🔄 Reiniciando container de desenvolvimento...${NC}"
            docker restart auth-dev
            ;;
        "all")
            echo -e "${BLUE}🔄 Reiniciando todos os containers...${NC}"
            docker restart auth-prod auth-dev nginx-proxy
            ;;
    esac
}

# Função para limpeza
clean_containers() {
    echo -e "${YELLOW}🧹 Limpando containers e imagens não utilizadas...${NC}"
    
    # Parar containers
    docker stop auth-prod auth-dev nginx-proxy 2>/dev/null || true
    
    # Remover containers
    docker rm auth-prod auth-dev nginx-proxy 2>/dev/null || true
    
    # Remover imagens não utilizadas
    docker image prune -f
    
    # Remover volumes não utilizados
    docker volume prune -f
    
    echo -e "${GREEN}✅ Limpeza concluída!${NC}"
}

# Função para backup
backup_uploads() {
    local timestamp=$(date +"%Y%m%d_%H%M%S")
    local backup_dir="/var/backups/uploads_$timestamp"
    
    echo -e "${BLUE}💾 Fazendo backup dos uploads...${NC}"
    
    # Criar diretório de backup
    sudo mkdir -p "$backup_dir"
    
    # Backup de produção
    if [ -d "/var/app/uploads" ]; then
        sudo cp -r /var/app/uploads "$backup_dir/uploads_prod"
        echo -e "${GREEN}✅ Backup de produção criado${NC}"
    fi
    
    # Backup de desenvolvimento
    if [ -d "/var/app/uploads-dev" ]; then
        sudo cp -r /var/app/uploads-dev "$backup_dir/uploads_dev"
        echo -e "${GREEN}✅ Backup de desenvolvimento criado${NC}"
    fi
    
    # Comprimir backup
    sudo tar -czf "$backup_dir.tar.gz" -C /var/backups "uploads_$timestamp"
    sudo rm -rf "$backup_dir"
    
    echo -e "${GREEN}✅ Backup criado: $backup_dir.tar.gz${NC}"
}

# Verificar se Docker está rodando
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker não está rodando!${NC}"
    exit 1
fi

# Processar argumentos
command=$1
environment=${2:-all}

# Validar comando
case $command in
    "start"|"stop"|"restart"|"status"|"logs"|"clean"|"backup"|"help"|"-h"|"--help")
        ;;
    *)
        echo -e "${RED}❌ Comando inválido: $command${NC}"
        show_help
        exit 1
        ;;
esac

# Validar ambiente
case $environment in
    "prod"|"dev"|"all")
        ;;
    *)
        echo -e "${RED}❌ Ambiente inválido: $environment${NC}"
        show_help
        exit 1
        ;;
esac

# Executar comando
case $command in
    "start")
        start_containers $environment
        ;;
    "stop")
        stop_containers $environment
        ;;
    "restart")
        restart_containers $environment
        ;;
    "status")
        show_status
        ;;
    "logs")
        show_logs $environment
        ;;
    "clean")
        clean_containers
        ;;
    "backup")
        backup_uploads
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
esac

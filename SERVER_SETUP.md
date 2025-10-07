# 🖥️ Configuração do Servidor Ubuntu 23

Este guia te ajudará a configurar um servidor Ubuntu 23 do zero para rodar a aplicação auth-java-erp com RabbitMQ em container, conectando-se a serviços externos (banco de dados MySQL e blob storage).

## 📋 Pré-requisitos

- Servidor Ubuntu 23 (virgem)
- Acesso SSH ao servidor
- Banco de dados MySQL externo (já configurado)
- Blob Storage (S3/DigitalOcean Spaces) configurado
- Repositório: [jeronimo0007/auth-java-erp](https://github.com/jeronimo0007/auth-java-erp)

## 🚀 Configuração Completa

### **PASSO 1: Conectar e Atualizar o Servidor**

#### 1.1 Conectar ao servidor
```bash
ssh root@seu-ip-do-servidor
# ou
ssh ubuntu@seu-ip-do-servidor
```

#### 1.2 Atualizar o sistema
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git htop nano
```

### **PASSO 2: Instalar Docker**

#### 2.1 Remover versões antigas
```bash
sudo apt remove -y docker docker-engine docker.io containerd runc
```

#### 2.2 Instalar dependências
```bash
sudo apt install -y ca-certificates curl gnupg lsb-release
```

#### 2.3 Adicionar chave GPG oficial do Docker
```bash
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

#### 2.4 Adicionar repositório do Docker
```bash
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

#### 2.5 Instalar Docker
```bash
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

#### 2.6 Adicionar usuário ao grupo docker
```bash
sudo usermod -aG docker $USER
```

### **PASSO 3: Instalar Docker Compose**

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### **PASSO 4: Configurar Docker**

#### 4.1 Habilitar Docker para iniciar automaticamente
```bash
sudo systemctl enable docker
sudo systemctl start docker
```

#### 4.2 Fazer logout e login novamente
```bash
exit
ssh root@seu-ip-do-servidor
```

#### 4.3 Verificar instalação
```bash
docker --version
docker-compose --version
```

### **PASSO 5: Configurar Firewall**

#### 5.1 Instalar UFW
```bash
sudo apt install -y ufw
```

#### 5.2 Configurar regras básicas
```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
```

#### 5.3 Permitir portas necessárias
```bash
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 8080/tcp  # Aplicação Produção
sudo ufw allow 8081/tcp  # Aplicação Desenvolvimento
sudo ufw allow 5672/tcp  # RabbitMQ
sudo ufw allow 15672/tcp # RabbitMQ Management
```

#### 5.4 Ativar firewall
```bash
sudo ufw --force enable
sudo ufw status
```

### **PASSO 6: Criar Estrutura de Diretórios**

```bash
# Criar diretórios para a aplicação (apenas para logs e cache local)
sudo mkdir -p /var/app/logs
sudo mkdir -p /var/app/cache
sudo mkdir -p /var/app/rabbitmq-data

# Definir permissões
sudo chown -R $USER:$USER /var/app
```

### **PASSO 7: Configurar RabbitMQ**

#### 7.1 Criar arquivo de configuração
```bash
nano /var/app/docker-compose.rabbitmq.yml
```

#### 7.2 Conteúdo do arquivo:
```yaml
version: '3.8'

services:
  rabbitmq:
    image: rabbitmq:3.12-management
    container_name: rabbitmq-server
    restart: unless-stopped
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - /var/app/rabbitmq-data:/var/lib/rabbitmq
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
```

#### 7.3 Iniciar RabbitMQ
```bash
cd /var/app
docker-compose -f docker-compose.rabbitmq.yml up -d
```

#### 7.4 Verificar se está rodando
```bash
docker ps
```

### **PASSO 8: Verificar RabbitMQ**

```bash
# Verificar se RabbitMQ está rodando
docker logs rabbitmq-server

# Acessar interface web (opcional)
# http://seu-ip:15672
# Usuário: guest
# Senha: guest
```

### **PASSO 9: Gerar Chave SSH para GitHub Actions**

#### 9.1 Gerar chave SSH
```bash
ssh-keygen -t rsa -b 4096 -C "jeronimo.alvescardoso@gmail.com"
# Pressione Enter para usar local padrão
# Pressione Enter para não usar senha
```

#### 9.2 Configurar authorized_keys
```bash
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

#### 9.3 Copiar chave privada
```bash
cat ~/.ssh/id_rsa
# COPIE TODO O CONTEÚDO - você vai precisar no GitHub
```

### **PASSO 10: Configurar GitHub Secrets**

#### 10.1 Ir para o GitHub
Vá para: [https://github.com/jeronimo0007/auth-java-erp/settings/secrets/actions](https://github.com/jeronimo0007/auth-java-erp/settings/secrets/actions)

#### 10.2 Adicionar os seguintes secrets:

| Nome do Secret | Valor |
|----------------|-------|
| `VPS_HOST` | `SEU-IP-DO-SERVIDOR` |
| `VPS_USERNAME` | `root` (ou `ubuntu` se usar esse usuário) |
| `VPS_SSH_KEY` | Conteúdo completo do `~/.ssh/id_rsa` |
| `VPS_PORT` | `22` |
| `DB_URL` | `jdbc:mysql://195.200.1.59:3306/app_omny?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | `api` |
| `DB_PASSWORD` | `c6m8n4d2d3` |
| `JWT_SECRET` | `mySecretKey123456789012345678901234567890` |
| `JWT_EXPIRATION` | `86400` |
| `STORAGE_S3_NAME_SPACE` | `seu-bucket-name` |
| `STORAGE_S3_ACCESS_KEY` | `sua-access-key` |
| `STORAGE_S3_SECRET_KEY` | `sua-secret-key` |
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_USERNAME` | `guest` |
| `RABBITMQ_PASSWORD` | `guest` |

### **PASSO 11: Baixar Scripts de Deploy**

```bash
cd /var/app
curl -O https://raw.githubusercontent.com/jeronimo0007/auth-java-erp/main/scripts/setup-vps.sh
curl -O https://raw.githubusercontent.com/jeronimo0007/auth-java-erp/main/scripts/deploy-prod.sh
curl -O https://raw.githubusercontent.com/jeronimo0007/auth-java-erp/main/scripts/deploy-dev.sh
curl -O https://raw.githubusercontent.com/jeronimo0007/auth-java-erp/main/scripts/manage-containers.sh

# Tornar executáveis
chmod +x *.sh
```

### **PASSO 12: Fazer Primeiro Deploy**

#### 12.1 No seu computador local
```bash
git add .
git commit -m "feat: configuração de deploy automático"
git push origin main
```

#### 12.2 Verificar deploy
- Vá para: [https://github.com/jeronimo0007/auth-java-erp/actions](https://github.com/jeronimo0007/auth-java-erp/actions)
- Aguarde o workflow "Deploy to Production" concluir (5-10 minutos)

#### 12.3 Testar aplicação
```bash
# No servidor
docker ps  # Deve mostrar rabbitmq-server e auth-prod

# Testar aplicação
curl http://localhost:8080/actuator/health
```

## 🎯 URLs de Acesso

Após o deploy, suas aplicações estarão disponíveis em:

- **Produção**: `http://SEU-IP:8080`
- **Desenvolvimento**: `http://SEU-IP:8081` (quando fizer deploy para develop)
- **RabbitMQ Management**: `http://SEU-IP:15672` (guest/guest)

## 🛠️ Comandos Úteis

### Gerenciar Containers
```bash
# Ver todos os containers
docker ps -a

# Ver logs da aplicação
docker logs auth-prod

# Ver logs do RabbitMQ
docker logs rabbitmq-server

# Reiniciar aplicação
docker restart auth-prod

# Parar tudo
docker stop auth-prod rabbitmq-server

# Iniciar RabbitMQ
cd /var/app
docker-compose -f docker-compose.rabbitmq.yml up -d
```

### Gerenciar Aplicação
```bash
# Usar script de gerenciamento
./manage-containers.sh status
./manage-containers.sh logs prod
./manage-containers.sh restart all
./manage-containers.sh backup
```

## 🆘 Troubleshooting

### Deploy falha
1. Verificar logs do GitHub Actions: [https://github.com/jeronimo0007/auth-java-erp/actions](https://github.com/jeronimo0007/auth-java-erp/actions)
2. Verificar se todos os secrets estão configurados
3. Verificar conectividade SSH: `ssh -T git@github.com`

### Aplicação não conectar ao banco externo
```bash
# Verificar conectividade com banco externo
ping 195.200.1.59

# Testar conexão com banco externo
telnet 195.200.1.59 3306
```

### RabbitMQ não funcionar
```bash
# Verificar logs
docker logs rabbitmq-server

# Reiniciar
docker restart rabbitmq-server
```

### Container não inicia
```bash
# Ver logs detalhados
docker logs auth-prod

# Verificar recursos
docker stats

# Verificar espaço em disco
df -h
```

## 📊 Estrutura Final

Após a configuração, seu servidor terá:

```
/var/app/
├── logs/                      # Logs da aplicação
├── cache/                     # Cache local
├── rabbitmq-data/             # Dados do RabbitMQ
├── docker-compose.rabbitmq.yml  # Configuração RabbitMQ
├── setup-vps.sh              # Script de configuração
├── deploy-prod.sh            # Script deploy produção
├── deploy-dev.sh             # Script deploy desenvolvimento
└── manage-containers.sh      # Gerenciador de containers
```

## 🔐 Segurança

- ✅ Firewall configurado (UFW)
- ✅ Containers rodam como usuário não-root
- ✅ Chaves SSH para GitHub Actions
- ✅ Variáveis de ambiente para credenciais
- ✅ Conexão com banco de dados externo
- ✅ Blob storage externo (sem uploads locais)

## 🎉 Conclusão

Seu servidor Ubuntu 23 está agora configurado com:

- ✅ Docker e Docker Compose
- ✅ RabbitMQ com interface web
- ✅ Firewall configurado
- ✅ Deploy automático via GitHub Actions
- ✅ Scripts de gerenciamento
- ✅ Conexão com banco de dados externo
- ✅ Blob storage externo

**Próximos passos:**
1. Fazer deploy da aplicação
2. Configurar domínio (opcional)
3. Configurar SSL/HTTPS (opcional)
4. Configurar backup automático (opcional)

---

📖 **Guia de deploy:** [README_DEPLOY.md](README_DEPLOY.md)  
🚀 **Deploy rápido:** [DEPLOY_QUICK_START.md](DEPLOY_QUICK_START.md)

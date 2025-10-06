# 🚀 Guia de Configuração de Deploy Automático

Este guia te ajudará a configurar o deploy automático do seu projeto Java no VPS usando GitHub Actions.

## 📋 Visão Geral

O sistema está configurado para:
- **Merge para `main`** → Deploy em produção (porta 8080, application-prod)
- **Merge para `develop`** → Deploy em desenvolvimento (porta 8081, application-dev)

## 🛠️ Configuração do VPS

### 1. Conectar ao VPS
```bash
ssh seu-usuario@seu-vps-ip
```

### 2. Executar script de configuração
```bash
# Baixar e executar o script de configuração
curl -fsSL https://raw.githubusercontent.com/SEU_USUARIO/SEU_REPOSITORIO/main/scripts/setup-vps.sh | bash
```

### 3. Fazer logout e login novamente
```bash
exit
ssh seu-usuario@seu-vps-ip
```

### 4. Verificar instalação
```bash
docker --version
docker-compose --version
```

## 🔐 Configuração do GitHub

### 1. Gerar SSH Key para o VPS
```bash
# No seu VPS
ssh-keygen -t rsa -b 4096 -C "github-actions@seu-vps"
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
cat ~/.ssh/id_rsa  # Copie este conteúdo
```

### 2. Configurar GitHub Secrets

Vá para: `https://github.com/SEU_USUARIO/SEU_REPOSITORIO/settings/secrets/actions`

Adicione os seguintes secrets:

| Secret | Descrição | Exemplo |
|--------|-----------|---------|
| `VPS_HOST` | IP do seu VPS | `195.200.1.59` |
| `VPS_USERNAME` | Usuário do VPS | `root` ou `ubuntu` |
| `VPS_SSH_KEY` | Chave SSH privada | Conteúdo do `~/.ssh/id_rsa` |
| `VPS_PORT` | Porta SSH | `22` |
| `DB_URL` | URL do banco de dados | `jdbc:mysql://195.200.1.59:3306/app_omny?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | Usuário do banco | `api` |
| `DB_PASSWORD` | Senha do banco | `c6m8n4d2d3` |
| `JWT_SECRET` | Chave secreta JWT | `mySecretKey123456789012345678901234567890` |
| `JWT_EXPIRATION` | Expiração JWT (segundos) | `86400` |
| `STORAGE_S3_NAME_SPACE` | Nome do bucket S3 | `meu-bucket` |
| `STORAGE_S3_ACCESS_KEY` | Access Key do S3 | `AKIA...` |
| `STORAGE_S3_SECRET_KEY` | Secret Key do S3 | `wJalr...` |
| `RABBITMQ_HOST` | Host do RabbitMQ | `localhost` |
| `RABBITMQ_USERNAME` | Usuário do RabbitMQ | `guest` |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ | `guest` |

### 3. Atualizar workflows com seu repositório

Edite os arquivos:
- `.github/workflows/deploy-prod.yml`
- `.github/workflows/deploy-dev.yml`
- `docker-compose.prod.yml`
- `docker-compose.dev.yml`

Substitua `SEU_USUARIO/SEU_REPOSITORIO` pelo nome real do seu repositório.

## 🏗️ Estrutura do Projeto

```
projeto/
├── .github/workflows/
│   ├── deploy-prod.yml      # Deploy para produção
│   └── deploy-dev.yml       # Deploy para desenvolvimento
├── scripts/
│   ├── setup-vps.sh         # Configuração inicial do VPS
│   ├── deploy-prod.sh       # Script de deploy produção
│   └── deploy-dev.sh        # Script de deploy desenvolvimento
├── docker-compose.yml       # Para desenvolvimento local
├── docker-compose.prod.yml  # Para produção
├── docker-compose.dev.yml   # Para desenvolvimento
├── dockerfile               # Imagem Docker otimizada
└── env.example              # Exemplo de variáveis de ambiente
```

## 🚀 Como Usar

### Deploy Automático

1. **Para Produção:**
   ```bash
   git checkout main
   git merge develop
   git push origin main
   ```
   - A aplicação será deployada na porta 8080
   - Usará o profile `prod`
   - Acesse: `http://seu-vps-ip:8080`

2. **Para Desenvolvimento:**
   ```bash
   git checkout develop
   git merge feature/nova-funcionalidade
   git push origin develop
   ```
   - A aplicação será deployada na porta 8081
   - Usará o profile `dev`
   - Acesse: `http://seu-vps-ip:8081`

### Deploy Manual

Você também pode executar o deploy manualmente:

1. Vá para a aba "Actions" no GitHub
2. Selecione o workflow desejado
3. Clique em "Run workflow"

## 🔍 Monitoramento

### Verificar Status dos Containers
```bash
# No VPS
docker ps
docker logs auth-prod    # Logs de produção
docker logs auth-dev     # Logs de desenvolvimento
```

### Health Checks
```bash
# Produção
curl http://localhost:8080/actuator/health

# Desenvolvimento
curl http://localhost:8081/actuator/health
```

## 🛠️ Comandos Úteis

### Gerenciar Containers
```bash
# Parar containers
docker stop auth-prod auth-dev

# Remover containers
docker rm auth-prod auth-dev

# Ver logs
docker logs -f auth-prod

# Entrar no container
docker exec -it auth-prod bash
```

### Limpeza
```bash
# Remover imagens não utilizadas
docker image prune -f

# Remover volumes não utilizados
docker volume prune -f

# Limpeza completa
docker system prune -a
```

## 🔧 Troubleshooting

### Container não inicia
```bash
# Verificar logs
docker logs auth-prod

# Verificar variáveis de ambiente
docker exec auth-prod env
```

### Problemas de conectividade
```bash
# Verificar portas
netstat -tlnp | grep :8080
netstat -tlnp | grep :8081

# Verificar firewall
sudo ufw status
```

### Problemas de permissão
```bash
# Corrigir permissões dos uploads
sudo chown -R 1000:1000 /var/app/uploads
sudo chown -R 1000:1000 /var/app/uploads-dev
```

## 📊 Monitoramento de Recursos

### Verificar uso de recursos
```bash
# Uso de CPU e memória
docker stats

# Espaço em disco
df -h

# Logs do sistema
journalctl -u docker.service
```

## 🔄 Rollback

Se algo der errado, você pode fazer rollback:

```bash
# Parar container atual
docker stop auth-prod

# Executar versão anterior
docker run -d --name auth-prod --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  ghcr.io/SEU_USUARIO/SEU_REPOSITORIO:commit-anterior
```

## 📝 Notas Importantes

1. **Segurança**: Nunca commite senhas ou chaves no código
2. **Backup**: Configure backup regular do banco de dados
3. **Logs**: Monitore os logs regularmente
4. **Updates**: Mantenha o VPS e Docker atualizados
5. **SSL**: Configure HTTPS em produção usando nginx ou similar

## 🆘 Suporte

Se encontrar problemas:

1. Verifique os logs do GitHub Actions
2. Verifique os logs dos containers no VPS
3. Confirme se todas as variáveis de ambiente estão configuradas
4. Verifique se o VPS tem recursos suficientes

---

**🎉 Parabéns! Seu sistema de deploy automático está configurado!**

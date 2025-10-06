asjus# 🚀 Deploy Rápido - auth-java-erp

**Repositório:** [jeronimo0007/auth-java-erp](https://github.com/jeronimo0007/auth-java-erp)

## ⚡ Configuração em 5 Passos

### 1️⃣ **Configurar VPS**
```bash
ssh seu-usuario@seu-vps-ip
curl -fsSL https://raw.githubusercontent.com/jeronimo0007/auth-java-erp/main/scripts/setup-vps.sh | bash
exit
ssh seu-usuario@seu-vps-ip
```

### 2️⃣ **Gerar Chave SSH**
```bash
# No VPS
ssh-keygen -t rsa -b 4096 -C "github-actions@seu-vps"
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
cat ~/.ssh/id_rsa  # Copie este conteúdo
```

### 3️⃣ **Configurar GitHub Secrets**
Vá para: [https://github.com/jeronimo0007/auth-java-erp/settings/secrets/actions](https://github.com/jeronimo0007/auth-java-erp/settings/secrets/actions)

Adicione estes secrets:

| Secret | Valor |
|--------|-------|
| `VPS_HOST` | `195.200.1.59` |
| `VPS_USERNAME` | `root` ou `ubuntu` |
| `VPS_SSH_KEY` | Conteúdo do `~/.ssh/id_rsa` |
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

### 4️⃣ **Fazer Deploy**
```bash
git add .
git commit -m "feat: configuração de deploy automático"
git push origin main
```

### 5️⃣ **Verificar Deploy**
- Vá para: [https://github.com/jeronimo0007/auth-java-erp/actions](https://github.com/jeronimo0007/auth-java-erp/actions)
- Aguarde o workflow "Deploy to Production" concluir
- Teste: `http://SEU-VPS-IP:8080/actuator/health`

## 🎯 URLs de Acesso

- **Produção**: `http://SEU-VPS-IP:8080`
- **Desenvolvimento**: `http://SEU-VPS-IP:8081`

## 🔄 Deploy Futuro

### Produção
```bash
git checkout main
git merge develop
git push origin main
```

### Desenvolvimento
```bash
git checkout develop
git merge feature/nova-funcionalidade
git push origin develop
```

## 🛠️ Comandos Úteis

```bash
# Ver containers
docker ps

# Ver logs
docker logs auth-prod
docker logs auth-dev

# Reiniciar
docker restart auth-prod
```

## 🆘 Problemas?

1. **Deploy falha**: Verifique [GitHub Actions](https://github.com/jeronimo0007/auth-java-erp/actions)
2. **Container não inicia**: `docker logs auth-prod`
3. **Aplicação não responde**: `docker ps` e `curl http://localhost:8080/actuator/health`

---

📖 **Guia completo:** [README_DEPLOY.md](README_DEPLOY.md)

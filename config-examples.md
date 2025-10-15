# Exemplos de Configuração de Porta

## 1. Porta Padrão (8080)
```bash
./mvnw spring-boot:run
```
Acesse: `http://localhost:8080`

## 2. Porta 3000
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=3000
```
Acesse: `http://localhost:3000`

## 3. Porta 9000
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9000
```
Acesse: `http://localhost:9000`

## 4. Usando Scripts

### Windows
```bash
# Porta padrão
start.bat

# Porta personalizada
start.bat 3000
start.bat 9000
```

### Linux/Mac
```bash
# Porta padrão
./start.sh

# Porta personalizada
./start.sh 3000
./start.sh 9000
```

## 5. Usando Variáveis de Ambiente

### Windows
```cmd
set SERVER_PORT=3000
./mvnw spring-boot:run
```

### Linux/Mac
```bash
export SERVER_PORT=3000
./mvnw spring-boot:run
```

## 6. Usando Perfis

### Desenvolvimento (porta 8080)
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Produção (porta 8080)
```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## 7. Testando os Endpoints

### Porta 8080 (padrão)
```bash
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"teste@teste.com","password":"123456"}'
```

### Porta 3000
```bash
curl -X POST http://localhost:3000/auth/login -H "Content-Type: application/json" -d '{"email":"teste@teste.com","password":"123456"}'
```

### Porta 9000
```bash
curl -X POST http://localhost:9000/auth/login -H "Content-Type: application/json" -d '{"email":"teste@teste.com","password":"123456"}'
```

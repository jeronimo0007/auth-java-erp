# Sistema de Autenticação Spring Boot

Este projeto implementa um sistema de autenticação usando Spring Boot com MySQL, compatível com o sistema de hash de senhas do PHP.

**Repositório:** [jeronimo0007/auth-java-erp](https://github.com/jeronimo0007/auth-java-erp)

## 🚀 Deploy Automático

Este projeto está configurado com deploy automático via GitHub Actions para VPS:

- **Merge para `main`** → Deploy automático em produção (porta 8080)
- **Merge para `develop`** → Deploy automático em desenvolvimento (porta 8081)

📖 **Guia completo de deploy:** [README_DEPLOY.md](README_DEPLOY.md)

## Configuração do Banco de Dados

### Pré-requisitos
- MySQL instalado e rodando
- Banco de dados `dev` criado
- Usuário `root` com senha vazia

### Configuração
O banco de dados está configurado no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dev?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
```

## Estrutura do Banco de Dados

### Tabela `tblclients`
- `userid` (PK) - ID do usuário
- `company` - Nome da empresa
- `phonenumber` - Telefone
- `zip` - CEP
- `city` - Cidade
- `state` - Estado
- `address` - Endereço
- `default` - Padrão (1 ou 0)
- `active` - Ativo (1 ou 0)
- `datecreated` - Data de criação

### Tabela `tblcontacts`
- `id` (PK) - ID do contato
- `userid` (FK) - ID do usuário
- `is_primary` - Contato primário (0 ou 1)
- `firstname` - Nome
- `lastname` - Sobrenome
- `email` - Email (único)
- `phonenumber` - Telefone
- `password` - Senha (hash)
- `datecreated` - Data de criação

### Tabela `tblstaff`
- `staffid` (PK) - ID do staff
- `email` - Email (único)
- `firstname` - Nome
- `lastname` - Sobrenome
- `password` - Senha (hash)
- `admin` - Administrador (0 ou 1)
- `active` - Ativo (0 ou 1)
- `role` - ID do papel/função
- `warehouse` - JSON com IDs dos warehouses permitidos
- `datecreated` - Data de criação

### Tabela `tblwarehouse`
- `warehouse_id` (PK) - ID do warehouse
- `warehouse_code` - Código do warehouse
- `warehouse_name` - Nome do warehouse
- `cnpj` - CNPJ
- `type` - Tipo (filial, franquia, distribuidor, importador, ecommerce)
- `perfex_saas_tenant_id` - ID do tenant

## Endpoints da API

### 1. Registro de Usuário (API)
**POST** `/api/auth/register`

```json
{
  "company": "Nome da Empresa",
  "email": "usuario@email.com",
  "password": "senha123",
  "firstName": "Nome",
  "lastName": "Sobrenome",
  "phoneNumber": "11999999999",
  "zip": "01234567",
  "city": "São Paulo",
  "state": "SP",
  "address": "Rua Exemplo, 123"
}
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Usuário registrado com sucesso",
  "data": {
    "userId": 1,
    "company": "Nome da Empresa",
    "phoneNumber": "11999999999",
    "zip": "01234567",
    "city": "São Paulo",
    "state": "SP",
    "address": "Rua Exemplo, 123",
    "dateCreated": "2024-01-01T10:00:00"
  }
}
```

### 2. Login (API)
**POST** `/api/auth/login`

```json
{
  "email": "usuario@email.com",
  "password": "senha123"
}
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "id": 1,
    "userId": 1,
    "isPrimary": true,
    "firstName": "Nome",
    "lastName": "Sobrenome",
    "email": "usuario@email.com",
    "phoneNumber": "11999999999",
    "dateCreated": "2024-01-01T10:00:00"
  }
}
```

### 3. Recuperação de Senha (API)
**POST** `/api/auth/lost_password`

```json
{
  "email": "usuario@email.com"
}
```

**Resposta:**
```json
{
  "success": true,
  "message": "Se o email estiver cadastrado, você receberá instruções para redefinir sua senha"
}
```

### 4. Registro via Site
**POST** `/register/site`

```json
{
  "company": "Nome da Empresa",
  "email": "usuario@email.com",
  "password": "senha123",
  "firstName": "Nome",
  "lastName": "Sobrenome",
  "phoneNumber": "11999999999",
  "zip": "01234567",
  "city": "São Paulo",
  "state": "SP",
  "address": "Rua Exemplo, 123"
}
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Usuário registrado com sucesso via site",
  "data": {
    "userId": 1,
    "company": "Nome da Empresa",
    "phoneNumber": "11999999999",
    "zip": "01234567",
    "city": "São Paulo",
    "state": "SP",
    "address": "Rua Exemplo, 123",
    "dateCreated": "2024-01-01T10:00:00"
  }
}
```

### 5. Login de Administrador/Staff
**POST** `/api/auth/admin/login`

```json
{
  "email": "admin@empresa.com",
  "password": "senha123",
  "warehouseId": 1
}
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Logado com sucesso",
  "user": {
    "staffId": 1,
    "email": "admin@empresa.com",
    "firstName": "Admin",
    "lastName": "Sistema",
    "phoneNumber": "11999999999",
    "admin": 1,
    "active": 1,
    "role": 1,
    "type": "EMPLOYEE"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "warehouse": {
    "warehouseId": 1,
    "warehouseCode": "WH001",
    "warehouseName": "Matriz",
    "cnpj": "12345678000199",
    "type": "FILIAL"
  }
}
```

**Resposta de Erro:**
```json
{
  "success": false,
  "message": "Login Inválido"
}
```

**Possíveis Mensagens de Erro:**
- `"Usuário inexistente"` - Email não encontrado
- `"Warehouse não encontrado"` - ID do warehouse inválido
- `"Acesso negado a loja selecionada."` - Usuário não tem permissão para o warehouse
- `"Login Inválido"` - Senha incorreta
- `"Usuário inativo, contate o administrador"` - Usuário desativado

### 6. Buscar Informações do Cliente
**GET** `/auth/client/{id}`

**Parâmetros:**
- `id` (Long) - ID do cliente

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Informações do cliente encontradas",
  "data": {
    "company": "Nome da Empresa",
    "email": "cliente@email.com",
    "phoneNumber": "11999999999"
  }
}
```

**Resposta de Erro:**
```json
{
  "success": false,
  "message": "Cliente não encontrado"
}
```

**Descrição:**
Este endpoint retorna apenas as informações básicas de um cliente específico: nome da empresa, email do contato primário e telefone. Útil para consultas rápidas sem expor dados sensíveis.

### 7. Registro de Site

**Endpoint:** `POST /register/site`

**Descrição:** Registra um novo site, criando automaticamente um cliente, contato, projeto e task com todas as informações necessárias para desenvolvimento.

**Parâmetros (multipart/form-data):**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `email` | String | Sim | Email do cliente |
| `phonenumber` | String | Sim | Telefone do cliente |
| `company` | String | Sim | Nome da empresa |
| `nome_site` | String | Sim | Nome do site |
| `dominio` | String | Sim | Domínio do site |
| `descricao_negocio` | String | Sim | Descrição do negócio |
| `publico_alvo` | String | Sim | Público alvo |
| `banner_texto` | String | Sim | Texto do banner principal |
| `banner_secundario` | String | Não | Texto do banner secundário |
| `banner_terciario` | String | Não | Texto do banner terciário |
| `tipo_site` | String | Sim | Tipo do site (site, curriculo, cartao de visita) |
| `quem_somos` | String | Sim | Seção "Quem Somos" |
| `empresa_imagem` | File | Não | Imagem da empresa |
| `servicos` | String | Sim | Descrição dos serviços |
| `servicos_imagens[]` | File[] | Não | Imagens dos serviços (múltiplas) |
| `contato_info` | String | Sim | Informações de contato |
| `email_empresa` | String | Sim | Email da empresa |
| `telefone_empresa` | String | Sim | Telefone da empresa |
| `endereco_empresa` | String | Sim | Endereço da empresa |
| `secao1_titulo` | String | Sim | Título da seção 1 |
| `secao1_conteudo` | String | Sim | Conteúdo da seção 1 |
| `secao2_titulo` | String | Sim | Título da seção 2 |
| `secao2_conteudo` | String | Sim | Conteúdo da seção 2 |
| `logo` | File | Não | Logo da empresa |
| `cor_principal` | String | Sim | Cor principal do site |
| `cor_secundaria` | String | Sim | Cor secundária do site |
| `estilo` | String | Sim | Estilo do site |
| `observacoes` | String | Não | Observações adicionais |
| `firstname` | String | Sim | Primeiro nome do cliente |
| `lastname` | String | Sim | Último nome do cliente |
| `facebook` | String | Não | URL do Facebook |
| `linkedin` | String | Não | URL do LinkedIn |
| `youtube` | String | Não | URL do YouTube |
| `tiktok` | String | Não | URL do TikTok |
| `instagram` | String | Não | URL do Instagram |

**Exemplo de Requisição (cURL):**
```bash
curl -X POST http://localhost:8080/register/site \
  -F "email=jeronimo.alvves@teste.com" \
  -F "phonenumber=(11) 98010-2250" \
  -F "company=empresa" \
  -F "nome_site=site" \
  -F "dominio=www.site.com" \
  -F "descricao_negocio=descricao negocio" \
  -F "publico_alvo=publico alvo" \
  -F "banner_texto=conteudo banner 1" \
  -F "banner_secundario=conteudo banner 2" \
  -F "banner_terciario=conteudo banner 3" \
  -F "tipo_site=site" \
  -F "quem_somos=Empresa XPTO" \
  -F "empresa_imagem=@logomarca_royal.png" \
  -F "servicos=Serviços" \
  -F "servicos_imagens[]=@2.jpg" \
  -F "servicos_imagens[]=@3.jpg" \
  -F "contato_info=contatos xpto" \
  -F "email_empresa=a@a.com" \
  -F "telefone_empresa=(11) 98010-2250" \
  -F "endereco_empresa=Rua mario rossi, 66" \
  -F "secao1_titulo=Frutas" \
  -F "secao1_conteudo=teste" \
  -F "secao2_titulo=Legumes" \
  -F "secao2_conteudo=Sessao legumes" \
  -F "logo=@logomarca_royal.png" \
  -F "cor_principal=#9a03e9" \
  -F "cor_secundaria=#000000" \
  -F "estilo=moderno" \
  -F "observacoes=Deixa igual xptop" \
  -F "firstname=Jeronimo" \
  -F "lastname=cardoso" \
  -F "facebook=https://facebook.com/empresa" \
  -F "linkedin=https://linkedin.com/company/empresa" \
  -F "youtube=https://youtube.com/empresa" \
  -F "tiktok=https://tiktok.com/@empresa" \
  -F "instagram=https://instagram.com/empresa"
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "Site registrado com sucesso! Cliente, projeto e 3 tasks foram criadas (Desenvolvimento, Configurações e Fatura).",
  "data": {
    "userId": 1,
    "company": "empresa",
    "phoneNumber": "(11) 98010-2250",
    "zip": null,
    "city": null,
    "state": null,
    "address": null,
    "dateCreated": "2024-01-15T10:30:00"
  }
}
```

**Resposta de Erro:**
```json
{
  "success": false,
  "message": "Erro ao registrar site: Email já está em uso"
}
```

**Tipos de Site Disponíveis:**

| Tipo | Descrição | Características |
|------|-----------|-----------------|
| `site` | Site institucional completo | - Todas as seções solicitadas<br/>- Foco em apresentação da empresa<br/>- Serviços e produtos<br/>- Design corporativo |
| `curriculo` | Site de currículo profissional | - Apresentação pessoal<br/>- Experiência profissional<br/>- Habilidades e formação<br/>- Design limpo e profissional |
| `cartao de visita` | Site tipo cartão de visita digital | - Design minimalista<br/>- Informações de contato<br/>- Página única<br/>- Navegação suave |

**Funcionalidades do Endpoint:**
1. **Criação de Cliente:** Registra um novo cliente na tabela `tblclients`
2. **Criação de Contato:** Cria um contato primário na tabela `tblcontacts` com senha padrão "123456"
3. **Criação de Site:** Registra todos os dados do site na tabela `tblsites` para facilitar ajustes futuros
4. **Upload de Arquivos:** Faz upload das imagens fornecidas para a pasta `uploads/`
5. **Criação de Projeto:** Cria um projeto na tabela `tblprojects` com descrição detalhada
6. **Criação de 3 Tasks:**
   - **Task 1 - Desenvolvimento:** Prompt completo para criação do site
   - **Task 2 - Configurações:** 4 itens (domínio, HTTPS, email, contato)
   - **Task 3 - Fatura:** Geração de fatura após validação do cliente

**Estrutura de Arquivos Criada:**

O sistema cria automaticamente:
- **Pasta:** `{userId}` (ID do cliente retornado)
- **Arquivo:** `index.php` dentro da pasta
- **Conteúdo:** Site completo em HTML, CSS e PHP

**Descrição das Tasks Criadas:**

**Task 1 - Desenvolvimento do Site:**
- Prompt detalhado e estruturado para o desenvolvedor
- Instruções para criar pasta com ID do cliente
- Criação do arquivo index.php com todo o site
- Informações básicas do cliente e site
- Conteúdo completo de todas as seções
- Especificações de design (cores, estilo)
- Lista de arquivos fornecidos
- Requisitos técnicos (responsivo, SEO, performance)
- Instruções passo a passo para o desenvolvedor

**Task 2 - Configurações do Site (4 itens):**
1. **Criar Domínio:** Configuração DNS e registro
2. **Gerar HTTPS:** Instalação de certificado SSL
3. **Gerar Email:** Configuração de contas corporativas
4. **Entrar em Contato:** Validação e credenciais com cliente

**Task 3 - Geração de Fatura:**
- Executada após validação final do cliente
- Lista completa de serviços incluídos
- Instruções para geração e envio da fatura
- Acompanhamento de pagamento

**Tabela `tblsites`:**

A tabela `tblsites` armazena todos os dados do site para facilitar ajustes futuros:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `site_id` | INT | ID único do site |
| `client_id` | INT | ID do cliente (FK para tblclients) |
| `nome_site` | VARCHAR(255) | Nome do site |
| `dominio` | VARCHAR(255) | Domínio do site |
| `tipo_site` | VARCHAR(50) | Tipo do site (site, curriculo, cartao de visita) |
| `descricao_negocio` | TEXT | Descrição do negócio |
| `publico_alvo` | TEXT | Público alvo |
| `banner_texto` | TEXT | Texto do banner principal |
| `banner_secundario` | TEXT | Texto do banner secundário |
| `banner_terciario` | TEXT | Texto do banner terciário |
| `quem_somos` | TEXT | Seção "Quem Somos" |
| `empresa_imagem` | VARCHAR(500) | Caminho da imagem da empresa |
| `servicos` | TEXT | Descrição dos serviços |
| `servicos_imagens` | TEXT | Caminhos das imagens dos serviços |
| `contato_info` | TEXT | Informações de contato |
| `email_empresa` | VARCHAR(255) | Email da empresa |
| `telefone_empresa` | VARCHAR(50) | Telefone da empresa |
| `endereco_empresa` | TEXT | Endereço da empresa |
| `secao1_titulo` | VARCHAR(255) | Título da seção 1 |
| `secao1_conteudo` | TEXT | Conteúdo da seção 1 |
| `secao2_titulo` | VARCHAR(255) | Título da seção 2 |
| `secao2_conteudo` | TEXT | Conteúdo da seção 2 |
| `logo` | VARCHAR(500) | Caminho do logo |
| `cor_principal` | VARCHAR(20) | Cor principal do site |
| `cor_secundaria` | VARCHAR(20) | Cor secundária do site |
| `estilo` | VARCHAR(100) | Estilo do site |
| `observacoes` | TEXT | Observações adicionais |
| `facebook` | VARCHAR(255) | URL do Facebook |
| `linkedin` | VARCHAR(255) | URL do LinkedIn |
| `youtube` | VARCHAR(255) | URL do YouTube |
| `tiktok` | VARCHAR(255) | URL do TikTok |
| `instagram` | VARCHAR(255) | URL do Instagram |
| `status` | INT | Status do site (0=Pendente, 1=Em desenvolvimento, 2=Concluído, 3=Cancelado) |
| `data_criacao` | DATETIME | Data de criação |
| `data_atualizacao` | DATETIME | Data de última atualização |

## Como Executar

### Opção 1: Usando Maven (Porta Padrão)
```bash
./mvnw spring-boot:run
```

### Opção 2: Usando Scripts (Porta Personalizada)

**Windows:**
```bash
# Porta padrão (8080)
start.bat

# Porta personalizada
start.bat 3000
```

**Linux/Mac:**
```bash
# Porta padrão (8080)
./start.sh

# Porta personalizada
./start.sh 3000
```

### Opção 3: Usando Maven com Porta Personalizada
```bash
# Porta 3000
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=3000

# Porta 9000
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9000
```

### Opção 4: Usando Perfis de Ambiente
```bash
# Ambiente de desenvolvimento
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Ambiente de produção
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### Configuração de Porta
A porta padrão é **8080**, mas pode ser alterada de várias formas:

1. **Via parâmetro de linha de comando:**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=3000
   ```

2. **Via variável de ambiente:**
   ```bash
   export SERVER_PORT=3000
   ./mvnw spring-boot:run
   ```

3. **Via arquivo de configuração:**
   - Edite `src/main/resources/application.properties`
   - Altere `server.port=8080` para a porta desejada

### Acesso
Após iniciar, acesse: `http://localhost:PORTA_ESCOLHIDA`

## Compatibilidade com PHP

O sistema de hash de senhas é compatível com a classe `PasswordHash` do PHP fornecida. As senhas são criptografadas usando o mesmo algoritmo, permitindo que usuários cadastrados no sistema PHP possam fazer login no sistema Java e vice-versa.

## Tecnologias Utilizadas

- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- Spring Web (multipart file upload)
- MySQL Connector
- Bean Validation
- JWT (JSON Web Tokens)
- Lombok (para redução de boilerplate)
- Java 17

## Funcionalidades

### Autenticação de Clientes
- Registro de novos usuários
- Login com email e senha
- Recuperação de senha
- Compatibilidade com sistema PHP existente

### Autenticação de Administradores/Staff
- Login de administradores e funcionários
- Controle de acesso por warehouse
- Geração de tokens JWT
- Verificação de permissões por loja
- Suporte a diferentes tipos de usuário (admin, funcionário, representante, etc.)

### Gestão de Projetos e Sites
- Registro completo de sites com upload de arquivos
- Criação automática de clientes, contatos, projetos e tasks
- Geração de prompts detalhados para desenvolvimento
- Upload e gerenciamento de imagens (logo, empresa, serviços)
- Estruturação automática de informações para desenvolvimento

### Segurança
- Hash de senhas compatível com PHP
- Tokens JWT para autenticação
- Validação de entrada
- Controle de acesso baseado em roles


# Docker - Configuração MySQL para SGBU

## Pré-requisitos

- Docker instalado (https://www.docker.com/products/docker-desktop)
- Docker Compose (geralmente vem com Docker Desktop)

## Iniciar o MySQL com Docker

### 1. Iniciar o container

```bash
docker-compose up -d
```

O flag `-d` executa em background. Remova para ver os logs em tempo real.

### 2. Verificar se está rodando

```bash
docker-compose ps
```

### 3. Parar o container

```bash
docker-compose down
```

### 4. Ver logs

```bash
docker-compose logs -f mysql
```

## Informações de Conexão

**Host:** localhost  
**Porta:** 3306  
**Usuário:** sgbu_user  
**Senha:** sgbu_password  
**Banco de dados:** sgbu_db  

**Usuário Root:**  
**Usuário:** root  
**Senha:** root  

## Conectar ao MySQL diretamente

### Via MySQL CLI

```bash
mysql -h localhost -u sgbu_user -p sgbu_db
# Senha: sgbu_password
```

### Via Docker

```bash
docker exec -it sgbu-mysql mysql -u sgbu_user -p sgbu_db
# Senha: sgbu_password
```

## Dados Iniciais

O script `init.sql` já cria:

- Tabelas necessárias (usuario, obra, exemplar, emprestimo, multa, reserva, relatorio)
- Usuário administrador padrão
  - Email: admin@sgbu.local
  - Senha: admin123
- 3 obras de exemplo para testes

## Atualizar DatabaseConnection.java

A aplicação JavaFX deve usar estas credenciais no arquivo `DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/sgbu_db?useSSL=false&serverTimezone=UTC";
private static final String USER = "sgbu_user";
private static final String PASSWORD = "sgbu_password";
```

## Troubleshooting

### Porta 3306 já em uso

```bash
# Verificar qual processo usa a porta
lsof -i :3306

# Ou mudar a porta no docker-compose.yml
# Alterar "3306:3306" para "3307:3306"
```

### Reconectar após reiniciar

O volume `mysql_data` persiste os dados. Para limpar tudo:

```bash
docker-compose down -v
docker-compose up -d
```

## Executar a Aplicação

Após garantir que o MySQL está rodando:

```bash
mvn javafx:run
```

Login com o usuário admin criado automaticamente.

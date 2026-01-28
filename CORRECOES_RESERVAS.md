# Correções Implementadas no Sistema de Reservas

## Data: 28 de janeiro de 2026

## Problema Original
O sistema estava gerando erro ao tentar criar reservas:
```
Erro ao inserir reserva: Duplicate entry '7-1' for key 'reservas.uk_reserva'
```

## Alterações Implementadas

### 1. Banco de Dados
- **Removida** a constraint única `uk_reserva (usuario_id, obra_id)` que impedia renovações
- **Adicionados** índices para melhorar performance:
  - `idx_usuario (usuario_id)`
  - `idx_obra_status (obra_id, status)` - índice composto

### 2. Modelo de Dados (ReservaDAO.java)
Adicionados novos métodos:

- `existeReservaAtivaDaObra(int obraId)` - Verifica se há alguma reserva ativa para a obra
- `existeReservaAtivaDeOutroUsuario(int obraId, int usuarioId)` - Verifica se outro usuário tem reserva ativa
- `calcularProximaPosicaoFila(int obraId)` - Calcula automaticamente a próxima posição na fila

### 3. Controller (ReservasController.java)
**Formulário de Nova Reserva:**
- ✅ Removido campo de **Status** - agora sempre ATIVA por default
- ✅ Removido campo editável de **Posição na Fila** - agora é calculada automaticamente
- ✅ Adicionada validação de renovação:
  - Se o usuário já tem reserva ativa para a obra E existe reserva de outro usuário → **BLOQUEIA**
  - Se o usuário já tem reserva ativa para a obra E NÃO existe reserva de outro usuário → **PERMITE (renovação)**
  - Se é uma reserva nova → **PERMITE**

**Tabela de Reservas:**
- ✅ Adicionada coluna **"Posição na Fila"** para visualização

### 4. Interface (reservas.fxml)
- ✅ Adicionada coluna `colPosicaoFila` na TableView

## Regras de Negócio Implementadas

### Criação de Reserva:
1. Status é sempre **ATIVA** por default
2. Posição na fila é calculada automaticamente (última posição + 1)
3. Não exibe campos de status ou posição no formulário

### Renovação (mesma obra, mesmo usuário):
✅ **PERMITIDA** apenas quando:
- NÃO existir outra reserva ATIVA de outro usuário para a mesma obra

❌ **BLOQUEADA** quando:
- Existir reserva ATIVA de outro usuário para a mesma obra
- Mensagem: "Não é possível renovar a reserva porque existem outras reservas ativas para esta obra."

## Arquivos Modificados

1. `/src/main/java/ao/co/imetro/sgbu/database/DatabaseInitializer.java`
2. `/src/main/java/ao/co/imetro/sgbu/model/dao/ReservaDAO.java`
3. `/src/main/java/ao/co/imetro/sgbu/controller/ReservasController.java`
4. `/src/main/resources/fxml/reservas.fxml`
5. `migration_fix_reservas.sql` (script de migração executado)

## Como Testar

1. **Reserva Normal:**
   - Selecionar usuário e obra
   - Sistema calcula automaticamente a posição na fila
   - Status é ATIVA por default

2. **Renovação Permitida:**
   - Usuário com reserva ativa tenta fazer nova reserva da mesma obra
   - Não há outras reservas ativas de outros usuários
   - Sistema permite e adiciona na última posição da fila

3. **Renovação Bloqueada:**
   - Usuário com reserva ativa tenta fazer nova reserva da mesma obra
   - Existe pelo menos uma reserva ativa de outro usuário
   - Sistema exibe mensagem de erro e bloqueia

## Compilação
```bash
mvn clean compile
```
✅ BUILD SUCCESS

## Migration Executada
```bash
docker compose exec -T mysql mysql -u sgbu_user -psgbu_password sgbu_db < migration_fix_reservas.sql
```
✅ Executada com sucesso

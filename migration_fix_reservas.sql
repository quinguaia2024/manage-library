-- Migration para corrigir a tabela de reservas
-- Remove a constraint única para permitir renovações
-- Adiciona índices para melhorar performance

USE sgbu_db;

-- Primeiro, vamos ver a estrutura atual
-- SHOW CREATE TABLE reservas;

-- Passo 1: Remover as foreign keys
ALTER TABLE reservas DROP FOREIGN KEY reservas_ibfk_1;
ALTER TABLE reservas DROP FOREIGN KEY reservas_ibfk_2;

-- Passo 2: Remover o índice único uk_reserva
ALTER TABLE reservas DROP INDEX uk_reserva;

-- Passo 3: Adicionar índices novos
ALTER TABLE reservas ADD INDEX idx_usuario (usuario_id);
ALTER TABLE reservas ADD INDEX idx_obra_status (obra_id, status);

-- Passo 4: Recriar as foreign keys
ALTER TABLE reservas ADD CONSTRAINT reservas_ibfk_1 FOREIGN KEY (usuario_id) REFERENCES usuarios(id);
ALTER TABLE reservas ADD CONSTRAINT reservas_ibfk_2 FOREIGN KEY (obra_id) REFERENCES obras(id);

-- Verificar a estrutura atualizada
SHOW CREATE TABLE reservas;

-- Mostrar os índices da tabela
SHOW INDEX FROM reservas;

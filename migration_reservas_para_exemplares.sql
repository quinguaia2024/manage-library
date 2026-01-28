-- Migration para mudar reservas de obras para exemplares
-- E implementar cria\u00e7\u00e3o autom\u00e1tica de empr\u00e9stimos ao confirmar reserva

USE sgbu_db;

-- Passo 1: Fazer backup das reservas existentes
CREATE TABLE IF NOT EXISTS reservas_backup_temp AS SELECT * FROM reservas;

-- Passo 2: Remover foreign keys e \u00edndices antigos
ALTER TABLE reservas DROP FOREIGN KEY reservas_ibfk_2;
ALTER TABLE reservas DROP INDEX idx_obra;
ALTER TABLE reservas DROP INDEX idx_obra_status;

-- Passo 3: Renomear coluna obra_id para exemplar_id
ALTER TABLE reservas CHANGE COLUMN obra_id exemplar_id INT NOT NULL;

-- Passo 4: Adicionar nova foreign key para exemplares
ALTER TABLE reservas ADD CONSTRAINT reservas_ibfk_2 FOREIGN KEY (exemplar_id) REFERENCES exemplares(id);

-- Passo 5: Adicionar novos \u00edndices
ALTER TABLE reservas ADD INDEX idx_exemplar (exemplar_id);
ALTER TABLE reservas ADD INDEX idx_exemplar_status (exemplar_id, status);

-- Passo 6: Limpar reservas antigas (opcional - remover se quiser manter)
TRUNCATE TABLE reservas;

-- Verificar estrutura atualizada
SHOW CREATE TABLE reservas;

-- Mostrar \u00edndices
SHOW INDEX FROM reservas;

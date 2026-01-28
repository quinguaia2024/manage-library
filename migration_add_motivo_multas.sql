-- Adicionar coluna motivo na tabela multas
-- Executar este script para atualizar o banco de dados

-- Verificar e adicionar coluna motivo
-- Se a coluna já existir, este comando gerará um erro que pode ser ignorado
ALTER TABLE multas ADD COLUMN motivo VARCHAR(20) NOT NULL DEFAULT 'ATRASO';

-- Atualizar multas existentes para ter motivo ATRASO como padrão
UPDATE multas SET motivo = 'ATRASO' WHERE motivo IS NULL OR motivo = '';

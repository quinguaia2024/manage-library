-- Criar tabelas para SGBU - Sistema de Gestão de Biblioteca Universitária

-- Tabela de Usuários
-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS sgbu_db;
USE sgbu_db;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMINISTRADOR', 'BIBLIOTECARIO', 'DOCENTE', 'ESTUDANTE') NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Obras (Livros)
CREATE TABLE IF NOT EXISTS obra (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    editora VARCHAR(100),
    ano_publicacao INT,
    num_paginas INT,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Exemplares (Cópias dos Livros)
CREATE TABLE IF NOT EXISTS exemplar (
    id INT PRIMARY KEY AUTO_INCREMENT,
    obra_id INT NOT NULL,
    codigo_barras VARCHAR(50) UNIQUE,
    status ENUM('DISPONIVEL', 'EMPRESTADO', 'DANIFICADO', 'DESCARTADO') DEFAULT 'DISPONIVEL',
    data_aquisicao DATE,
    FOREIGN KEY (obra_id) REFERENCES obra(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Empréstimos
CREATE TABLE IF NOT EXISTS emprestimo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    exemplar_id INT NOT NULL,
    data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_devolucao_prevista DATE NOT NULL,
    data_devolucao_efetiva DATE,
    renovacoes INT DEFAULT 0,
    status ENUM('ATIVO', 'DEVOLVIDO', 'ATRASADO') DEFAULT 'ATIVO',
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (exemplar_id) REFERENCES exemplar(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Multas
CREATE TABLE IF NOT EXISTS multa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    emprestimo_id INT,
    valor DECIMAL(10, 2) NOT NULL,
    dias_atraso INT,
    status ENUM('ABERTA', 'PAGA') DEFAULT 'ABERTA',
    data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_pagamento DATE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (emprestimo_id) REFERENCES emprestimo(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Reservas
CREATE TABLE IF NOT EXISTS reserva (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    obra_id INT NOT NULL,
    data_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ATIVA', 'ATENDIDA', 'CANCELADA') DEFAULT 'ATIVA',
    data_cancelamento DATE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (obra_id) REFERENCES obra(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Relatorios
CREATE TABLE IF NOT EXISTS relatorio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(50) NOT NULL,
    data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id INT,
    descricao TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inserir usuário admin padrão (senha: admin123)
INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES 
('Administrador', 'admin@sgbu.local', '$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Aw74qdR/Jsqv.3zK', 'ADMINISTRADOR', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- Inserir algumas obras de exemplo
INSERT INTO obra (titulo, autor, isbn, editora, ano_publicacao, num_paginas) VALUES 
('Clean Code', 'Robert C. Martin', '9780132350884', 'Prentice Hall', 2008, 464),
('Design Patterns', 'Gang of Four', '9780201633610', 'Addison-Wesley', 1994, 395),
('Refactoring', 'Martin Fowler', '9780201485677', 'Addison-Wesley', 1999, 464)
ON DUPLICATE KEY UPDATE id=id;

-- Criar índices para melhor performance
CREATE INDEX idx_obra_titulo ON obra(titulo);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_emprestimo_usuario ON emprestimo(usuario_id);
CREATE INDEX idx_emprestimo_exemplar ON emprestimo(exemplar_id);
CREATE INDEX idx_exemplar_obra ON exemplar(obra_id);
CREATE INDEX idx_multa_usuario ON multa(usuario_id);
CREATE INDEX idx_reserva_usuario ON reserva(usuario_id);

# SGBU - Sistema de Gestão de Biblioteca Universitária
## Documentação Completa - v1.0.0

---

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Entidades e Banco de Dados](#entidades-e-banco-de-dados)
4. [Serviços e Lógica de Negócio](#serviços-e-lógica-de-negócio)
5. [DAOs e Acesso a Dados](#daos-e-acesso-a-dados)
6. [Funcionalidades Implementadas](#funcionalidades-implementadas)
7. [Instalação e Configuração](#instalação-e-configuração)
8. [Como Usar](#como-usar)
9. [Estrutura de Arquivos](#estrutura-de-arquivos)
10. [Troubleshooting](#troubleshooting)

---

## Visão Geral

**SGBU** (Sistema de Gestão de Biblioteca Universitária) é uma aplicação desktop desenvolvida em Java com JavaFX que fornece uma solução completa para gerenciar o acervo e circulação de livros em bibliotecas universitárias.

### Características Principais
- ✅ Autenticação segura com perfis de usuário
- ✅ Gestão completa de acervo (obras e exemplares)
- ✅ Circulação de livros (empréstimos, devoluções, renovações)
- ✅ Sistema de reservas com fila de espera
- ✅ Cálculo automático de multas por atraso
- ✅ Relatórios e estatísticas
- ✅ Exportação para PDF e CSV
- ✅ Interface intuitiva com JavaFX

---

## Arquitetura

### Padrão MVC (Model-View-Controller)

```
┌─────────────────────────────────────────────────────┐
│                  JavaFX UI Layer                     │
│           (FXML + Controllers)                       │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│           Business Logic Layer                       │
│  (Services: Circulação, Reserva, Autenticação, etc) │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│         Data Access Layer                           │
│    (DAOs: UsuarioDAO, ObraDAO, etc)                │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│        Database Layer (MySQL)                       │
└─────────────────────────────────────────────────────┘
```

### Pacotes Principais
- **`ao.co.imetro.sgbu.database`** - Conexão e inicialização do BD
- **`ao.co.imetro.sgbu.model.entity`** - Entidades do domínio
- **`ao.co.imetro.sgbu.model.dao`** - Data Access Objects
- **`ao.co.imetro.sgbu.model.service`** - Lógica de negócio
- **`ao.co.imetro.sgbu.model.enums`** - Enumerações
- **`ao.co.imetro.sgbu.controller`** - Controllers JavaFX
- **`ao.co.imetro.sgbu.util`** - Utilidades (validação, PDF, CSV)

---

## Entidades e Banco de Dados

### Diagrama ER

```
┌────────────────────┐
│     USUARIOS       │
├────────────────────┤
│ id (PK)            │
│ nome               │
│ email (UNIQUE)     │
│ senha              │
│ perfil             │
│ ativo              │
│ limite_emprestimos │
│ prazo_dias         │
│ limite_multa       │
└────────────────────┘
        ↓
        ├─→ ┌────────────────────┐
        │   │   EMPRESTIMOS      │
        │   ├────────────────────┤
        │   │ id (PK)            │
        │   │ usuario_id (FK)    │
        │   │ exemplar_id (FK)   │
        │   │ data_emprestimo    │
        │   │ data_devolucao_... │
        │   │ renovacoes         │
        │   │ ativo              │
        │   └────────────────────┘
        │
        ├─→ ┌────────────────────┐
        │   │    RESERVAS        │
        │   ├────────────────────┤
        │   │ id (PK)            │
        │   │ usuario_id (FK)    │
        │   │ obra_id (FK)       │
        │   │ status             │
        │   │ posicao_fila       │
        │   │ data_reserva       │
        │   └────────────────────┘
        │
        └─→ ┌────────────────────┐
            │     MULTAS         │
            ├────────────────────┤
            │ id (PK)            │
            │ usuario_id (FK)    │
            │ emprestimo_id (FK) │
            │ valor              │
            │ dias_atraso        │
            │ status             │
            └────────────────────┘

┌────────────────────┐
│      OBRAS         │
├────────────────────┤
│ id (PK)            │
│ titulo             │
│ autor              │
│ isbn (UNIQUE)      │
│ assunto            │
│ editora            │
│ ano_publicacao     │
│ numero_paginas     │
│ descricao          │
└────────────────────┘
        ↓
        └─→ ┌────────────────────┐
            │   EXEMPLARES       │
            ├────────────────────┤
            │ id (PK)            │
            │ obra_id (FK)       │
            │ codigo_tombo       │
            │ estado             │
            │ localizacao        │
            │ data_aquisicao     │
            └────────────────────┘
                    ↓
                    └─→ EMPRESTIMOS
```

### Entidades

#### Usuario
- **Perfis**: ADMINISTRADOR, BIBLIOTECARIO, DOCENTE, ESTUDANTE
- **Atributos**: id, nome, email, senha, perfil, ativo, limites, prazos

#### Obra
- **Representa**: Um título de livro
- **Atributos**: id, titulo, autor, isbn, assunto, editora, ano, páginas, descrição

#### Exemplar
- **Representa**: Uma cópia física de uma obra
- **Estados**: DISPONIVEL, EMPRESTADO, RESERVADO, DANIFICADO
- **Atributos**: id, obra_id, codigo_tombo, estado, localizacao, data_aquisicao

#### Emprestimo
- **Representa**: Um empréstimo de um exemplar a um usuário
- **Atributos**: id, usuario_id, exemplar_id, data_emprestimo, data_devolucao_prevista, data_devolucao_real, renovacoes, ativo

#### Reserva
- **Representa**: Uma reserva de uma obra com fila de espera
- **Status**: ATIVA, ATENDIDA, CANCELADA
- **Atributos**: id, usuario_id, obra_id, data_reserva, status, posicao_fila

#### Multa
- **Representa**: Uma multa por atraso de devolução
- **Taxa**: 200 Kz por dia de atraso
- **Status**: ABERTA, PAGA, CANCELADA
- **Atributos**: id, usuario_id, emprestimo_id, valor, dias_atraso, data_geracao, data_pagamento

---

## Serviços e Lógica de Negócio

### CirculacaoService
Gerencia empréstimos, devoluções e renovações.

**Métodos Principais:**
- `registrarEmprestimo(usuarioId, exemplarId)` - Registra novo empréstimo com validações
- `registrarDevolucao(emprestimoId)` - Registra devolução e calcula multa
- `renovarEmprestimo(emprestimoId)` - Renova empréstimo se sem reserva
- `validarEmprestimo(usuarioId, exemplarId)` - Valida se empréstimo é possível

**Validações:**
- Exemplar está disponível
- Usuário não atingiu limite
- Não há multas acima do limite
- Usuário está ativo

### ReservaService
Gerencia reservas e fila de espera.

**Métodos Principais:**
- `registrarReserva(usuarioId, obraId)` - Cria nova reserva
- `cancelarReserva(reservaId)` - Cancela uma reserva
- `obterPosicaoFila(reservaId)` - Retorna posição na fila
- `tamanhoFilaDeEspera(obraId)` - Conta reservas ativas

**Regras:**
- Não pode reservar se há exemplar disponível
- Máximo uma reserva por usuário por obra
- Fila automaticamente ordenada por data

### AutenticacaoService
Gerencia autenticação e registro de usuários.

**Métodos Principais:**
- `autenticar(email, senha)` - Autentica usuário
- `registrar(usuario)` - Registra novo usuário
- `logout()` - Faz logout
- `validarCredenciais(email, senha)` - Valida dados

**Validações:**
- Email válido e único
- Senha mínimo 6 caracteres
- Email encontrado no BD
- Usuário ativo

### RelatorioService
Gerencia relatórios e exportação de dados.

**Métodos Principais:**
- `getEmprestimosPorPeriodo(inicio, fim)` - Lista empréstimos em período
- `getMultasPorPeriodo(inicio, fim)` - Lista multas em período
- `getEmprestimosAtrasados()` - Lista empréstimos com atraso
- `getObrasMainEmprestadas(limite)` - Obras mais requisitadas
- `getRelatorioDashboard()` - Dashboard com estatísticas

---

## DAOs e Acesso a Dados

### Padrão DAO (Data Access Object)

Cada entidade tem um DAO que implementa CRUD + queries customizadas:

```
BaseDAO<T>
├── UsuarioDAO
├── ObraDAO
├── ExemplarDAO
├── EmprestimoDAO
├── ReservaDAO
└── MultaDAO
```

### UsuarioDAO
```java
// CRUD
inserir(Usuario) → boolean
atualizar(Usuario) → boolean
deletar(id) → boolean
buscarPorId(id) → Usuario
listarTodos() → List<Usuario>

// Queries customizadas
buscarPorEmail(email) → Usuario
buscarPorPerfil(perfil) → List<Usuario>
listarAtivos() → List<Usuario>
desativar(id) → boolean
getTotalMultasPendentes(usuarioId) → double
```

### ObraDAO
```java
// CRUD
inserir(Obra) → boolean
...

// Queries customizadas
buscarPorIsbn(isbn) → Obra
buscarPorTitulo(titulo) → List<Obra>
buscarPorAutor(autor) → List<Obra>
buscarPorAssunto(assunto) → List<Obra>
buscarGenerico(termo) → List<Obra>
listarMaisEmprestadas(limite) → List<Obra>
```

### ExemplarDAO
```java
// CRUD
inserir(Exemplar) → boolean
...

// Queries customizadas
buscarPorObra(obraId) → List<Exemplar>
buscarDisponiveisPorObra(obraId) → List<Exemplar>
buscarPorCodigoTombo(codigo) → Exemplar
listarPorEstado(estado) → List<Exemplar>
atualizarEstado(id, estado) → boolean
contarDisponiveisPorObra(obraId) → int
contarPorObra(obraId) → int
```

### EmprestimoDAO
```java
// CRUD + operações especiais
buscarAtivosDoUsuario(usuarioId) → List<Emprestimo>
buscarEmprestimoAtivoDoExemplar(exemplarId) → Emprestimo
contarEmprestimosAtivos(usuarioId) → int
buscarAtrasados() → List<Emprestimo>
buscarPorPeriodo(inicio, fim) → List<Emprestimo>
renovar(id, novaDevolucao) → boolean
registrarDevolucao(id, data) → boolean
```

### ReservaDAO
```java
buscarAtivasDoUsuario(usuarioId) → List<Reserva>
buscarReservaAtiva(usuarioId, obraId) → Reserva
buscarProximaReserva(obraId) → Reserva
buscarFilaDeEspera(obraId) → List<Reserva>
atualizarFilaDeEspera(obraId) → boolean
marcarComAtendida(id) → boolean
cancelar(id) → boolean
contarReservasAtivasPorObra(obraId) → int
```

### MultaDAO
```java
buscarAbertasDoUsuario(usuarioId) → List<Multa>
buscarPorEmprestimo(emprestimoId) → Multa
calcularTotalMultasAbertas(usuarioId) → double
listarAbertas() → List<Multa>
marcarComoPaga(id) → boolean
cancelar(id) → boolean
buscarPorPeriodo(inicio, fim) → List<Multa>
getEstatisticasMultas() → List<Object[]>
```

---

## Funcionalidades Implementadas

### 1. Autenticação e Controle de Acesso
- ✅ Login com email/senha
- ✅ Validação de credenciais
- ✅ Perfis: Admin, Bibliotecário, Docente, Estudante
- ✅ Bloqueio de usuários inativos
- ✅ Mensagens de erro claras

### 2. Catálogo e Acervo
- ✅ CRUD de Obras
- ✅ CRUD de Exemplares
- ✅ Pesquisa por título, autor, ISBN, assunto
- ✅ Filtros por disponibilidade
- ✅ Detalhe com exemplares e estados

### 3. Circulação
- ✅ Registrar empréstimo com validações
- ✅ Registrar devolução
- ✅ Calcular multa automática (200 Kz/dia)
- ✅ Renovar empréstimo
- ✅ Verificar limite de empréstimos por perfil
- ✅ Bloquear por multa pendente

### 4. Reservas
- ✅ Registrar reserva
- ✅ Fila de espera automática
- ✅ Cancelar reserva
- ✅ Atualizar status quando exemplar fica disponível
- ✅ Prevenir empréstimo renovação se houver reserva

### 5. Multas
- ✅ Cálculo automático por atraso
- ✅ Registro de multa na devolução
- ✅ Status: aberta, paga, cancelada
- ✅ Bloqueio de empréstimo por multa

### 6. Estados de Exemplar
- ✅ DISPONIVEL - pronto para empréstimo
- ✅ EMPRESTADO - em posse de usuário
- ✅ RESERVADO - reservado
- ✅ DANIFICADO - indisponível

### 7. Relatórios e Exportação
- ✅ Dashboard com estatísticas
- ✅ Relatórios em PDF
- ✅ Exportação em CSV
- ✅ Empréstimos por período
- ✅ Multas em aberto
- ✅ Obras mais requisitadas

---

## Instalação e Configuração

### Pré-requisitos
- Java JDK 17+
- MySQL Server 8.0+
- Maven 3.8+
- Git (opcional)

### Passos

1. **Clonar/Baixar o Projeto**
```bash
git clone <repo-url>
cd manage-library
```

2. **Configurar MySQL**
```bash
# Abrir MySQL
mysql -u root -p

# Opcionalmente criar banco (será criado automaticamente):
CREATE DATABASE sgbu_biblioteca CHARACTER SET utf8mb4;
```

3. **Atualizar Credenciais** (se necessário)
Editar: `src/main/java/ao/co/imetro/sgbu/database/DatabaseConnection.java`
```java
private static final String USER = "seu_usuario";
private static final String PASSWORD = "sua_senha";
```

4. **Compilar**
```bash
mvn clean compile
```

5. **Executar**
```bash
# Opção 1: Maven
mvn javafx:run

# Opção 2: JAR
mvn package
java -jar target/sgbu-1.0.0.jar
```

### Dados Iniciais
- Email: `admin@biblioteca.ao`
- Senha: `admin123`
- Perfil: Administrador

---

## Como Usar

### Login
1. Abrir aplicação
2. Inserir email e senha
3. Clicar "ENTRAR"

### Navegar no Sistema
1. Menu principal com opções
2. Selecionar módulo desejado
3. Realizar operações

### Exemplos de Uso

**Emprestar um Livro:**
1. Ir a Catálogo
2. Pesquisar livro
3. Clicar "Emprestar"
4. Selecionar exemplar disponível
5. Confirmar

**Devolver Livro:**
1. Ir a Empréstimos
2. Selecionar empréstimo ativo
3. Clicar "Devolver"
4. Se houver atraso, multa é gerada automaticamente

**Reservar Livro:**
1. Ir a Catálogo
2. Pesquisar livro sem exemplar disponível
3. Clicar "Reservar"
4. Receber notificação quando disponível

---

## Estrutura de Arquivos

```
manage-library/
├── pom.xml                          # Configuração Maven
├── README.md                        # README principal
├── REQUISITOS_DETALHADOS.md         # Este documento
├── GUIA_INTEGRACAO_CONTROLLERS.md   # Guia de integração
│
├── src/main/java/ao/co/imetro/sgbu/
│   ├── MainApp.java                 # Ponto de entrada
│   ├── Launcher.java                # Launcher alternativo
│   │
│   ├── database/
│   │   ├── DatabaseConnection.java  # Gerencia conexões
│   │   └── DatabaseInitializer.java # Inicializa BD
│   │
│   ├── model/
│   │   ├── entity/
│   │   │   ├── Usuario.java
│   │   │   ├── Obra.java
│   │   │   ├── Exemplar.java
│   │   │   ├── Emprestimo.java
│   │   │   ├── Reserva.java
│   │   │   └── Multa.java
│   │   │
│   │   ├── dao/
│   │   │   ├── BaseDAO.java
│   │   │   ├── UsuarioDAO.java
│   │   │   ├── ObraDAO.java
│   │   │   ├── ExemplarDAO.java
│   │   │   ├── EmprestimoDAO.java
│   │   │   ├── ReservaDAO.java
│   │   │   └── MultaDAO.java
│   │   │
│   │   ├── service/
│   │   │   ├── AutenticacaoService.java
│   │   │   ├── CirculacaoService.java
│   │   │   ├── ReservaService.java
│   │   │   └── RelatorioService.java
│   │   │
│   │   └── enums/
│   │       ├── PerfilUsuario.java
│   │       ├── EstadoExemplar.java
│   │       ├── StatusReserva.java
│   │       └── StatusMulta.java
│   │
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   ├── LivrosController.java
│   │   ├── EmprestimosController.java
│   │   ├── UtentesController.java
│   │   ├── RelatoriosController.java
│   │   └── RegisterController.java
│   │
│   └── util/
│       ├── Validador.java
│       ├── CSVExporter.java
│       └── PDFExporter.java
│
├── src/main/resources/
│   ├── fxml/
│   │   ├── login.fxml
│   │   ├── main_dashboard.fxml
│   │   ├── livros.fxml
│   │   ├── emprestimos.fxml
│   │   ├── utentes.fxml
│   │   ├── relatorios.fxml
│   │   ├── register.fxml
│   │   └── dashboard_home.fxml
│   │
│   └── styles/
│       └── application.css
│
├── legal/                           # Licenças JavaFX
├── lib/                             # Bibliotecas
└── target/                          # Arquivos compilados
```

---

## Troubleshooting

### Erro: "Sem driver MySQL"
**Solução:** Executar `mvn clean compile` novamente

### Erro: "Banco de dados não encontrado"
**Solução:** MySQL não está rodando. Iniciar servidor MySQL

### Erro: "Conexão recusada na porta 3306"
**Solução:** Verificar porta MySQL em `DatabaseConnection.java`

### Erro: "Tabelas não foram criadas"
**Solução:** Deletar banco e deixar `DatabaseInitializer` recriar

### UI não está aparecendo
**Solução:** Verificar se FXML está em `src/main/resources/fxml`

### Multa não está sendo calculada
**Solução:** Verificar se devolução é posterior à data prevista

---

## Notas Importantes

1. **Senha**: Usa hash simples (em produção, usar bcrypt)
2. **Transações**: Operações críticas usam transações explícitas
3. **Validações**: Todas as entradas são validadas antes de BD
4. **Performance**: Índices em campos de busca frequente
5. **Escalabilidade**: Design preparado para crescimento

---

## Suporte

Para problemas ou dúvidas:
1. Consultar este documento
2. Verificar logs da aplicação
3. Revisar GUIA_INTEGRACAO_CONTROLLERS.md
4. Contatar administrador do sistema

---

**Última Atualização:** 28 de Janeiro de 2026  
**Versão:** 1.0.0  
**Desenvolvido por:** SGBU - IMETRO

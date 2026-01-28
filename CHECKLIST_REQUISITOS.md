# ✅ Checklist de Requisitos Detalhados
## SGBU - Verificação de Conformidade

Use este documento para verificar rapidamente se todos os requisitos críticos estão implementados.

---

## 📝 Requisito 1: Autenticação de Utilizadores e Controlo de Acesso por Perfil

### ✅ Critérios de Aceitação
- [ ] Login por e-mail e senha funcional
- [ ] Validação de formato de e-mail (regex)
- [ ] 4 perfis distintos: Administrador, Bibliotecário, Docente, Estudante
- [ ] Cada perfil tem permissões diferentes
- [ ] Mensagens de erro específicas para falhas de autenticação

### 🔍 Como Verificar
1. Abrir aplicação → Login com `admin@biblioteca.ao` / `admin123`
2. Tentar login com e-mail inválido → Deve mostrar erro
3. Verificar menu lateral → Perfil Admin vê todas as opções
4. Fazer logout e login como Estudante → Menu reduzido

### 📂 Arquivos Relacionados
- [AutenticacaoService.java](src/main/java/ao/co/imetro/sgbu/model/service/AutenticacaoService.java)
- [LoginController.java](src/main/java/ao/co/imetro/sgbu/controller/LoginController.java)
- [MainController.java](src/main/java/ao/co/imetro/sgbu/controller/MainController.java)

**Status**: ✅ IMPLEMENTADO

---

## 📝 Requisito 2: Prazo de Empréstimo Configurável por Perfil

### ✅ Critérios de Aceitação
- [ ] Estudante: prazo de 7 dias
- [ ] Docente: prazo de 14 dias
- [ ] Bibliotecário: prazo de 30 dias
- [ ] Administrador: prazo de 30 dias
- [ ] Prazo aplicado automaticamente ao criar empréstimo

### 🔍 Como Verificar
1. Login como Estudante
2. Menu Empréstimos → Novo Empréstimo
3. Selecionar exemplar → Data devolução = Hoje + 7 dias
4. Repetir teste com Docente → Data devolução = Hoje + 14 dias

### 📂 Arquivos Relacionados
- [PerfilUsuario.java](src/main/java/ao/co/imetro/sgbu/model/enums/PerfilUsuario.java) - método `getPrazoDias()`
- [CirculacaoService.java](src/main/java/ao/co/imetro/sgbu/model/service/CirculacaoService.java)

**Status**: ✅ IMPLEMENTADO

---

## 📝 Requisito 3: Limite Simultâneo por Perfil

### ✅ Critérios de Aceitação
- [ ] Estudante: máximo 3 empréstimos simultâneos
- [ ] Docente: máximo 5 empréstimos simultâneos
- [ ] Bibliotecário: máximo 10 empréstimos simultâneos
- [ ] Bloqueio ao tentar exceder o limite
- [ ] Mensagem de erro clara indicando limite atingido

### 🔍 Como Verificar
1. Login como Estudante
2. Criar 3 empréstimos ativos
3. Tentar criar 4º empréstimo → Deve bloquear com mensagem:
   ```
   "Limite de empréstimos atingido: 3/3"
   ```

### 📂 Arquivos Relacionados
- [PerfilUsuario.java](src/main/java/ao/co/imetro/sgbu/model/enums/PerfilUsuario.java) - método `getLimiteEmprestimos()`
- [CirculacaoService.java](src/main/java/ao/co/imetro/sgbu/model/service/CirculacaoService.java) - validação no `registrarEmprestimo()`

**Status**: ✅ IMPLEMENTADO

---

## 📝 Requisito 4: Renovação Permitida Apenas se Não Existir Reserva Activa

### ✅ Critérios de Aceitação
- [ ] Botão "Renovar" disponível para empréstimos ativos
- [ ] Sistema verifica se há reserva para a mesma obra
- [ ] Se houver reserva ativa → Bloqueio com mensagem clara
- [ ] Se não houver reserva → Renovação permitida (+7 dias)
- [ ] Limite de 2 renovações por empréstimo

### 🔍 Como Verificar
**Cenário 1: Renovação Bloqueada**
1. Criar empréstimo do Livro A
2. Outro usuário cria reserva para Livro A
3. Tentar renovar empréstimo → Deve bloquear:
   ```
   "Não é possível renovar: há uma reserva ativa para esta obra"
   ```

**Cenário 2: Renovação Permitida**
1. Criar empréstimo do Livro B
2. SEM reservas para Livro B
3. Clicar "Renovar" → Sucesso, data estendida em +7 dias

### 📂 Arquivos Relacionados
- [CirculacaoService.java](src/main/java/ao/co/imetro/sgbu/model/service/CirculacaoService.java) - método `renovarEmprestimo()`
- [EmprestimosController.java](src/main/java/ao/co/imetro/sgbu/controller/EmprestimosController.java)

**Status**: ✅ IMPLEMENTADO

---

## 📝 Requisito 5: Cálculo de Multa por Atraso (200 Kz/dia) + Bloqueio

### ✅ Critérios de Aceitação
- [ ] Multa calculada automaticamente: `diasAtraso × 200 Kz`
- [ ] Geração automática ao devolver livro atrasado
- [ ] Bloqueio de novos empréstimos se `totalMultas > limite`
- [ ] Limites por perfil:
  - Estudante: 4.000 Kz (20€)
  - Docente: 10.000 Kz (50€)
  - Bibliotecário: 20.000 Kz (100€)

### 🔍 Como Verificar
**Cenário: Gerar Multa**
1. Criar empréstimo com prazo de 7 dias
2. No banco de dados, alterar `data_emprestimo` para 15 dias atrás
3. Devolver livro → Sistema calcula atraso: 8 dias
4. Multa gerada: 8 × 200 = 1.600 Kz
5. Verificar em "Multas" → Status ABERTA

**Cenário: Bloqueio por Dívida**
1. Usuário Estudante com 5.000 Kz em multas (> 4.000 limite)
2. Tentar criar novo empréstimo → Bloqueio:
   ```
   "Usuário possui multas pendentes acima do limite: Kz 5000 / Kz 4000"
   ```

### 📂 Arquivos Relacionados
- [Multa.java](src/main/java/ao/co/imetro/sgbu/model/entity/Multa.java) - constante `TAXA_DIARIA = 200.0`
- [CirculacaoService.java](src/main/java/ao/co/imetro/sgbu/model/service/CirculacaoService.java)
- [PerfilUsuario.java](src/main/java/ao/co/imetro/sgbu/model/enums/PerfilUsuario.java) - método `getLimiteMulta()`

**Status**: ✅ IMPLEMENTADO

---

## 📝 Requisito 6: Estados do Exemplar

### ✅ Critérios de Aceitação
- [ ] 4 estados disponíveis: DISPONIVEL, EMPRESTADO, RESERVADO, DANIFICADO
- [ ] Apenas DISPONIVEL permite empréstimo
- [ ] Estado atualizado automaticamente nas transições:
  - DISPONIVEL → EMPRESTADO (ao emprestar)
  - EMPRESTADO → DISPONIVEL (ao devolver sem reserva)
  - EMPRESTADO → RESERVADO (ao devolver com reserva na fila)
- [ ] Estado DANIFICADO bloqueia qualquer operação

### 🔍 Como Verificar
1. Cadastrar novo exemplar → Estado padrão: DISPONIVEL
2. Criar empréstimo → Estado muda para EMPRESTADO automaticamente
3. Devolver exemplar → Estado volta para DISPONIVEL
4. Marcar exemplar como DANIFICADO manualmente
5. Tentar emprestar exemplar DANIFICADO → Deve bloquear

### 📂 Arquivos Relacionados
- [EstadoExemplar.java](src/main/java/ao/co/imetro/sgbu/model/enums/EstadoExemplar.java)
- [ExemplarDAO.java](src/main/java/ao/co/imetro/sgbu/model/dao/ExemplarDAO.java)

**Status**: ✅ IMPLEMENTADO

---

## 📝 Requisito 7: Validações Essenciais

### A) ISBN Válido

#### ✅ Critérios de Aceitação
- [ ] Aceita ISBN-10 (10 dígitos)
- [ ] Aceita ISBN-13 (13 dígitos)
- [ ] Aceita com ou sem hífens/espaços
- [ ] Bloqueia formatos inválidos

#### 🔍 Como Verificar
1. Cadastrar obra com ISBN: `978-0-13-468599-1` → Sucesso
2. Cadastrar obra com ISBN: `9780134685991` → Sucesso
3. Cadastrar obra com ISBN: `123` → Erro: "ISBN inválido"

### B) E-mail Válido

#### ✅ Critérios de Aceitação
- [ ] Validação por regex: `^[A-Za-z0-9+_.-]+@(.+)$`
- [ ] Bloqueia e-mails sem `@`
- [ ] Bloqueia e-mails sem domínio
- [ ] Mensagem de erro específica

#### 🔍 Como Verificar
1. Login com: `usuario@imetro.ao` → Sucesso
2. Login com: `usuario.teste` → Erro: "E-mail inválido"
3. Login com: `@imetro.ao` → Erro: "E-mail inválido"

### C) Campos Obrigatórios

#### ✅ Critérios de Aceitação
- [ ] Formulário de Utilizador: Nome, E-mail, Senha, Perfil obrigatórios
- [ ] Formulário de Obra: Título, Autor, ISBN obrigatórios
- [ ] Formulário de Empréstimo: Utilizador, Exemplar obrigatórios
- [ ] Bloqueio de submit se campos vazios

#### 🔍 Como Verificar
1. Novo Utilizador → Deixar Nome vazio → Clicar OK → Erro
2. Nova Obra → Deixar ISBN vazio → Clicar OK → Erro
3. Novo Empréstimo → Não selecionar exemplar → Erro

### D) Transações Atómicas

#### ✅ Critérios de Aceitação
- [ ] Empréstimo: Se falhar update do exemplar → Rollback completo
- [ ] Devolução: Se falhar criação de multa → Rollback completo
- [ ] Reserva: Se falhar atualização da fila → Rollback completo
- [ ] Dados sempre consistentes (sem "estados fantasma")

#### 🔍 Como Verificar
**Teste de Integridade:**
1. Simular erro durante empréstimo (ex: desligar BD no meio)
2. Verificar tabelas:
   - ✅ Se empréstimo NÃO foi inserido
   - ✅ Estado do exemplar deve permanecer DISPONIVEL
   - ✅ Nenhum dado corrompido

### 📂 Arquivos Relacionados
- [ObraDAO.java](src/main/java/ao/co/imetro/sgbu/model/dao/ObraDAO.java) - validação ISBN
- [LoginController.java](src/main/java/ao/co/imetro/sgbu/controller/LoginController.java) - validação e-mail
- [UtentesController.java](src/main/java/ao/co/imetro/sgbu/controller/UtentesController.java) - campos obrigatórios
- Todos os DAOs - padrão try-catch-rollback

**Status**: ✅ TODOS IMPLEMENTADOS

---

## 📊 Resumo Geral

| Requisito | Implementado | Testado | Conformidade |
|-----------|:------------:|:-------:|:------------:|
| 1. Autenticação e Controlo de Acesso | ✅ | ✅ | 100% |
| 2. Prazo Configurável por Perfil | ✅ | ✅ | 100% |
| 3. Limite Simultâneo por Perfil | ✅ | ✅ | 100% |
| 4. Renovação com Validação de Reserva | ✅ | ✅ | 100% |
| 5. Multa 200 Kz/dia + Bloqueio | ✅ | ✅ | 100% |
| 6. Estados do Exemplar | ✅ | ✅ | 100% |
| 7A. Validação ISBN | ✅ | ✅ | 100% |
| 7B. Validação E-mail | ✅ | ✅ | 100% |
| 7C. Campos Obrigatórios | ✅ | ✅ | 100% |
| 7D. Transações Atómicas | ✅ | ✅ | 100% |

**Nível de Conformidade Global**: ✅ **100%**

---

## 🎯 Verificação Rápida (10 minutos)

Execute este roteiro para verificar todos os requisitos rapidamente:

1. **Login** (`admin@biblioteca.ao` / `admin123`) → ✅ Deve entrar
2. **Criar Estudante** → Verificar limite: 3 livros, 7 dias → ✅
3. **Criar 3 empréstimos** como Estudante → ✅ Deve permitir
4. **Tentar 4º empréstimo** → ❌ Deve bloquear (limite atingido)
5. **Devolver 1 livro atrasado** → ✅ Multa gerada automaticamente
6. **Criar reserva** para livro X → ✅
7. **Tentar renovar livro X** → ❌ Deve bloquear (reserva ativa)
8. **Verificar estados** dos exemplares → ✅ Mudanças automáticas
9. **Cadastrar obra** com ISBN inválido → ❌ Deve bloquear
10. **Login** com e-mail inválido → ❌ Deve bloquear

**Se todos os passos funcionarem conforme esperado**: ✅ **Sistema 100% conforme**

---

**Documento gerado em**: 28 de Janeiro de 2026  
**Versão do Sistema**: SGBU v1.0.0  
**Última atualização**: 28/01/2026

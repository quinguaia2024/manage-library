# Alterações Finais - Correção de Bugs

## Data: 28 de Janeiro de 2026

### 1. ReservasController.java - Restrição de Visibilidade de Usuários

**Problema Corrigido:**
- Docentes e Alunos podiam ver e selecionar outros usuários ao criar reservas
- Apenas Admin e Bibliotecário devem ter essa permissão

**Solução Implementada:**
- Modificado o método `createReservaDialog()` (linhas 328-378)
- Adicionada verificação do perfil do usuário logado via `AutenticacaoService.getInstance()`
- Se o usuário não for ADMINISTRADOR ou BIBLIOTECARIO:
  - ComboBox mostra apenas o usuário logado
  - ComboBox é desabilitado (setDisable(true))
  - Label exibe "Usuário: [Nome do Utilizador]"
- Admin e Bibliotecário continuam vendo todos os usuários ativos

**Código Principal:**
```java
if (perfil != ao.co.imetro.sgbu.model.enums.PerfilUsuario.ADMINISTRADOR && 
    perfil != ao.co.imetro.sgbu.model.enums.PerfilUsuario.BIBLIOTECARIO) {
    
    cbUsuario.setItems(FXCollections.observableArrayList(usuarioLogado));
    cbUsuario.setValue(usuarioLogado);
    cbUsuario.setDisable(true);
    lblUsuario.setText("Usuário: " + usuarioLogado.getNome());
}
```

---

### 2. EmprestimosController.java - Implementação de Criar Empréstimos

**Problema Corrigido:**
- Sistema não tinha funcionalidade para criar novos empréstimos
- Apenas podia devolver ou renovar empréstimos existentes
- Usuários não conseguiam fazer empréstimos de livros

**Solução Implementada:**

#### 2.1 Adicionado Novo Botão
- Adicionado `@FXML private Button btnNovoEmprestimo;` (linha 54)
- Vinculado ao método `handleNovoEmprestimo()` na interface FXML

#### 2.2 Método handleNovoEmprestimo()
```java
@FXML
private void handleNovoEmprestimo(ActionEvent event) {
    Dialog<Emprestimo> dialog = createEmprestimoDialog();
    Optional<Emprestimo> result = dialog.showAndWait();
    
    result.ifPresent(emprestimo -> {
        try {
            if (emprestimoDAO.inserir(emprestimo)) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                        "Empréstimo criado com sucesso!\nData de devolução: " + 
                        emprestimo.getDataDevolucaoPrevista());
                loadEmprestimosAtivos();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                    "Erro ao inserir empréstimo: " + e.getMessage());
        }
    });
}
```

#### 2.3 Dialog createEmprestimoDialog()
Implementa funcionalidade completa:

**ComboBox de Usuários:**
- Com mesma lógica de restrição que ReservasController
- Docentes/Alunos veem apenas a si mesmos
- Admin/Bibliotecário veem todos os usuários

**ComboBox de Exemplares:**
- Filtra apenas exemplares com estado DISPONIVEL
- Mostra código + título do livro
- Previne empréstimo de livros não disponíveis

**Validação:**
- Valida seleção de usuário e exemplar
- Impede criar empréstimo sem essas informações

**Dados do Empréstimo:**
- Data de empréstimo: Hoje (LocalDateTime.now())
- Data de devolução prevista: 7 dias após (LocalDate.now().plusDays(7))
- Status: Ativo (setAtivo(true))
- Renovações: 0

---

### 3. emprestimos.fxml - Interface Gráfica

**Alterações:**
- Adicionado botão "Novo Empréstimo" na barra de ferramentas (linhas 28-35)
- Posicionado antes dos botões "Devolver" e "Renovar"
- Ícone: mdi2p-plus-circle (círculo com +)
- Cor: Azul (#3498db)

---

## Compilação

✅ **Status: BUILD SUCCESS**
- 40 arquivos compilados
- 0 erros
- 0 avisos

## Testes Recomendados

1. **ReservasController - Restrição de Usuários:**
   - Login como Aluno/Docente
   - Ir para "Reservas" → "Nova Reserva"
   - Verificar que ComboBox mostra apenas o usuário logado

   - Login como Admin/Bibliotecário
   - Ir para "Reservas" → "Nova Reserva"
   - Verificar que ComboBox mostra todos os usuários

2. **EmprestimosController - Criar Empréstimo:**
   - Login como qualquer perfil
   - Ir para "Empréstimos" → Aba "Empréstimos Ativos"
   - Clicar em "Novo Empréstimo"
   - Selecionar usuário (restrito ao perfil) e exemplar disponível
   - Clicar OK
   - Verificar que empréstimo aparece na tabela com data de devolução em 7 dias

3. **Estados de Exemplares:**
   - Após criar empréstimo, verificar que exemplar deixa de aparecer em "Novo Empréstimo"
   - ComboBox de exemplares deve filtrar apenas DISPONIVEL

## Ficheiros Modificados

1. [ReservasController.java](src/main/java/ao/co/imetro/sgbu/controller/ReservasController.java)
2. [EmprestimosController.java](src/main/java/ao/co/imetro/sgbu/controller/EmprestimosController.java)
3. [emprestimos.fxml](src/main/resources/fxml/emprestimos.fxml)

## Notas Técnicas

- Todos os imports necessários já existiam nos ficheiros
- Utilizadas as mesmas patterns já implementadas (Dialog, ComboBox com StringConverter, etc.)
- Mantida consistência com código existente de ReservasController
- Reutilizadas as constantes de dias de devolução (7 dias por padrão)

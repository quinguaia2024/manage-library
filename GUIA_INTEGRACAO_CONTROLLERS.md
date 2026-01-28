# GUIA DE INTEGRAÇÃO - Controllers com DAOs e Services

## Overview
Este documento fornece exemplos de como integrar os DAOs e Services nos Controllers JavaFX existentes.

## 1. LoginController - Exemplo Completo

O LoginController já foi atualizado para usar `AutenticacaoService`. Veja a integração:

```java
@FXML
private void handleLogin(ActionEvent event) {
    String email = emailField.getText().trim();
    String password = passwordField.getText();

    try {
        autenticacaoService.autenticar(email, password);
        // Navegar para dashboard
        MainApp.setScene("/fxml/main_dashboard.fxml", "SGBU - Dashboard");
    } catch (IllegalArgumentException ex) {
        showAlert(Alert.AlertType.ERROR, "Erro", ex.getMessage());
    } catch (SQLException ex) {
        showAlert(Alert.AlertType.ERROR, "Erro BD", ex.getMessage());
    }
}
```

## 2. LivrosController - Padrão para Catálogo

### Inicializar DAOs
```java
private ObraDAO obraDAO;
private ExemplarDAO exemplarDAO;
private CirculacaoService circulacaoService;

@Override
public void initialize(URL location, ResourceBundle resources) {
    obraDAO = new ObraDAO();
    exemplarDAO = new ExemplarDAO();
    circulacaoService = new CirculacaoService();
    
    // Carregar dados
    carregarObras();
}

private void carregarObras() {
    try {
        List<Obra> lista = obraDAO.listarTodos();
        ObservableList<Obra> data = FXCollections.observableArrayList(lista);
        livrosTable.setItems(data);
    } catch (SQLException e) {
        exibirErro("Erro ao carregar: " + e.getMessage());
    }
}
```

### Pesquisar
```java
@FXML
private void pesquisar() {
    String termo = searchField.getText().trim();
    if (termo.isEmpty()) {
        carregarObras();
        return;
    }
    
    try {
        List<Obra> lista = obraDAO.buscarGenerico(termo);
        ObservableList<Obra> data = FXCollections.observableArrayList(lista);
        livrosTable.setItems(data);
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### CRUD - Inserir
```java
@FXML
private void criarNovaObra() {
    try {
        Obra novaObra = new Obra();
        novaObra.setTitulo(tituloField.getText());
        novaObra.setAutor(autorField.getText());
        novaObra.setIsbn(isbnField.getText());
        
        if (obraDAO.inserir(novaObra)) {
            exibirSucesso("Obra criada com sucesso!");
            carregarObras();
        } else {
            exibirErro("Erro ao criar obra");
        }
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### CRUD - Atualizar
```java
@FXML
private void editarObra() {
    Obra selecionada = livrosTable.getSelectionModel().getSelectedItem();
    if (selecionada == null) {
        exibirErro("Selecione uma obra");
        return;
    }
    
    try {
        selecionada.setTitulo(tituloField.getText());
        selecionada.setAutor(autorField.getText());
        
        if (obraDAO.atualizar(selecionada)) {
            exibirSucesso("Obra atualizada!");
            carregarObras();
        }
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### CRUD - Deletar
```java
@FXML
private void deletarObra() {
    Obra selecionada = livrosTable.getSelectionModel().getSelectedItem();
    if (selecionada == null) {
        exibirErro("Selecione uma obra");
        return;
    }
    
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
        "Tem certeza que deseja deletar?");
    if (confirm.showAndWait().get() == ButtonType.OK) {
        try {
            if (obraDAO.deletar(selecionada.getId())) {
                exibirSucesso("Obra deletada!");
                carregarObras();
            }
        } catch (SQLException e) {
            exibirErro("Erro: " + e.getMessage());
        }
    }
}
```

## 3. EmprestimosController - Circulação

### Registrar Empréstimo
```java
@FXML
private void registrarEmprestimo() {
    int usuarioId = usuarioLogado.getId();
    int exemplarId = Integer.parseInt(exemplarField.getText());
    
    try {
        if (circulacaoService.registrarEmprestimo(usuarioId, exemplarId)) {
            exibirSucesso("Empréstimo registrado!");
            carregarEmprestimos();
        }
    } catch (IllegalArgumentException e) {
        exibirErro(e.getMessage());
    } catch (SQLException e) {
        exibirErro("Erro BD: " + e.getMessage());
    }
}

private void carregarEmprestimos() {
    try {
        List<Emprestimo> lista = emprestimoDAO.buscarAtivosDoUsuario(usuarioId);
        ObservableList<Emprestimo> data = FXCollections.observableArrayList(lista);
        emprestimosTable.setItems(data);
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### Registrar Devolução
```java
@FXML
private void registrarDevolucao() {
    Emprestimo selecionado = emprestimosTable.getSelectionModel().getSelectedItem();
    if (selecionado == null) {
        exibirErro("Selecione um empréstimo");
        return;
    }
    
    try {
        if (circulacaoService.registrarDevolucao(selecionado.getId())) {
            exibirSucesso("Devolução registrada!");
            // Mostrar multa se houver
            Multa multa = multaDAO.buscarPorEmprestimo(selecionado.getId());
            if (multa != null) {
                exibirSucesso("Multa gerada: Kz " + multa.getValor());
            }
            carregarEmprestimos();
        }
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### Renovar Empréstimo
```java
@FXML
private void renovarEmprestimo() {
    Emprestimo selecionado = emprestimosTable.getSelectionModel().getSelectedItem();
    if (selecionado == null) {
        exibirErro("Selecione um empréstimo");
        return;
    }
    
    try {
        if (circulacaoService.renovarEmprestimo(selecionado.getId())) {
            exibirSucesso("Empréstimo renovado!");
            carregarEmprestimos();
        }
    } catch (IllegalArgumentException e) {
        exibirErro(e.getMessage());
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

## 4. RelatoriosController - Geração de Relatórios

### Exportar PDF
```java
@FXML
private void exportarPDF() {
    try {
        RelatorioService relatorio = new RelatorioService();
        LocalDate inicio = dataInicioField.getValue();
        LocalDate fim = dataFimField.getValue();
        
        List<Emprestimo> emprestimos = relatorio.getEmprestimosPorPeriodo(inicio, fim);
        
        // Preparar dados
        String[][] dados = new String[emprestimos.size()][4];
        for (int i = 0; i < emprestimos.size(); i++) {
            Emprestimo e = emprestimos.get(i);
            dados[i][0] = e.getUsuario().getNome();
            dados[i][1] = e.getExemplar().getObra().getTitulo();
            dados[i][2] = e.getDataEmprestimo().toLocalDate().toString();
            dados[i][3] = e.getDataDevolucaoPrevista().toString();
        }
        
        String[] cabecalho = {"Usuário", "Obra", "Data Empréstimo", "Data Devolução"};
        PDFExporter.criarRelatorio("relatorio_emprestimos.pdf", 
            "Relatório de Empréstimos", 
            "Período: " + inicio + " a " + fim,
            cabecalho, dados);
        
        exibirSucesso("PDF gerado: relatorio_emprestimos.pdf");
    } catch (IOException e) {
        exibirErro("Erro ao gerar PDF: " + e.getMessage());
    } catch (SQLException e) {
        exibirErro("Erro BD: " + e.getMessage());
    }
}
```

### Exportar CSV
```java
@FXML
private void exportarCSV() {
    try {
        List<String[]> dados = new ArrayList<>();
        String[] cabecalho = {"Usuário", "Obra", "Data Empréstimo"};
        
        RelatorioService relatorio = new RelatorioService();
        List<Emprestimo> emprestimos = relatorio.getEmprestimosAtrasados();
        
        for (Emprestimo e : emprestimos) {
            String[] linha = {
                CSVExporter.escaparCSV(e.getUsuario().getNome()),
                CSVExporter.escaparCSV(e.getExemplar().getObra().getTitulo()),
                CSVExporter.formatarData(e.getDataEmprestimo().toLocalDate())
            };
            dados.add(linha);
        }
        
        CSVExporter.exportarCSV("relatorio_atrasados.csv", dados, cabecalho);
        exibirSucesso("CSV gerado: relatorio_atrasados.csv");
    } catch (IOException e) {
        exibirErro("Erro ao gerar CSV: " + e.getMessage());
    } catch (SQLException e) {
        exibirErro("Erro BD: " + e.getMessage());
    }
}
```

## 5. UtentesController - Gestão de Usuários

### Listar Usuários
```java
private void carregarUsuarios() {
    try {
        List<Usuario> lista = usuarioDAO.listarAtivos();
        ObservableList<Usuario> data = FXCollections.observableArrayList(lista);
        usuariosTable.setItems(data);
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### Criar Usuário
```java
@FXML
private void criarUsuario() {
    try {
        Usuario novo = new Usuario();
        novo.setNome(nomeField.getText());
        novo.setEmail(emailField.getText());
        novo.setSenha(senhaField.getText());
        novo.setPerfil(PerfilUsuario.ESTUDANTE);
        
        if (usuarioDAO.inserir(novo)) {
            exibirSucesso("Usuário criado!");
            carregarUsuarios();
        }
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

### Desativar Usuário
```java
@FXML
private void desativarUsuario() {
    Usuario selecionado = usuariosTable.getSelectionModel().getSelectedItem();
    if (selecionado == null) {
        exibirErro("Selecione um usuário");
        return;
    }
    
    try {
        if (usuarioDAO.desativar(selecionado.getId())) {
            exibirSucesso("Usuário desativado!");
            carregarUsuarios();
        }
    } catch (SQLException e) {
        exibirErro("Erro: " + e.getMessage());
    }
}
```

## 6. Padrão de Tratamento de Erros

```java
private void exibirErro(String mensagem) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Erro");
    alert.setHeaderText(null);
    alert.setContentText(mensagem);
    alert.showAndWait();
}

private void exibirSucesso(String mensagem) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Sucesso");
    alert.setHeaderText(null);
    alert.setContentText(mensagem);
    alert.showAndWait();
}
```

## 7. Padrão de Carregamento Assíncrono (para operações longas)

```java
private void carregarDadosAsync() {
    Task<List<Obra>> task = new Task<List<Obra>>() {
        @Override
        protected List<Obra> call() throws Exception {
            return obraDAO.listarTodos();
        }
    };
    
    task.setOnSucceeded(e -> {
        ObservableList<Obra> data = FXCollections.observableArrayList(task.getValue());
        livrosTable.setItems(data);
    });
    
    task.setOnFailed(e -> {
        exibirErro("Erro ao carregar dados");
    });
    
    new Thread(task).start();
}
```

## 8. Acesso ao Usuário Logado

```java
// No controller, obter usuário logado
AutenticacaoService authService = new AutenticacaoService();
Usuario usuarioLogado = authService.getUsuarioLogado();

// Usar para filtrar dados por usuário
List<Emprestimo> meusEmprestimos = emprestimoDAO.buscarAtivosDoUsuario(usuarioLogado.getId());
```

## Resumo de Padrões

1. **Inicializar DAOs/Services no initialize()**
2. **Sempre envolver em try-catch para SQLException**
3. **Validar seleção antes de operações**
4. **Usar FXCollections para atualizar TableViews**
5. **Mostrar mensagens ao usuário para feedback**
6. **Recarregar dados após modificações**
7. **Para operações longas, usar Task/Thread**

## Testes

Para testar cada funcionalidade:

1. **Login**: admin@biblioteca.ao / admin123
2. **Criar Obra**: Título, Autor, ISBN
3. **Emprestar**: Selecionar obra, clicar emprestar
4. **Devolver**: Selecionar empréstimo, devolver
5. **Gerar Relatório**: PDF e CSV

package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.dao.EmprestimoDAO;
import ao.co.imetro.sgbu.model.dao.UsuarioDAO;
import ao.co.imetro.sgbu.model.dao.ExemplarDAO;
import ao.co.imetro.sgbu.model.entity.Emprestimo;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.entity.Exemplar;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gestão de empréstimos
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class EmprestimosController {

    @FXML
    private TabPane tabPane;
    
    @FXML
    private Tab tabEmprestimosAtivos;
    
    @FXML
    private Tab tabHistorico;

    // Tab Empréstimos Ativos
    @FXML
    private TableView<Emprestimo> activeLoansTable;
    
    @FXML
    private TableColumn<Emprestimo, Integer> colIdAtivo;
    @FXML
    private TableColumn<Emprestimo, String> colUsuarioAtivo;
    @FXML
    private TableColumn<Emprestimo, String> colObraAtivo;
    @FXML
    private TableColumn<Emprestimo, LocalDate> colDataEmpAtivo;
    @FXML
    private TableColumn<Emprestimo, LocalDate> colDataDevAtivo;
    @FXML
    private TableColumn<Emprestimo, Integer> colRenovAtivo;
    
    @FXML
    private Button btnDevolver;
    @FXML
    private Button btnRenovar;
    @FXML
    private Button btnNovoEmprestimo;

    // Tab Histórico
    @FXML
    private TableView<Emprestimo> historyTable;
    
    @FXML
    private TableColumn<Emprestimo, Integer> colIdHistorico;
    @FXML
    private TableColumn<Emprestimo, String> colUsuarioHistorico;
    @FXML
    private TableColumn<Emprestimo, String> colObraHistorico;
    @FXML
    private TableColumn<Emprestimo, LocalDateTime> colDataEmpHistorico;
    @FXML
    private TableColumn<Emprestimo, LocalDate> colDataDevHistorico;

    private EmprestimoDAO emprestimoDAO;
    private UsuarioDAO usuarioDAO;
    private ExemplarDAO exemplarDAO;
    private ao.co.imetro.sgbu.model.service.CirculacaoService circulacaoService;
    private ObservableList<Emprestimo> activeLoansData;
    private ObservableList<Emprestimo> historyData;

    @FXML
    public void initialize() {
        emprestimoDAO = new EmprestimoDAO();
        usuarioDAO = new UsuarioDAO();
        exemplarDAO = new ExemplarDAO();
        circulacaoService = new ao.co.imetro.sgbu.model.service.CirculacaoService();
        
        // Configurar colunas de empréstimos ativos
        colIdAtivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsuarioAtivo.setCellValueFactory(cellData -> {
            try {
                Usuario u = usuarioDAO.buscarPorId(cellData.getValue().getUsuarioId());
                return new javafx.beans.property.SimpleStringProperty(u != null ? u.getNome() : "Desconhecido");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colObraAtivo.setCellValueFactory(cellData -> {
            try {
                Exemplar e = exemplarDAO.buscarPorId(cellData.getValue().getExemplarId());
                return new javafx.beans.property.SimpleStringProperty(e != null && e.getObra() != null ? e.getObra().getTitulo() : "Desconhecida");
            } catch (SQLException ex) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colDataEmpAtivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataEmprestimo().toLocalDate()));
        colDataDevAtivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataDevolucaoPrevista()));
        colRenovAtivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getRenovacoes()).asObject());
        
        // Configurar colunas de histórico
        colIdHistorico.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsuarioHistorico.setCellValueFactory(cellData -> {
            try {
                Usuario u = usuarioDAO.buscarPorId(cellData.getValue().getUsuarioId());
                return new javafx.beans.property.SimpleStringProperty(u != null ? u.getNome() : "Desconhecido");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colObraHistorico.setCellValueFactory(cellData -> {
            try {
                Exemplar e = exemplarDAO.buscarPorId(cellData.getValue().getExemplarId());
                return new javafx.beans.property.SimpleStringProperty(e != null && e.getObra() != null ? e.getObra().getTitulo() : "Desconhecida");
            } catch (SQLException ex) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colDataEmpHistorico.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataEmprestimo()));
        colDataDevHistorico.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataDevoluaoReal()));
        
        // Verificar permissões do usuário logado
        verificarPermissoes();
        
        // Listener para seleção em empréstimos ativos
        activeLoansTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                atualizarEstadoBotoes(newSelection != null);
            }
        );
        
        // Carregar dados
        loadEmprestimosAtivos();
        loadHistorico();
    }

    /**
     * Verifica se o usuário tem permissão para devolver/renovar
     */
    private boolean temPermissaoParaDevolver() {
        Usuario usuarioLogado = AutenticacaoService.getInstance().getUsuarioLogado();
        if (usuarioLogado == null) return false;
        
        PerfilUsuario perfil = usuarioLogado.getPerfil();
        return perfil == PerfilUsuario.ADMINISTRADOR || perfil == PerfilUsuario.BIBLIOTECARIO;
    }
    
    /**
     * Verifica permissões e configura estado inicial dos botões
     */
    private void verificarPermissoes() {
        boolean temPermissao = temPermissaoParaDevolver();
        
        if (!temPermissao) {
            // Ocultar botões para estudantes e docentes
            btnDevolver.setVisible(false);
            btnDevolver.setManaged(false);
            btnRenovar.setVisible(false);
            btnRenovar.setManaged(false);
            btnNovoEmprestimo.setVisible(false);
            btnNovoEmprestimo.setManaged(false);
        } else {
            // Desabilitar até selecionar
            btnDevolver.setDisable(true);
            btnRenovar.setDisable(true);
        }
    }
    
    /**
     * Atualiza o estado dos botões baseado na seleção e permissões
     */
    private void atualizarEstadoBotoes(boolean itemSelecionado) {
        boolean temPermissao = temPermissaoParaDevolver();
        
        // Botões só ficam habilitados se tiver item selecionado E permissão
        btnDevolver.setDisable(!itemSelecionado || !temPermissao);
        btnRenovar.setDisable(!itemSelecionado || !temPermissao);
    }

    /**
     * Ação: Novo Empréstimo
     */
    @FXML
    private void handleNovoEmprestimo(ActionEvent event) {
        Dialog<int[]> dialog = createEmprestimoDialog();
        Optional<int[]> result = dialog.showAndWait();
        
        result.ifPresent(ids -> {
            int usuarioId = ids[0];
            int exemplarId = ids[1];
            
            try {
                // Validar empréstimo antes de criar
                String erro = circulacaoService.validarEmprestimo(usuarioId, exemplarId);
                if (erro != null) {
                    showAlert(Alert.AlertType.ERROR, "Não é possível criar empréstimo", erro);
                    return;
                }
                
                // Registrar empréstimo usando o serviço (já aplica prazo por perfil)
                if (circulacaoService.registrarEmprestimo(usuarioId, exemplarId)) {
                    Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
                    int prazoDias = usuario.getPerfil().getPrazoDias();
                    LocalDate dataDevolucao = LocalDate.now().plusDays(prazoDias);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Empréstimo criado com sucesso!\n" +
                            "Perfil: " + usuario.getPerfil().getDescricao() + "\n" +
                            "Prazo: " + prazoDias + " dias\n" +
                            "Data de devolução: " + dataDevolucao);
                    loadEmprestimosAtivos();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao criar empréstimo.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao inserir empréstimo: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Validação", e.getMessage());
            }
        });
    }

    /**
     * Cria um dialog para entrada de dados de empréstimo
     */
    private Dialog<int[]> createEmprestimoDialog() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Novo Empréstimo");
        dialog.setHeaderText("Criar novo empréstimo");

        ComboBox<Usuario> cbUsuario = new ComboBox<>();
        Label lblUsuario = new Label("Usuário:");
        
        try {
            List<Usuario> usuarios = usuarioDAO.listarAtivos();
            
            // Verificar se o usuário logado é Admin ou Bibliotecário
            ao.co.imetro.sgbu.model.service.AutenticacaoService autenticacao = 
                ao.co.imetro.sgbu.model.service.AutenticacaoService.getInstance();
            Usuario usuarioLogado = autenticacao.getUsuarioLogado();
            
            if (usuarioLogado != null) {
                ao.co.imetro.sgbu.model.enums.PerfilUsuario perfil = usuarioLogado.getPerfil();
                
                // Se não for Admin ou Bibliotecário, mostrar apenas o usuário logado
                if (perfil != ao.co.imetro.sgbu.model.enums.PerfilUsuario.ADMINISTRADOR && 
                    perfil != ao.co.imetro.sgbu.model.enums.PerfilUsuario.BIBLIOTECARIO) {
                    
                    cbUsuario.setItems(FXCollections.observableArrayList(usuarioLogado));
                    cbUsuario.setValue(usuarioLogado);
                    cbUsuario.setDisable(true);
                    lblUsuario.setText("Usuário: " + usuarioLogado.getNome());
                } else {
                    // Admin/Bibliotecário pode selecionar qualquer usuário
                    cbUsuario.setItems(FXCollections.observableArrayList(usuarios));
                }
            } else {
                cbUsuario.setItems(FXCollections.observableArrayList(usuarios));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao carregar usuários: " + e.getMessage());
        }
        
        // StringConverter para exibir o texto no ComboBox
        javafx.util.StringConverter<Usuario> usuarioConverter = new javafx.util.StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario != null ? usuario.getNome() + " - " + usuario.getEmail() : "";
            }
            @Override
            public Usuario fromString(String string) {
                return null;
            }
        };
        
        cbUsuario.setConverter(usuarioConverter);
        cbUsuario.setPromptText("Selecione o usuário");
        cbUsuario.setPrefWidth(300);
        
        // ButtonCell para exibir o valor selecionado
        cbUsuario.setButtonCell(new javafx.scene.control.ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(usuarioConverter.toString(item));
                }
            }
        });
        
        ComboBox<Exemplar> cbExemplar = new ComboBox<>();
        try {
            List<Exemplar> exemplares = exemplarDAO.listarTodos();
            List<Exemplar> disponiveis = exemplares.stream()
                .filter(e -> e.getEstado() == ao.co.imetro.sgbu.model.enums.EstadoExemplar.DISPONIVEL)
                .toList();
            cbExemplar.setItems(FXCollections.observableArrayList(disponiveis));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao carregar exemplares: " + e.getMessage());
        }
        cbExemplar.setPromptText("Selecione o exemplar");
        cbExemplar.setPrefWidth(300);
        
        // StringConverter para exibir o texto no ComboBox
        javafx.util.StringConverter<Exemplar> exemplarConverter = new javafx.util.StringConverter<Exemplar>() {
            @Override
            public String toString(Exemplar exemplar) {
                return exemplar != null && exemplar.getObra() != null 
                    ? exemplar.getCodigoTombo() + " - " + exemplar.getObra().getTitulo()
                    : "";
            }
            @Override
            public Exemplar fromString(String s) { return null; }
        };
        
        cbExemplar.setConverter(exemplarConverter);
        
        // ButtonCell para exibir o valor selecionado
        cbExemplar.setButtonCell(new javafx.scene.control.ListCell<Exemplar>() {
            @Override
            protected void updateItem(Exemplar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(exemplarConverter.toString(item));
                }
            }
        });

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(10));
        grid.add(lblUsuario, 0, 0);
        grid.add(cbUsuario, 1, 0);
        grid.add(new Label("Exemplar:"), 0, 1);
        grid.add(cbExemplar, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (cbUsuario.getValue() == null || cbExemplar.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios", 
                        "Selecione usuário e exemplar!");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Retorna array com [usuarioId, exemplarId]
                return new int[] { cbUsuario.getValue().getId(), cbExemplar.getValue().getId() };
            }
            return null;
        });

        return dialog;
    }

    /**
     * Carrega os empréstimos ativos
     */
    private void loadEmprestimosAtivos() {
        try {
            // Obter usuário logado
            AutenticacaoService auth = AutenticacaoService.getInstance();
            Usuario usuarioLogado = auth.getUsuarioLogado();
            
            List<Emprestimo> emprestimos;
            
            if (usuarioLogado != null) {
                PerfilUsuario perfil = usuarioLogado.getPerfil();
                
                // Se for ESTUDANTE ou DOCENTE, mostrar apenas seus empréstimos
                if (perfil == PerfilUsuario.ESTUDANTE || perfil == PerfilUsuario.DOCENTE) {
                    emprestimos = emprestimoDAO.buscarPorUsuario(usuarioLogado.getId());
                } else {
                    // ADMIN e BIBLIOTECARIO veem todos
                    emprestimos = emprestimoDAO.listarTodos();
                }
            } else {
                // Fallback: se não houver usuário logado, mostrar todos
                emprestimos = emprestimoDAO.listarTodos();
            }
            
            // Filtrar apenas empréstimos ativos
            activeLoansData = FXCollections.observableArrayList(
                emprestimos.stream().filter(Emprestimo::isAtivo).toList()
            );
            activeLoansTable.setItems(activeLoansData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", 
                    "Erro ao carregar empréstimos ativos: " + e.getMessage());
        }
    }

    /**
     * Carrega o histórico de empréstimos (finalizados)
     */
    private void loadHistorico() {
        try {
            // Obter usuário logado
            AutenticacaoService auth = AutenticacaoService.getInstance();
            Usuario usuarioLogado = auth.getUsuarioLogado();
            
            List<Emprestimo> emprestimos;
            
            if (usuarioLogado != null) {
                PerfilUsuario perfil = usuarioLogado.getPerfil();
                
                // Se for ESTUDANTE ou DOCENTE, mostrar apenas seus empréstimos
                if (perfil == PerfilUsuario.ESTUDANTE || perfil == PerfilUsuario.DOCENTE) {
                    emprestimos = emprestimoDAO.buscarPorUsuario(usuarioLogado.getId());
                } else {
                    // ADMIN e BIBLIOTECARIO veem todos
                    emprestimos = emprestimoDAO.listarTodos();
                }
            } else {
                // Fallback: se não houver usuário logado, mostrar todos
                emprestimos = emprestimoDAO.listarTodos();
            }
            
            // Filtrar apenas empréstimos finalizados (não ativos ou com data de devolução)
            historyData = FXCollections.observableArrayList(
                emprestimos.stream().filter(e -> !e.isAtivo() || e.getDataDevoluaoReal() != null).toList()
            );
            historyTable.setItems(historyData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", 
                    "Erro ao carregar histórico: " + e.getMessage());
        }
    }

    /**
     * Ação: Devolver empréstimo
     */
    @FXML
    private void handleDevolver(ActionEvent event) {
        // Verificar permissão
        if (!temPermissaoParaDevolver()) {
            showAlert(Alert.AlertType.ERROR, "Acesso Negado", 
                    "Apenas Administradores e Bibliotecários podem devolver empréstimos.");
            return;
        }
        
        Emprestimo selectedEmprestimo = activeLoansTable.getSelectionModel().getSelectedItem();
        if (selectedEmprestimo == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione um empréstimo para devolver.");
            return;
        }

        try {
            // Criar diálogo customizado para devolução
            Dialog<ButtonType> devolucaoDialog = criarDialogDevolucao(selectedEmprestimo);
            Optional<ButtonType> result = devolucaoDialog.showAndWait();
            
            if (result.isPresent()) {
                ButtonType escolha = result.get();
                
                if (escolha.getText().equals("Devolução Normal")) {
                    processarDevolucaoNormal(selectedEmprestimo);
                } else if (escolha.getText().equals("Devolução Danificada")) {
                    processarDevolucaoDanificada(selectedEmprestimo);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                    "Erro ao devolver empréstimo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validação", e.getMessage());
        }
    }
    
    /**
     * Cria o diálogo de devolução com opções normal e danificada
     */
    private Dialog<ButtonType> criarDialogDevolucao(Emprestimo emprestimo) throws SQLException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Devolução de Empréstimo");
        dialog.setHeaderText("Escolha o tipo de devolução");
        
        // Calcular multa por atraso se houver
        double multaAtraso = circulacaoService.calcularMultaPendente(emprestimo.getId());
        
        String conteudo = "ID do Empréstimo: " + emprestimo.getId() + "\n";
        
        try {
            Usuario usuario = usuarioDAO.buscarPorId(emprestimo.getUsuarioId());
            Exemplar exemplar = exemplarDAO.buscarPorId(emprestimo.getExemplarId());
            
            conteudo += "Usuário: " + (usuario != null ? usuario.getNome() : "Desconhecido") + "\n";
            if (exemplar != null && exemplar.getObra() != null) {
                conteudo += "Obra: " + exemplar.getObra().getTitulo() + "\n";
            }
            conteudo += "Código Tombo: " + (exemplar != null ? exemplar.getCodigoTombo() : "N/A") + "\n\n";
        } catch (SQLException e) {
            conteudo += "\n";
        }
        
        if (multaAtraso > 0) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(
                emprestimo.getDataDevolucaoPrevista(), 
                LocalDate.now()
            );
            conteudo += "⚠️ ATENÇÃO: Empréstimo em atraso!\n";
            conteudo += "Dias de atraso: " + diasAtraso + "\n";
            conteudo += "Multa por atraso: Kz " + String.format("%.2f", multaAtraso) + "\n\n";
        }
        
        conteudo += "OPÇÕES:\n\n";
        conteudo += "🟢 Devolução Normal\n";
        conteudo += "   - Exemplar em bom estado\n";
        if (multaAtraso > 0) {
            conteudo += "   - Multa por atraso será aplicada\n";
        }
        conteudo += "\n🔴 Devolução Danificada\n";
        conteudo += "   - Exemplar com danos\n";
        conteudo += "   - Multa fixa de Kz 5000,00 por danos\n";
        if (multaAtraso > 0) {
            conteudo += "   - Multa por atraso também será aplicada\n";
        }
        
        dialog.setContentText(conteudo);
        
        ButtonType btnNormal = new ButtonType("Devolução Normal", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnDanificada = new ButtonType("Devolução Danificada", ButtonBar.ButtonData.OTHER);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(btnNormal, btnDanificada, btnCancelar);
        
        return dialog;
    }
    
    /**
     * Processa devolução normal
     */
    private void processarDevolucaoNormal(Emprestimo emprestimo) throws SQLException {
        double multaAtraso = circulacaoService.calcularMultaPendente(emprestimo.getId());
        
        if (circulacaoService.registrarDevolucao(emprestimo.getId())) {
            String sucesso = "Empréstimo devolvido com sucesso!";
            if (multaAtraso > 0) {
                sucesso += "\n\nMulta por atraso: Kz " + String.format("%.2f", multaAtraso);
            }
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", sucesso);
            loadEmprestimosAtivos();
            loadHistorico();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", 
                    "Erro ao devolver empréstimo.");
        }
    }
    
    /**
     * Processa devolução danificada (gera multa por danos)
     */
    private void processarDevolucaoDanificada(Emprestimo emprestimo) throws SQLException {
        double multaAtraso = circulacaoService.calcularMultaPendente(emprestimo.getId());
        double multaDanos = 5000.00; // Multa fixa por danos
        double multaTotal = multaAtraso + multaDanos;
        
        // Registrar devolução normal primeiro
        if (circulacaoService.registrarDevolucao(emprestimo.getId())) {
            // Criar multa por danos
            ao.co.imetro.sgbu.model.entity.Multa multaPorDanos = new ao.co.imetro.sgbu.model.entity.Multa(
                emprestimo.getUsuarioId(),
                emprestimo.getId(),
                multaDanos,
                ao.co.imetro.sgbu.model.enums.MotivoMulta.DANOS
            );
            
            ao.co.imetro.sgbu.model.dao.MultaDAO multaDAO = new ao.co.imetro.sgbu.model.dao.MultaDAO();
            multaDAO.inserir(multaPorDanos);
            
            // Atualizar estado do exemplar para DANIFICADO
            Exemplar exemplar = exemplarDAO.buscarPorId(emprestimo.getExemplarId());
            if (exemplar != null) {
                exemplarDAO.atualizarEstado(exemplar.getId(), 
                    ao.co.imetro.sgbu.model.enums.EstadoExemplar.DANIFICADO);
            }
            
            String mensagem = "Devolução danificada processada com sucesso!\n\n";
            if (multaAtraso > 0) {
                mensagem += "Multa por atraso: Kz " + String.format("%.2f", multaAtraso) + "\n";
            }
            mensagem += "Multa por danos: Kz " + String.format("%.2f", multaDanos) + "\n";
            mensagem += "Total de multas: Kz " + String.format("%.2f", multaTotal) + "\n\n";
            mensagem += "O exemplar foi marcado como DANIFICADO.";
            
            showAlert(Alert.AlertType.WARNING, "Devolução Danificada", mensagem);
            loadEmprestimosAtivos();
            loadHistorico();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", 
                    "Erro ao processar devolução danificada.");
        }
    }

    /**
     * Ação: Renovar empréstimo
     */
    @FXML
    private void handleRenovar(ActionEvent event) {
        // Verificar permissão
        if (!temPermissaoParaDevolver()) {
            showAlert(Alert.AlertType.ERROR, "Acesso Negado", 
                    "Apenas Administradores e Bibliotecários podem renovar empréstimos.");
            return;
        }
        
        Emprestimo selectedEmprestimo = activeLoansTable.getSelectionModel().getSelectedItem();
        if (selectedEmprestimo == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione um empréstimo para renovar.");
            return;
        }

        if (selectedEmprestimo.getRenovacoes() >= 2) {
            showAlert(Alert.AlertType.WARNING, "Limite de Renovações", 
                    "Este empréstimo já atingiu o limite de 2 renovações.");
            return;
        }

        try {
            // Usar o serviço de circulação (valida se há reserva)
            if (circulacaoService.renovarEmprestimo(selectedEmprestimo.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                        "Empréstimo renovado por mais 7 dias!\n" +
                        "Nova data de devolução: " + selectedEmprestimo.getDataDevolucaoPrevista().plusDays(7));
                loadEmprestimosAtivos();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", 
                        "Erro ao renovar empréstimo.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                    "Erro ao renovar empréstimo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Não é possível renovar", e.getMessage());
        }
    }

    /**
     * Mostra um alerta na tela
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }
}

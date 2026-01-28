package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.model.dao.ReservaDAO;
import ao.co.imetro.sgbu.model.dao.UsuarioDAO;
import ao.co.imetro.sgbu.model.dao.ExemplarDAO;
import ao.co.imetro.sgbu.model.dao.EmprestimoDAO;
import ao.co.imetro.sgbu.model.entity.Reserva;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.entity.Exemplar;
import ao.co.imetro.sgbu.model.entity.Emprestimo;
import ao.co.imetro.sgbu.model.enums.StatusReserva;
import ao.co.imetro.sgbu.model.enums.EstadoExemplar;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gestão de reservas de obras
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class ReservasController {

    @FXML
    private TableView<Reserva> reservasTable;

    @FXML
    private TableColumn<Reserva, Integer> colId;
    @FXML
    private TableColumn<Reserva, String> colUsuarioId;
    @FXML
    private TableColumn<Reserva, String> colExemplarId;
    @FXML
    private TableColumn<Reserva, LocalDateTime> colData;
    @FXML
    private TableColumn<Reserva, Integer> colPosicaoFila;
    @FXML
    private TableColumn<Reserva, String> colStatus;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnNovaReserva;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnConfirmar;
    @FXML
    private ComboBox<String> filterStatus;

    private ReservaDAO reservaDAO;
    private UsuarioDAO usuarioDAO;
    private ExemplarDAO exemplarDAO;
    private EmprestimoDAO emprestimoDAO;
    private ObservableList<Reserva> reservasData;

    @FXML
    public void initialize() {
        reservaDAO = new ReservaDAO();
        usuarioDAO = new UsuarioDAO();
        exemplarDAO = new ExemplarDAO();
        emprestimoDAO = new EmprestimoDAO();
        
        // Configurar colunas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        
        // Coluna Usuário - mostrar nome e email
        colUsuarioId.setCellValueFactory(cellData -> {
            try {
                Usuario u = usuarioDAO.buscarPorId(cellData.getValue().getUsuarioId());
                return new javafx.beans.property.SimpleStringProperty(
                    u != null ? u.getNome() + " (" + u.getEmail() + ")" : "ID: " + cellData.getValue().getUsuarioId()
                );
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        
        // Coluna Exemplar - mostrar código tombo e obra
        colExemplarId.setCellValueFactory(cellData -> {
            try {
                Exemplar ex = exemplarDAO.buscarPorId(cellData.getValue().getExemplarId());
                if (ex != null && ex.getObra() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        ex.getCodigoTombo() + " - " + ex.getObra().getTitulo()
                    );
                }
                return new javafx.beans.property.SimpleStringProperty(
                    ex != null ? ex.getCodigoTombo() : "ID: " + cellData.getValue().getExemplarId()
                );
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colData.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataReserva()));
        colPosicaoFila.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPosicaoFila()).asObject());
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        // Configurar permissões baseadas no perfil do usuário
        configurarPermissoes();
        
        // Desabilitar botões até selecionar
        btnCancelar.setDisable(true);
        btnConfirmar.setDisable(true);
        
        // Listener para seleção
        reservasTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                Usuario usuarioLogado = AutenticacaoService.getInstance().getUsuarioLogado();
                boolean isAdmin = usuarioLogado != null && 
                    (usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR || 
                     usuarioLogado.getPerfil() == PerfilUsuario.BIBLIOTECARIO);
                
                boolean isSelected = newSelection != null;
                
                // Apenas ADMIN e BIBLIOTECARIO podem usar os botões de ação
                if (isAdmin) {
                    btnCancelar.setDisable(!isSelected);
                    btnConfirmar.setDisable(!isSelected);
                }
            }
        );
        
        // Configurar filtro de status
        filterStatus.setItems(FXCollections.observableArrayList("Todos", "ATIVA", "ATENDIDA", "CANCELADA"));
        filterStatus.setValue("Todos");
        filterStatus.setOnAction(e -> applyFilters());
        
        // Carregar dados
        loadReservas();
    }
    
    /**
     * Configura as permissões de interface baseadas no perfil do usuário logado
     */
    private void configurarPermissoes() {
        AutenticacaoService auth = AutenticacaoService.getInstance();
        Usuario usuarioLogado = auth.getUsuarioLogado();
        
        if (usuarioLogado != null) {
            PerfilUsuario perfil = usuarioLogado.getPerfil();
            
            // Docentes e Alunos: apenas visualização (ocultar botões de ação)
            if (perfil == PerfilUsuario.ESTUDANTE || perfil == PerfilUsuario.DOCENTE) {
                btnNovaReserva.setVisible(false);
                btnNovaReserva.setManaged(false);
                btnConfirmar.setVisible(false);
                btnConfirmar.setManaged(false);
                btnCancelar.setVisible(false);
                btnCancelar.setManaged(false);
            }
        }
    }

    /**
     * Carrega a lista de reservas da base de dados
     * Se o usuário for estudante ou docente, mostra apenas suas reservas
     * Se for admin ou bibliotecário, mostra todas
     */
    private void loadReservas() {
        try {
            // Obter usuário logado
            AutenticacaoService auth = AutenticacaoService.getInstance();
            Usuario usuarioLogado = auth.getUsuarioLogado();
            
            List<Reserva> reservas;
            
            if (usuarioLogado != null) {
                PerfilUsuario perfil = usuarioLogado.getPerfil();
                
                // Se for estudante ou docente, mostrar apenas suas reservas
                if (perfil == PerfilUsuario.ESTUDANTE || perfil == PerfilUsuario.DOCENTE) {
                    reservas = reservaDAO.buscarPorUsuario(usuarioLogado.getId());
                } else {
                    // Admin e Bibliotecário veem todas
                    reservas = reservaDAO.listarTodos();
                }
            } else {
                // Se não houver usuário logado, mostrar todas (fallback)
                reservas = reservaDAO.listarTodos();
            }
            
            reservasData = FXCollections.observableArrayList(reservas);
            reservasTable.setItems(reservasData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Reservas", 
                    "Erro ao carregar lista de reservas: " + e.getMessage());
        }
    }

    /**
     * Ação: Nova Reserva
     */
    @FXML
    private void handleNovaReserva(ActionEvent event) {
        Dialog<Reserva> dialog = createReservaDialog();
        Optional<Reserva> result = dialog.showAndWait();
        
        result.ifPresent(reserva -> {
            try {
                if (reservaDAO.inserir(reserva)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Reserva criada com sucesso!");
                    loadReservas();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao criar reserva.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao inserir reserva: " + e.getMessage());
            }
        });
    }

    /**
     * Ação: Cancelar Reserva
     */
    @FXML
    private void handleCancelar() {
        Reserva selectedReserva = reservasTable.getSelectionModel().getSelectedItem();
        if (selectedReserva == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione uma reserva para cancelar.");
            return;
        }

        if (StatusReserva.CANCELADA.equals(selectedReserva.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Reserva já Cancelada", 
                    "Esta reserva já foi cancelada.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Cancelamento");
        confirmDialog.setHeaderText("Cancelar Reserva");
        confirmDialog.setContentText("Deseja cancelar esta reserva?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedReserva.setStatus(StatusReserva.CANCELADA);
                if (reservaDAO.atualizar(selectedReserva)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Reserva cancelada com sucesso!");
                    loadReservas();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao cancelar reserva.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao atualizar reserva: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Confirmar Reserva - Cria empréstimo automaticamente
     */
    @FXML
    private void handleConfirmar() {
        Reserva selectedReserva = reservasTable.getSelectionModel().getSelectedItem();
        if (selectedReserva == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione uma reserva para confirmar.");
            return;
        }

        if (!StatusReserva.ATIVA.equals(selectedReserva.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Status Inválido", 
                    "Apenas reservas ativas podem ser confirmadas.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Levantamento");
        confirmDialog.setHeaderText("Confirmar Levantamento e Criar Empréstimo");
        confirmDialog.setContentText("Deseja confirmar o levantamento? Um empréstimo será criado automaticamente.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 1. Buscar o usuário para pegar prazo de dias
                Usuario usuario = usuarioDAO.buscarPorId(selectedReserva.getUsuarioId());
                if (usuario == null) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Usuário não encontrado.");
                    return;
                }
                
                // 2. Verificar se o exemplar está disponível
                Exemplar exemplar = exemplarDAO.buscarPorId(selectedReserva.getExemplarId());
                if (exemplar == null) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Exemplar não encontrado.");
                    return;
                }
                
                if (exemplar.getEstado() != EstadoExemplar.DISPONIVEL) {
                    showAlert(Alert.AlertType.WARNING, "Exemplar Não Disponível", 
                        "O exemplar não está disponível no momento. Estado atual: " + exemplar.getEstado());
                    return;
                }
                
                // 3. Criar o empréstimo
                LocalDate dataDevolucao = LocalDate.now().plusDays(usuario.getPrazoDias());
                Emprestimo emprestimo = new Emprestimo(
                    selectedReserva.getUsuarioId(),
                    selectedReserva.getExemplarId(),
                    dataDevolucao
                );
                
                if (emprestimoDAO.inserir(emprestimo)) {
                    // 4. Atualizar estado do exemplar para EMPRESTADO
                    exemplar.setEstado(EstadoExemplar.EMPRESTADO);
                    exemplarDAO.atualizar(exemplar);
                    
                    // 5. Marcar reserva como ATENDIDA
                    selectedReserva.setStatus(StatusReserva.ATENDIDA);
                    selectedReserva.setDataAtendimento(LocalDate.now());
                    reservaDAO.atualizar(selectedReserva);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Reserva confirmada e empréstimo criado com sucesso!\n" +
                            "Data de devolução: " + dataDevolucao.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    loadReservas();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao criar empréstimo.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao processar confirmação: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Pesquisar em tempo real
     */
    @FXML
    private void handleSearch(KeyEvent event) {
        applyFilters();
    }

    /**
     * Aplica filtros de pesquisa e status
     */
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String statusFilter = filterStatus.getValue();
        
        ObservableList<Reserva> filtered = FXCollections.observableArrayList();
        
        for (Reserva reserva : reservasData) {
            boolean matchesSearch = searchText.isEmpty() || 
                    String.valueOf(reserva.getUsuarioId()).contains(searchText) ||
                    String.valueOf(reserva.getExemplarId()).contains(searchText);
            
            boolean matchesStatus = "Todos".equals(statusFilter) || 
                    statusFilter.equals(reserva.getStatus().toString());
            
            if (matchesSearch && matchesStatus) {
                filtered.add(reserva);
            }
        }
        
        reservasTable.setItems(filtered);
    }

    /**
     * Cria um dialog para entrada de dados de reserva
     */
    private Dialog<Reserva> createReservaDialog() {
        Dialog<Reserva> dialog = new Dialog<>();
        dialog.setTitle("Nova Reserva");
        dialog.setHeaderText("Criar nova reserva de exemplar");

        // ComboBox para Usuário (com nome e email)
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
                    
                    // Mostrar apenas o usuário logado (como texto, não ComboBox)
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
        
        cbUsuario.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario != null ? usuario.getNome() + " - " + usuario.getEmail() : "";
            }
            @Override
            public Usuario fromString(String string) {
                return null;
            }
        });
        cbUsuario.setPromptText("Selecione o usuário");
        cbUsuario.setPrefWidth(400);
        
        // ComboBox para Exemplar - mostrar exemplares disponíveis ou emprestados
        ComboBox<Exemplar> cbExemplar = new ComboBox<>();
        try {
            List<Exemplar> exemplares = exemplarDAO.listarTodos();
            // Filtrar apenas disponíveis ou emprestados (pode fazer reserva)
            List<Exemplar> exemplaresDisponiveis = new java.util.ArrayList<>();
            for (Exemplar ex : exemplares) {
                if (ex.getEstado() == EstadoExemplar.DISPONIVEL || 
                    ex.getEstado() == EstadoExemplar.EMPRESTADO) {
                    exemplaresDisponiveis.add(ex);
                }
            }
            cbExemplar.setItems(FXCollections.observableArrayList(exemplaresDisponiveis));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao carregar exemplares: " + e.getMessage());
        }
        
        cbExemplar.setConverter(new StringConverter<Exemplar>() {
            @Override
            public String toString(Exemplar exemplar) {
                if (exemplar != null && exemplar.getObra() != null) {
                    return exemplar.getCodigoTombo() + " - " + exemplar.getObra().getTitulo() + 
                           " (" + exemplar.getEstado() + ")";
                }
                return exemplar != null ? exemplar.getCodigoTombo() : "";
            }
            @Override
            public Exemplar fromString(String string) {
                return null;
            }
        });
        cbExemplar.setPromptText("Selecione o exemplar");
        cbExemplar.setPrefWidth(400);
        
        // DatePicker para Data da Reserva
        DatePicker dpDataReserva = new DatePicker();
        dpDataReserva.setValue(java.time.LocalDate.now());
        dpDataReserva.setPrefWidth(400);
        
        // Label para mostrar a posição na fila (readonly)
        Label lblPosicaoFila = new Label("Será calculada automaticamente");
        lblPosicaoFila.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");
        
        // Label para mostrar quantidade de exemplares disponíveis
        Label lblQuantidade = new Label();
        lblQuantidade.setStyle("-fx-font-weight: bold;");
        
        // Atualizar quantidade quando selecionar exemplar
        cbExemplar.setOnAction(e -> {
            Exemplar selected = cbExemplar.getValue();
            if (selected != null && selected.getObra() != null) {
                try {
                    int disponiveis = exemplarDAO.contarDisponiveisPorObra(selected.getObraId());
                    int total = exemplarDAO.contarPorObra(selected.getObraId());
                    lblQuantidade.setText(String.format("Disponíveis: %d de %d exemplares", disponiveis, total));
                } catch (SQLException ex) {
                    lblQuantidade.setText("Erro ao buscar quantidade");
                }
            } else {
                lblQuantidade.setText("");
            }
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Usuário:"), 0, 0);
        grid.add(cbUsuario, 1, 0);
        grid.add(new Label("Exemplar:"), 0, 1);
        grid.add(cbExemplar, 1, 1);
        grid.add(lblQuantidade, 1, 2);
        grid.add(new Label("Data da Reserva:"), 0, 3);
        grid.add(dpDataReserva, 1, 3);
        grid.add(new Label("Posição na Fila:"), 0, 4);
        grid.add(lblPosicaoFila, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Validação
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (cbUsuario.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", 
                        "Selecione um usuário!");
                event.consume();
                return;
            }
            
            if (cbExemplar.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", 
                        "Selecione um exemplar!");
                event.consume();
                return;
            }
            
            // Validar se exemplar está disponível - se estiver, não permitir reserva
            Exemplar exemplarSelecionado = cbExemplar.getValue();
            if (exemplarSelecionado.getEstado() == EstadoExemplar.DISPONIVEL) {
                showAlert(Alert.AlertType.WARNING, "Exemplar Disponível", 
                    "Este exemplar está disponível. Realize um empréstimo ao invés de uma reserva.");
                event.consume();
                return;
            }
            
            // Validar renovação - verificar se já existe reserva ativa do mesmo usuário para este exemplar
            try {
                Reserva reservaExistente = reservaDAO.buscarReservaAtiva(
                    cbUsuario.getValue().getId(), 
                    cbExemplar.getValue().getId()
                );
                
                if (reservaExistente != null) {
                    // Usuário já tem reserva ativa para este exemplar - é uma tentativa de renovação
                    // Verificar se existe reserva ativa de outro usuário
                    boolean existeOutraReserva = reservaDAO.existeReservaAtivaDeOutroUsuario(
                        cbExemplar.getValue().getId(),
                        cbUsuario.getValue().getId()
                    );
                    
                    if (existeOutraReserva) {
                        showAlert(Alert.AlertType.ERROR, "Renovação Não Permitida", 
                            "Não é possível renovar a reserva porque existem outras reservas ativas para este exemplar.");
                        event.consume();
                        return;
                    }
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao validar reserva: " + e.getMessage());
                event.consume();
                return;
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Reserva reserva = new Reserva();
                reserva.setUsuarioId(cbUsuario.getValue().getId());
                reserva.setExemplarId(cbExemplar.getValue().getId());
                reserva.setDataReserva(dpDataReserva.getValue().atStartOfDay());
                reserva.setStatus(StatusReserva.ATIVA); // Status sempre ATIVA por default
                
                // Calcular posição na fila automaticamente
                try {
                    int proximaPosicao = reservaDAO.calcularProximaPosicaoFila(cbExemplar.getValue().getId());
                    reserva.setPosicaoFila(proximaPosicao);
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao calcular posição na fila: " + e.getMessage());
                    reserva.setPosicaoFila(1);
                }
                
                return reserva;
            }
            return null;
        });

        return dialog;
    }

    /**
     * Mostra um alerta na tela
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

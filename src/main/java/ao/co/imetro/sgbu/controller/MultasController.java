package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.dao.MultaDAO;
import ao.co.imetro.sgbu.model.dao.UsuarioDAO;
import ao.co.imetro.sgbu.model.entity.Multa;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.enums.StatusMulta;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gestão de multas de utilizadores
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class MultasController {

    @FXML
    private TableView<Multa> multasTable;

    @FXML
    private TableColumn<Multa, Integer> colId;
    @FXML
    private TableColumn<Multa, String> colUsuario;
    @FXML
    private TableColumn<Multa, String> colMotivo;
    @FXML
    private TableColumn<Multa, Double> colValor;
    @FXML
    private TableColumn<Multa, String> colStatus;
    @FXML
    private TableColumn<Multa, Integer> colDiasAtraso;
    @FXML
    private TableColumn<Multa, LocalDate> colDataGeracao;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnMarcarPaga;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnRemover;
    @FXML
    private ComboBox<String> filterStatus;

    private MultaDAO multaDAO;
    private UsuarioDAO usuarioDAO;
    private ObservableList<Multa> multasData;

    @FXML
    public void initialize() {
        multaDAO = new MultaDAO();
        usuarioDAO = new UsuarioDAO();
        
        // Configurar colunas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsuario.setCellValueFactory(cellData -> {
            try {
                Usuario u = usuarioDAO.buscarPorId(cellData.getValue().getUsuarioId());
                return new javafx.beans.property.SimpleStringProperty(u != null ? u.getNome() : "Desconhecido");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colMotivo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMotivo() != null ? cellData.getValue().getMotivo().getDescricao() : "Atraso"
            )
        );
        colValor.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValor()).asObject());
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        colDiasAtraso.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getDiasAtraso()).asObject());
        colDataGeracao.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataGeracao().toLocalDate()));
        
        // Configurar permissões baseadas no perfil do usuário
        configurarPermissoes();
        
        // Desabilitar botões até selecionar
        btnMarcarPaga.setDisable(true);
        btnCancelar.setDisable(true);
        btnRemover.setDisable(true);
        
        // Listener para seleção
        multasTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                Usuario usuarioLogado = AutenticacaoService.getInstance().getUsuarioLogado();
                boolean isAdmin = usuarioLogado != null && 
                    (usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR || 
                     usuarioLogado.getPerfil() == PerfilUsuario.BIBLIOTECARIO);
                
                boolean isSelected = newSelection != null;
                
                // Apenas ADMIN e BIBLIOTECARIO podem usar os botões de ação
                if (isAdmin) {
                    btnMarcarPaga.setDisable(!isSelected || (newSelection != null && newSelection.getStatus() != StatusMulta.ABERTA));
                    btnCancelar.setDisable(!isSelected || (newSelection != null && newSelection.getStatus() != StatusMulta.ABERTA));
                    btnRemover.setDisable(!isSelected);
                }
            }
        );
        
        // Configurar filtro de status
        filterStatus.setItems(FXCollections.observableArrayList("Todos", "ABERTA", "PAGA", "CANCELADA"));
        filterStatus.setValue("Todos");
        filterStatus.setOnAction(e -> applyFilters());
        
        // Carregar dados
        loadMultas();
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
                btnMarcarPaga.setVisible(false);
                btnMarcarPaga.setManaged(false);
                btnCancelar.setVisible(false);
                btnCancelar.setManaged(false);
                btnRemover.setVisible(false);
                btnRemover.setManaged(false);
            }
        }
    }

    /**
     * Carrega a lista de multas da base de dados
     * Estudantes e docentes veem apenas suas próprias multas
     * Admin e bibliotecário veem todas
     */
    private void loadMultas() {
        try {
            // Obter usuário logado
            AutenticacaoService auth = AutenticacaoService.getInstance();
            Usuario usuarioLogado = auth.getUsuarioLogado();
            
            List<Multa> multas;
            
            if (usuarioLogado != null) {
                PerfilUsuario perfil = usuarioLogado.getPerfil();
                
                // Se for estudante ou docente, mostrar apenas suas multas
                if (perfil == PerfilUsuario.ESTUDANTE || perfil == PerfilUsuario.DOCENTE) {
                    multas = multaDAO.buscarPorUsuario(usuarioLogado.getId());
                } else {
                    // Admin e Bibliotecário veem todas
                    multas = multaDAO.listarTodos();
                }
            } else {
                // Se não houver usuário logado, mostrar todas (fallback)
                multas = multaDAO.listarTodos();
            }
            
            multasData = FXCollections.observableArrayList(multas);
            multasTable.setItems(multasData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Multas", 
                    "Erro ao carregar lista de multas: " + e.getMessage());
        }
    }

    /**
     * Ação: Marcar multa como paga
     */
    @FXML
    private void handleMarcarPaga() {
        Multa selectedMulta = multasTable.getSelectionModel().getSelectedItem();
        if (selectedMulta == null || selectedMulta.getStatus() != StatusMulta.ABERTA) {
            showAlert(Alert.AlertType.WARNING, "Operação Inválida", 
                    "Selecione uma multa aberta para marcar como paga.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Pagamento");
        confirmDialog.setHeaderText("Marcar Multa como Paga");
        confirmDialog.setContentText(String.format("Registrar pagamento de %.2f Kz?", selectedMulta.getValor()));

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedMulta.setStatus(StatusMulta.PAGA);
                selectedMulta.setDataPagamento(LocalDate.now());
                if (multaDAO.atualizar(selectedMulta)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Multa marcada como paga com sucesso!");
                    loadMultas();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao marcar multa como paga.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao atualizar multa: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Cancelar multa
     */
    @FXML
    private void handleCancelar() {
        Multa selectedMulta = multasTable.getSelectionModel().getSelectedItem();
        if (selectedMulta == null || selectedMulta.getStatus() != StatusMulta.ABERTA) {
            showAlert(Alert.AlertType.WARNING, "Operação Inválida", 
                    "Selecione uma multa aberta para cancelar.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Cancelamento");
        confirmDialog.setHeaderText("Cancelar Multa");
        confirmDialog.setContentText("Tem certeza que deseja cancelar esta multa?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedMulta.setStatus(StatusMulta.CANCELADA);
                if (multaDAO.atualizar(selectedMulta)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Multa cancelada!");
                    loadMultas();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao cancelar multa.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao cancelar multa: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Remover Multa
     */
    @FXML
    private void handleRemover() {
        Multa selectedMulta = multasTable.getSelectionModel().getSelectedItem();
        if (selectedMulta == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione uma multa para remover.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Remoção");
        confirmDialog.setHeaderText("Remover Multa");
        confirmDialog.setContentText("Tem certeza que deseja remover esta multa?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (multaDAO.deletar(selectedMulta.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Multa removida com sucesso!");
                    loadMultas();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao remover multa.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao remover multa: " + e.getMessage());
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
        
        ObservableList<Multa> filtered = FXCollections.observableArrayList();
        
        for (Multa multa : multasData) {
            try {
                Usuario u = usuarioDAO.buscarPorId(multa.getUsuarioId());
                boolean matchesSearch = searchText.isEmpty() || 
                        (u != null && u.getNome().toLowerCase().contains(searchText)) ||
                        String.valueOf(multa.getValor()).contains(searchText);
                
                boolean matchesStatus = "Todos".equals(statusFilter) || 
                        statusFilter.equals(multa.getStatus().toString());
                
                if (matchesSearch && matchesStatus) {
                    filtered.add(multa);
                }
            } catch (SQLException e) {
                // Ignorar erro
            }
        }
        
        multasTable.setItems(filtered);
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

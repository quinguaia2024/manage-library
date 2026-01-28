package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.dao.ExemplarDAO;
import ao.co.imetro.sgbu.model.dao.ObraDAO;
import ao.co.imetro.sgbu.model.entity.Exemplar;
import ao.co.imetro.sgbu.model.entity.Obra;
import ao.co.imetro.sgbu.model.enums.EstadoExemplar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gestão de exemplares
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class ExemplaresController {

    @FXML
    private TableView<Exemplar> exemplaresTable;

    @FXML
    private TableColumn<Exemplar, Integer> colId;
    @FXML
    private TableColumn<Exemplar, String> colObra;
    @FXML
    private TableColumn<Exemplar, String> colCodigoTombo;
    @FXML
    private TableColumn<Exemplar, String> colEstado;
    @FXML
    private TableColumn<Exemplar, String> colLocalizacao;
    @FXML
    private TableColumn<Exemplar, LocalDate> colDataAquisicao;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnNovoExemplar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnRemover;

    private ExemplarDAO exemplarDAO;
    private ObraDAO obraDAO;
    private ObservableList<Exemplar> exemplaresData;

    @FXML
    public void initialize() {
        exemplarDAO = new ExemplarDAO();
        obraDAO = new ObraDAO();
        
        // Configurar colunas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colObra.setCellValueFactory(cellData -> {
            try {
                Obra obra = obraDAO.buscarPorId(cellData.getValue().getObraId());
                return new javafx.beans.property.SimpleStringProperty(obra != null ? obra.getTitulo() : "Desconhecida");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });
        colCodigoTombo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodigoTombo()));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().name()));
        colLocalizacao.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocalizacao() != null ? cellData.getValue().getLocalizacao() : ""));
        colDataAquisicao.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataAquisicao()));
        
        // Desabilitar botões até selecionar
        btnEditar.setDisable(true);
        btnRemover.setDisable(true);
        
        // Listener para seleção
        exemplaresTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean isSelected = newSelection != null;
                btnEditar.setDisable(!isSelected);
                btnRemover.setDisable(!isSelected);
            }
        );
        
        // Carregar dados
        loadExemplares();
    }

    /**
     * Carrega a lista de exemplares da base de dados
     */
    private void loadExemplares() {
        try {
            List<Exemplar> exemplares = exemplarDAO.listarTodos();
            exemplaresData = FXCollections.observableArrayList(exemplares);
            exemplaresTable.setItems(exemplaresData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Exemplares", 
                    "Erro ao carregar lista de exemplares: " + e.getMessage());
        }
    }

    /**
     * Ação: Novo Exemplar
     */
    @FXML
    private void handleNovoExemplar(ActionEvent event) {
        DialogResult dialogResult = createExemplarDialog(null);
        if (dialogResult == null) return;
        
        Optional<ButtonType> result = dialogResult.dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Validar campos
            if (dialogResult.cbObra.getValue() == null || dialogResult.tfCodigoTombo.getText().isEmpty() 
                || dialogResult.cbEstado.getValue() == null || dialogResult.spQuantidade.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios", 
                        "Preencha todos os campos obrigatórios!");
                return;
            }
            
            int quantidade = dialogResult.spQuantidade.getValue();
            String codigoBase = dialogResult.tfCodigoTombo.getText();
            int successCount = 0;
            
            try {
                // Criar múltiplos exemplares
                for (int i = 0; i < quantidade; i++) {
                    Exemplar exemplar = new Exemplar();
                    exemplar.setObraId(dialogResult.cbObra.getValue().getId());
                    
                    // Gerar código único para cada exemplar
                    String codigoTombo = quantidade > 1 ? codigoBase + "-" + (i + 1) : codigoBase;
                    exemplar.setCodigoTombo(codigoTombo);
                    
                    exemplar.setEstado(dialogResult.cbEstado.getValue());
                    exemplar.setLocalizacao(dialogResult.tfLocalizacao.getText());
                    exemplar.setDataAquisicao(dialogResult.dpDataAquisicao.getValue());
                    
                    if (exemplarDAO.inserir(exemplar)) {
                        successCount++;
                    }
                }
                
                if (successCount > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            successCount + " exemplar(es) cadastrado(s) com sucesso!");
                    loadExemplares();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao cadastrar exemplares.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao inserir exemplares: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Editar Exemplar
     */
    @FXML
    private void handleEditar(ActionEvent event) {
        Exemplar selectedExemplar = exemplaresTable.getSelectionModel().getSelectedItem();
        if (selectedExemplar == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione um exemplar para editar.");
            return;
        }

        DialogResult dialogResult = createExemplarDialog(selectedExemplar);
        if (dialogResult == null) return;
        
        // Esconder campo de quantidade na edição
        dialogResult.spQuantidade.setVisible(false);
        dialogResult.spQuantidade.setManaged(false);
        
        Optional<ButtonType> result = dialogResult.dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Validar campos
            if (dialogResult.cbObra.getValue() == null || dialogResult.tfCodigoTombo.getText().isEmpty() 
                || dialogResult.cbEstado.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios", 
                        "Preencha todos os campos obrigatórios!");
                return;
            }
            
            selectedExemplar.setObraId(dialogResult.cbObra.getValue().getId());
            selectedExemplar.setCodigoTombo(dialogResult.tfCodigoTombo.getText());
            selectedExemplar.setEstado(dialogResult.cbEstado.getValue());
            selectedExemplar.setLocalizacao(dialogResult.tfLocalizacao.getText());
            selectedExemplar.setDataAquisicao(dialogResult.dpDataAquisicao.getValue());
            
            try {
                if (exemplarDAO.atualizar(selectedExemplar)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Exemplar atualizado com sucesso!");
                    loadExemplares();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao atualizar exemplar.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao atualizar exemplar: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Remover Exemplar
     */
    @FXML
    private void handleRemover(ActionEvent event) {
        Exemplar selectedExemplar = exemplaresTable.getSelectionModel().getSelectedItem();
        if (selectedExemplar == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione um exemplar para remover.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Remoção");
        confirmDialog.setHeaderText("Remover Exemplar");
        confirmDialog.setContentText("Tem certeza que deseja remover o exemplar \"" + selectedExemplar.getCodigoTombo() + "\"?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (exemplarDAO.deletar(selectedExemplar.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Exemplar removido com sucesso!");
                    loadExemplares();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao remover exemplar.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao remover exemplar: " + e.getMessage());
            }
        }
    }

    /**
     * Ação: Pesquisar em tempo real
     */
    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            exemplaresTable.setItems(exemplaresData);
        } else {
            ObservableList<Exemplar> filtered = FXCollections.observableArrayList();
            for (Exemplar exemplar : exemplaresData) {
                if (exemplar.getCodigoTombo().toLowerCase().contains(searchText) ||
                    exemplar.getLocalizacao().toLowerCase().contains(searchText)) {
                    filtered.add(exemplar);
                }
            }
            exemplaresTable.setItems(filtered);
        }
    }

    /**
     * Classe auxiliar para retornar múltiplos componentes do diálogo
     */
    private static class DialogResult {
        Dialog<ButtonType> dialog;
        ComboBox<Obra> cbObra;
        TextField tfCodigoTombo;
        ComboBox<EstadoExemplar> cbEstado;
        TextField tfLocalizacao;
        DatePicker dpDataAquisicao;
        Spinner<Integer> spQuantidade;
    }

    /**
     * Cria um dialog para entrada de dados de exemplar
     */
    private DialogResult createExemplarDialog(Exemplar exemplar) {
        DialogResult result = new DialogResult();
        
        result.dialog = new Dialog<>();
        result.dialog.setTitle(exemplar == null ? "Novo Exemplar" : "Editar Exemplar");
        result.dialog.setHeaderText(exemplar == null ? "Cadastrar novo exemplar" : "Editar dados do exemplar");

        // Criar campos
        result.cbObra = new ComboBox<>();
        try {
            List<Obra> obras = obraDAO.listarTodos();
            result.cbObra.setItems(FXCollections.observableArrayList(obras));
            result.cbObra.setPromptText("Selecione uma obra");
            result.cbObra.setCellFactory(p -> new ListCell<Obra>() {
                @Override
                protected void updateItem(Obra item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTitulo());
                }
            });
            result.cbObra.setButtonCell(new ListCell<Obra>() {
                @Override
                protected void updateItem(Obra item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTitulo());
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao carregar obras: " + e.getMessage());
        }
        
        result.tfCodigoTombo = new TextField();
        result.tfCodigoTombo.setPromptText("Código Tombo");
        
        result.cbEstado = new ComboBox<>();
        result.cbEstado.setItems(FXCollections.observableArrayList(EstadoExemplar.values()));
        result.cbEstado.setPromptText("Estado");
        result.cbEstado.setValue(EstadoExemplar.DISPONIVEL);
        
        result.tfLocalizacao = new TextField();
        result.tfLocalizacao.setPromptText("Localização");
        
        result.dpDataAquisicao = new DatePicker();
        result.dpDataAquisicao.setValue(LocalDate.now());
        
        // Campo de quantidade (apenas para novo exemplar)
        result.spQuantidade = new Spinner<>(1, 100, 1);
        result.spQuantidade.setEditable(true);
        result.spQuantidade.setPrefWidth(150);

        // Preencher com dados existentes
        if (exemplar != null) {
            try {
                Obra obraExistente = obraDAO.buscarPorId(exemplar.getObraId());
                result.cbObra.setValue(obraExistente);
            } catch (SQLException e) {
                // Ignorar erro
            }
            result.tfCodigoTombo.setText(exemplar.getCodigoTombo());
            result.cbEstado.setValue(exemplar.getEstado());
            result.tfLocalizacao.setText(exemplar.getLocalizacao() != null ? exemplar.getLocalizacao() : "");
            result.dpDataAquisicao.setValue(exemplar.getDataAquisicao() != null ? exemplar.getDataAquisicao() : LocalDate.now());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        int row = 0;
        grid.add(new Label("Obra:"), 0, row);
        grid.add(result.cbObra, 1, row++);
        grid.add(new Label("Código Tombo:"), 0, row);
        grid.add(result.tfCodigoTombo, 1, row++);
        
        // Adicionar quantidade apenas se for novo exemplar
        if (exemplar == null) {
            Label lblQuantidade = new Label("Quantidade:");
            lblQuantidade.setTooltip(new Tooltip("Número de exemplares a criar. Serão numerados sequencialmente."));
            grid.add(lblQuantidade, 0, row);
            grid.add(result.spQuantidade, 1, row++);
        }
        
        grid.add(new Label("Estado:"), 0, row);
        grid.add(result.cbEstado, 1, row++);
        grid.add(new Label("Localização:"), 0, row);
        grid.add(result.tfLocalizacao, 1, row++);
        grid.add(new Label("Data de Aquisição:"), 0, row);
        grid.add(result.dpDataAquisicao, 1, row++);

        result.dialog.getDialogPane().setContent(grid);
        result.dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        return result;
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

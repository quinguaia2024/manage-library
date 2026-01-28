package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.model.dao.ObraDAO;
import ao.co.imetro.sgbu.model.entity.Obra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gestão de obras/livros no acervo
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class ObrasController {

    @FXML
    private TableView<Obra> obrasTable;

    @FXML
    private TableColumn<Obra, Integer> colId;
    @FXML
    private TableColumn<Obra, String> colTitulo;
    @FXML
    private TableColumn<Obra, String> colAutor;
    @FXML
    private TableColumn<Obra, String> colIsbn;
    @FXML
    private TableColumn<Obra, String> colEditora;
    @FXML
    private TableColumn<Obra, Integer> colAno;
    @FXML
    private TableColumn<Obra, Integer> colPaginas;
    @FXML
    private TableColumn<Obra, String> colAssunto;
    @FXML
    private TableColumn<Obra, String> colDescricao;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnNovaObra;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnRemover;

    private ObraDAO obraDAO;
    private ObservableList<Obra> obrasData;

    @FXML
    public void initialize() {
        obraDAO = new ObraDAO();
        
        // Configurar colunas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colTitulo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitulo()));
        colAutor.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAutor()));
        colIsbn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIsbn()));
        colEditora.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEditora()));
        colAno.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAnoPublicacao()).asObject());
        colPaginas.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getNumeroPaginas()).asObject());
        colAssunto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAssunto() != null ? cellData.getValue().getAssunto() : ""));
        colDescricao.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescricao() != null ? cellData.getValue().getDescricao() : ""));
        
        // Desabilitar botões até selecionar
        btnEditar.setDisable(true);
        btnRemover.setDisable(true);
        
        // Listener para seleção
        obrasTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean isSelected = newSelection != null;
                btnEditar.setDisable(!isSelected);
                btnRemover.setDisable(!isSelected);
            }
        );
        
        // Carregar dados
        loadObras();
    }

    /**
     * Carrega a lista de obras da base de dados
     */
    private void loadObras() {
        try {
            List<Obra> obras = obraDAO.listarTodos();
            obrasData = FXCollections.observableArrayList(obras);
            obrasTable.setItems(obrasData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Obras", 
                    "Erro ao carregar lista de obras: " + e.getMessage());
        }
    }

    /**
     * Ação: Nova Obra
     */
    @FXML
    private void handleNovaObra(ActionEvent event) {
        Dialog<Obra> dialog = createObraDialog(null);
        Optional<Obra> result = dialog.showAndWait();
        
        result.ifPresent(obra -> {
            try {
                if (obraDAO.inserir(obra)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Obra cadastrada com sucesso!");
                    loadObras();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao cadastrar obra.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao inserir obra: " + e.getMessage());
            }
        });
    }

    /**
     * Ação: Editar Obra
     */
    @FXML
    private void handleEditar(ActionEvent event) {
        Obra selectedObra = obrasTable.getSelectionModel().getSelectedItem();
        if (selectedObra == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione uma obra para editar.");
            return;
        }

        Dialog<Obra> dialog = createObraDialog(selectedObra);
        Optional<Obra> result = dialog.showAndWait();
        
        result.ifPresent(obra -> {
            try {
                if (obraDAO.atualizar(obra)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Obra atualizada com sucesso!");
                    loadObras();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao atualizar obra.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao atualizar obra: " + e.getMessage());
            }
        });
    }

    /**
     * Ação: Remover Obra
     */
    @FXML
    private void handleRemover(ActionEvent event) {
        Obra selectedObra = obrasTable.getSelectionModel().getSelectedItem();
        if (selectedObra == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione uma obra para remover.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Remoção");
        confirmDialog.setHeaderText("Remover Obra");
        confirmDialog.setContentText("Tem certeza que deseja remover a obra \"" + selectedObra.getTitulo() + "\"?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (obraDAO.deletar(selectedObra.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Obra removida com sucesso!");
                    loadObras();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao remover obra.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao remover obra: " + e.getMessage());
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
            obrasTable.setItems(obrasData);
        } else {
            ObservableList<Obra> filtered = FXCollections.observableArrayList();
            for (Obra obra : obrasData) {
                if (obra.getTitulo().toLowerCase().contains(searchText) ||
                    obra.getAutor().toLowerCase().contains(searchText) ||
                    obra.getIsbn().contains(searchText)) {
                    filtered.add(obra);
                }
            }
            obrasTable.setItems(filtered);
        }
    }

    /**
     * Cria um dialog para entrada de dados de obra
     */
    private Dialog<Obra> createObraDialog(Obra obra) {
        Dialog<Obra> dialog = new Dialog<>();
        dialog.setTitle(obra == null ? "Nova Obra" : "Editar Obra");
        dialog.setHeaderText(obra == null ? "Cadastrar nova obra" : "Editar dados da obra");

        // Criar campos
        TextField tfTitulo = new TextField();
        tfTitulo.setPromptText("Título");
        
        TextField tfAutor = new TextField();
        tfAutor.setPromptText("Autor");
        
        TextField tfIsbn = new TextField();
        tfIsbn.setPromptText("ISBN");
        
        TextField tfEditora = new TextField();
        tfEditora.setPromptText("Editora");
        
        Spinner<Integer> spAno = new Spinner<>(1900, 2100, 2024);
        spAno.setPrefWidth(100);
        
        Spinner<Integer> spPaginas = new Spinner<>(1, 10000, 300);
        spPaginas.setPrefWidth(100);
        
        TextField tfAssunto = new TextField();
        tfAssunto.setPromptText("Assunto");
        
        TextArea taDescricao = new TextArea();
        taDescricao.setPromptText("Descrição");
        taDescricao.setPrefRowCount(3);
        taDescricao.setWrapText(true);

        // Preencher com dados existentes
        if (obra != null) {
            tfTitulo.setText(obra.getTitulo());
            tfAutor.setText(obra.getAutor());
            tfIsbn.setText(obra.getIsbn());
            tfEditora.setText(obra.getEditora());
            spAno.getValueFactory().setValue(obra.getAnoPublicacao());
            spPaginas.getValueFactory().setValue(obra.getNumeroPaginas());
            tfAssunto.setText(obra.getAssunto() != null ? obra.getAssunto() : "");
            taDescricao.setText(obra.getDescricao() != null ? obra.getDescricao() : "");
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Título:"), 0, 0);
        grid.add(tfTitulo, 1, 0);
        grid.add(new Label("Autor:"), 0, 1);
        grid.add(tfAutor, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(tfIsbn, 1, 2);
        grid.add(new Label("Editora:"), 0, 3);
        grid.add(tfEditora, 1, 3);
        grid.add(new Label("Ano:"), 0, 4);
        grid.add(spAno, 1, 4);
        grid.add(new Label("Páginas:"), 0, 5);
        grid.add(spPaginas, 1, 5);
        grid.add(new Label("Assunto:"), 0, 6);
        grid.add(tfAssunto, 1, 6);
        grid.add(new Label("Descrição:"), 0, 7);
        grid.add(taDescricao, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Obra result = obra != null ? obra : new Obra();
                result.setTitulo(tfTitulo.getText());
                result.setAutor(tfAutor.getText());
                result.setIsbn(tfIsbn.getText());
                result.setEditora(tfEditora.getText());
                result.setAnoPublicacao(spAno.getValue());
                result.setNumeroPaginas(spPaginas.getValue());
                result.setAssunto(tfAssunto.getText());
                result.setDescricao(taDescricao.getText());
                return result;
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

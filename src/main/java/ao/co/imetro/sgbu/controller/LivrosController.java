package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**

 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class LivrosController {

    @FXML
    private TableView<?> livrosTable; // Tipo genérico por enquanto

    @FXML
    private TableColumn<?, ?> colId;
    @FXML
    private TableColumn<?, ?> colTitulo;
    @FXML
    private TableColumn<?, ?> colAutor;
    @FXML
    private TableColumn<?, ?> colIsbn;
    @FXML
    private TableColumn<?, ?> colEditora;
    @FXML
    private TableColumn<?, ?> colAno;
    @FXML
    private TableColumn<?, ?> colQuantidade;
    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnNovoLivro;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnRemover;


    @FXML
    public void initialize() {
     
        livrosTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean isSelected = newSelection != null;
                btnEditar.setDisable(!isSelected);
                btnRemover.setDisable(!isSelected);
            }
        );
    }

    /**
     * Ação: Novo Livro
     */
    @FXML
    private void handleNovoLivro(ActionEvent event) {
        // Abrir modal de cadastro
        showAlert(Alert.AlertType.INFORMATION, "Novo Livro", 
                 "Funcionalidade de cadastro será implementada na próxima etapa.");
    }

    /**
     * Ação: Editar Livro
     */
    @FXML
    private void handleEditar(ActionEvent event) {
        // Obter item selecionado e abrir modal de edição
        showAlert(Alert.AlertType.INFORMATION, "Editar Livro", 
                 "Funcionalidade de edição será implementada na próxima etapa.");
    }

    /**
     * Ação: Remover Livro
     */
    @FXML
    private void handleRemover(ActionEvent event) {
        // Confirmar e remover
        showAlert(Alert.AlertType.INFORMATION, "Remover Livro", 
                 "Funcionalidade de remoção será implementada na próxima etapa.");
    }

    /**
     * Ação: Pesquisar em tempo real
     */
    @FXML
    private void handleSearch(KeyEvent event) {
        String query = searchField.getText();
        System.out.println("Pesquisando por: " + query);
        // Implementar filtro na lista observável
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }
}

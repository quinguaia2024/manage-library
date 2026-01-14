package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador da Tela Principal (Dashboard)
 * 
 * Responsável por:
 * - Gerenciar navegação entre módulos
 * - Carregar conteúdo dinâmico na área central
 * - Controlar estado dos botões do menu
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnAcervo;

    @FXML
    private Button btnUtentes;

    @FXML
    private Button btnEmprestimos;

    @FXML
    private Button btnRelatorios;

    /**
     * Inicialização do controlador
     * Aqui deves configurar o estado inicial da tela.
     */
    @FXML
    public void initialize() {
        // Define o Dashboard como ativo por padrão e carrega o conteúdo.
        // Se precisares de outra tela inicial, altera esta chamada.
        handleDashboard(new ActionEvent());
    }

    /**
     * Navega para a tela de Dashboard.
     * Este método é chamado quando clicas no botão "Dashboard".
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleDashboard(ActionEvent event) {
        setActiveButton(btnDashboard);
        // Carrega o arquivo FXML separado do dashboard para garantir que ele apareça.
        loadContent("/fxml/dashboard_home.fxml");
    }

    /**
     * Navega para a tela de Gestão de Acervo.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleAcervo(ActionEvent event) {
        setActiveButton(btnAcervo);
        loadContent("/fxml/livros.fxml");
    }

    /**
     * Navega para a tela de Gestão de Utentes.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleUtentes(ActionEvent event) {
        setActiveButton(btnUtentes);
        loadContent("/fxml/utentes.fxml");
    }

    /**
     * Navega para a tela de Empréstimos.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleEmprestimos(ActionEvent event) {
        setActiveButton(btnEmprestimos);
        loadContent("/fxml/emprestimos.fxml");
    }

    /**
     * Navega para a tela de Relatórios.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleRelatorios(ActionEvent event) {
        setActiveButton(btnRelatorios);
        loadContent("/fxml/relatorios.fxml");
    }

    /**
     * Faz logout e retorna para a tela de login.
     * Pede confirmação antes de sair para evitar cliques acidentais.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Saída");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Tens a certeza que desejas sair do sistema?");
        confirmAlert.initOwner(MainApp.getPrimaryStage());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Carrega a tela de login novamente.
                MainApp.setScene("/fxml/login.fxml", "SGBU - Sistema de Gestão de Biblioteca | IMETRO");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível retornar à tela de login.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Carrega conteúdo FXML dinamicamente na área central.
     * Usa este método para trocar o conteúdo exibido no centro da tela.
     * 
     * @param fxmlPath Caminho absoluto do arquivo FXML dentro de resources.
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            // Limpa o conteúdo anterior e adiciona o novo.
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar",
                    "Não foi possível carregar o módulo: " + fxmlPath + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Define qual botão do menu está ativo visualmente.
     * Isto serve apenas para feedback visual (realce) na sidebar.
     * 
     * @param activeButton O botão que foi clicado.
     */
    private void setActiveButton(Button activeButton) {
        // Remove a classe 'active' de todos os botões para "limpar" a seleção anterior.
        btnDashboard.getStyleClass().remove("active");
        btnAcervo.getStyleClass().remove("active");
        btnUtentes.getStyleClass().remove("active");
        btnEmprestimos.getStyleClass().remove("active");
        btnRelatorios.getStyleClass().remove("active");

        // Adiciona a classe 'active' apenas ao botão atual.
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    /**
     * Método utilitário para exibir alertas simples.
     * Usa isto para mostrar erros ou informações ao usuário.
     * 
     * @param type    O tipo do alerta (ERROR, INFORMATION, etc.).
     * @param title   O título da janela.
     * @param message A mensagem principal.
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

package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controlador da Tela de Login
 * 
 * Responsável por:
 * - Capturar as credenciais do usuário
 * - Validar entrada básica
 * - Comunicar com a camada de serviço (futuro)
 * - Navegar para o Dashboard após autenticação
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    /**
     * Inicialização do controlador
     * Executado automaticamente após o carregamento do FXML
     */
    @FXML
    public void initialize() {
        // Configurações iniciais se necessário
        // Por exemplo: definir foco no campo de email
        if (emailField != null) {
            javafx.application.Platform.runLater(() -> emailField.requestFocus());
        }
    }

    /**
     * Manipula o evento de clique no botão "ENTRAR"
     * 
     * Fluxo:
     * 1. Valida se os campos não estão vazios
     * 2. (Futuro) Chama o AuthService para autenticação
     * 3. Navega para o Dashboard se sucesso
     * 
     * @param event Evento de ação do botão
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validação básica de entrada
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios",
                    "Por favor, preencha o e-mail e a senha.");
            return;
        }

        // Validação de formato de e-mail básica
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "E-mail Inválido",
                    "Por favor, insira um e-mail válido.");
            return;
        }

        // SIMULAÇÃO: Por enquanto, aceita qualquer credencial para teste de UI
        try {
            // Navega para o Dashboard
            MainApp.setScene("/fxml/main_dashboard.fxml",
                    "SGBU - Dashboard | IMETRO");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Sistema",
                    "Não foi possível carregar o Dashboard.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manipula o clique em "Esqueci a senha"
     * 
     * @param event Evento de ação do hyperlink
     */
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Recuperação de Senha",
                "Entre em contato com o administrador do sistema:\nadmin@imetro.ao");
    }

    /**
     * Manipula o clique em "Suporte Técnico"
     * 
     * @param event Evento de ação do hyperlink
     */
    @FXML
    private void handleSupport(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Suporte Técnico",
                "Departamento de TI - IMETRO\n" +
                        "E-mail: suporte@imetro.ao\n" +
                        "Telefone: +244 900 000 000");
    }

    /**
     * Manipula o clique em "Criar nova conta"
     * 
     * @param event Evento de ação do hyperlink
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            MainApp.setScene("/fxml/register.fxml", "SGBU - Criar Conta | IMETRO");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro",
                    "Não foi possível carregar a tela de registro.");
            e.printStackTrace();
        }
    }

    /**
     * Método utilitário para exibir alertas nativos do JavaFX
     * 
     * @param type    Tipo do alerta (INFO, WARNING, ERROR)
     * @param title   Título da janela
     * @param message Mensagem a ser exibida
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

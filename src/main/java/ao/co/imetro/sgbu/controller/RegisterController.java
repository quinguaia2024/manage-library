package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

/**
 * Controlador da Tela de Registro
 */
public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Processa o registro de um novo usuário.
     * Verifica se todos os campos estão preenchidos e se as senhas coincidem.
     * 
     * @param event O evento de clique do botão.
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preenche todos os campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Erro de Senha", "As senhas não coincidem.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "E-mail Inválido", "Por favor, insere um e-mail válido.");
            return;
        }

        // SIMULAÇÃO: Registro bem-sucedido
        // Aqui deves adicionar a lógica real de salvar o utilizador na base de dados.
        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Conta criada com sucesso! Faz login para continuar.");

        // Redireciona para o login após o sucesso
        returnToLogin();
    }

    /**
     * Retorna para a tela de login.
     * Podes chamar este método através de um clique no link ou após o registro.
     */
    @FXML
    private void handleBackToLogin() {
        returnToLogin();
    }

    /**
     * Método auxiliar para carregar a cena de login.
     * Separei este método para poder ser reutilizado.
     */
    private void returnToLogin() {
        try {
            MainApp.setScene("/fxml/login.fxml", "SGBU - Sistema de Gestão de Biblioteca | IMETRO");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível retornar ao login.");
            e.printStackTrace();
        }
    }

    /**
     * Exibe um alerta na tela.
     * Usa isto para comunicar erros ou sucessos ao utilizador.
     * 
     * @param type    O tipo de alerta.
     * @param title   O título da janela.
     * @param message A mensagem a ser exibida.
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

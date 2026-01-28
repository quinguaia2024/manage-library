package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

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
    
    private AutenticacaoService autenticacaoService;

    /**
     * Inicialização do controlador
     * Executado automaticamente após o carregamento do FXML
     */
    @FXML
    public void initialize() {
        autenticacaoService = AutenticacaoService.getInstance();
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
     * 1. Chama o AuthService para autenticação
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

        try {
            // Tentar autenticar
            autenticacaoService.autenticar(email, password);
            
            // Sucesso - navegar para dashboard
            showAlert(Alert.AlertType.INFORMATION, "Sucesso",
                    "Bem-vindo, " + autenticacaoService.getUsuarioLogado().getNome() + "!");
            
            MainApp.setScene("/fxml/main_dashboard.fxml",
                    "SGBU - Dashboard | IMETRO");
                    
        } catch (IllegalArgumentException ex) {
            // Erro de autenticação (email não encontrado, senha incorreta, etc)
            showAlert(Alert.AlertType.ERROR, "Erro de Autenticação",
                    ex.getMessage());
        } catch (SQLException ex) {
            // Erro de banco de dados
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados",
                    "Não foi possível conectar ao banco de dados.\n" + ex.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Sistema",
                    "Não foi possível carregar o Dashboard.\n" + e.getMessage());
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
                        "Telefone: +244 925 033 626");
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

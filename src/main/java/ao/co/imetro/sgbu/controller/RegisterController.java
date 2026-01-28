package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.dao.UsuarioDAO;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

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
    
    @FXML
    private ComboBox<PerfilUsuario> perfilComboBox;
    
    private UsuarioDAO usuarioDAO;
    
    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        
        // Configurar ComboBox com apenas Estudante e Docente
        if (perfilComboBox != null) {
            perfilComboBox.getItems().addAll(PerfilUsuario.ESTUDANTE, PerfilUsuario.DOCENTE);
            perfilComboBox.setValue(PerfilUsuario.ESTUDANTE); // Padrão
        }
    }

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
        PerfilUsuario perfil = perfilComboBox.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || perfil == null) {
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
        
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Senha Fraca", "A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        // Criar novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(name);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(BCrypt.hashpw(password, BCrypt.gensalt())); // Hash da senha
        novoUsuario.setPerfil(perfil);
        novoUsuario.setAtivo(true);
        novoUsuario.setLimiteEmprestimos(perfil.getLimiteEmprestimos());
        novoUsuario.setPrazoDias(perfil.getPrazoDias());
        novoUsuario.setLimiteMulta(perfil.getLimiteMulta());
        
        try {
            if (usuarioDAO.inserir(novoUsuario)) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                    "Conta criada com sucesso!\nPerfil: " + perfil.name() + "\nFaz login para continuar.");
                returnToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível criar a conta. Tenta novamente.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "E-mail Duplicado", 
                    "Este e-mail já está registrado. Usa outro e-mail ou faz login.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro de Base de Dados", 
                    "Erro ao criar conta: " + e.getMessage());
            }
        }
    }

    /**
     * Retorna para a tela de login.
     */
    @FXML
    private void handleBackToLogin() {
        returnToLogin();
    }

    /**
     * Método auxiliar para carregar a cena de login.
     */
    private void returnToLogin() {
        try {
            MainApp.setScene("/fxml/login.fxml", "SGBU - Sistema de Gestão de Biblioteca | IMETRO");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível retornar ao login.");
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

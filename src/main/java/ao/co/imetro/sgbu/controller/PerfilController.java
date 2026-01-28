package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.dao.UsuarioDAO;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

/**
 * Controlador da tela de perfil/configurações do usuário
 */
public class PerfilController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label perfilLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private UsuarioDAO usuarioDAO;
    private AutenticacaoService autenticacaoService;
    private Usuario usuarioLogado;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        autenticacaoService = AutenticacaoService.getInstance();
        usuarioLogado = autenticacaoService.getUsuarioLogado();

        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum usuário logado.");
            return;
        }

        nameField.setText(usuarioLogado.getNome());
        emailField.setText(usuarioLogado.getEmail());
        perfilLabel.setText(usuarioLogado.getPerfil().name());
    }

    @FXML
    private void handleSalvar() {
        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum usuário logado.");
            return;
        }

        String nome = nameField.getText().trim();
        String email = emailField.getText().trim();
        String senha = passwordField.getText();
        String confirmar = confirmPasswordField.getText();

        if (nome.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios", "Nome e e-mail são obrigatórios.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "E-mail Inválido", "Por favor, insere um e-mail válido.");
            return;
        }

        if (!senha.isEmpty()) {
            if (senha.length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Senha Fraca", "A senha deve ter pelo menos 6 caracteres.");
                return;
            }
            if (!senha.equals(confirmar)) {
                showAlert(Alert.AlertType.ERROR, "Erro de Senha", "As senhas não coincidem.");
                return;
            }
        }

        try {
            Usuario existente = usuarioDAO.buscarPorEmail(email);
            if (existente != null && existente.getId() != usuarioLogado.getId()) {
                showAlert(Alert.AlertType.ERROR, "E-mail Duplicado", "Este e-mail já está em uso.");
                return;
            }

            usuarioLogado.setNome(nome);
            usuarioLogado.setEmail(email);

            if (!senha.isEmpty()) {
                usuarioLogado.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
            } else {
                usuarioLogado.setSenha("");
            }

            if (usuarioDAO.atualizar(usuarioLogado)) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Perfil atualizado com sucesso.");
                passwordField.clear();
                confirmPasswordField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o perfil.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Base de Dados", "Erro ao atualizar perfil: " + e.getMessage());
        }
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

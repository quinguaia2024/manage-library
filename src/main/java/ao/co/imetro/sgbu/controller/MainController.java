package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
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
    private Button btnUsuarios;

    @FXML
    private Button btnEmprestimos;

    @FXML
    private Button btnExemplares;

    @FXML
    private Button btnMultas;

    @FXML
    private Button btnReservas;
    
    @FXML
    private Button btnEstatisticas;

    @FXML
    private Button btnConfiguracoes;

    private AutenticacaoService autenticacaoService;

    /**
     * Inicialização do controlador
     * Aqui deves configurar o estado inicial da tela.
     */
    @FXML
    public void initialize() {
        autenticacaoService = AutenticacaoService.getInstance();
        
        // Aplicar controle de acesso baseado no perfil do usuário
        aplicarControleDeAcesso();
        
        // Define o Dashboard como ativo por padrão e carrega o conteúdo.
        // Se precisares de outra tela inicial, altera esta chamada.
        handleDashboard(new ActionEvent());
    }

    /**
     * Aplica controle de acesso baseado no perfil do usuário logado
     * Apenas ADMINISTRADOR pode acessar Gestão de Usuários
     */
    private void aplicarControleDeAcesso() {
        if (autenticacaoService.getUsuarioLogado() == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum usuário logado!");
            return;
        }

        PerfilUsuario perfilUsuario = autenticacaoService.getUsuarioLogado().getPerfil();

        // Regras de visibilidade por perfil
        boolean isAdmin = perfilUsuario == PerfilUsuario.ADMINISTRADOR;
        boolean isBibliotecario = perfilUsuario == PerfilUsuario.BIBLIOTECARIO;
        boolean isLeitor = perfilUsuario == PerfilUsuario.ESTUDANTE || perfilUsuario == PerfilUsuario.DOCENTE;

        setMenuVisibility(btnDashboard, true);
        setMenuVisibility(btnConfiguracoes, true);

        setMenuVisibility(btnUsuarios, isAdmin);

        setMenuVisibility(btnAcervo, isAdmin || isBibliotecario);
        setMenuVisibility(btnExemplares, isAdmin || isBibliotecario);
        setMenuVisibility(btnMultas, true); // Todos podem ver suas multas
        setMenuVisibility(btnEstatisticas, isAdmin || isBibliotecario);

        setMenuVisibility(btnEmprestimos, isAdmin || isBibliotecario || isLeitor);
        setMenuVisibility(btnReservas, isAdmin || isBibliotecario || isLeitor);
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
     * Navega para a tela de Gestão de Obras.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleAcervo(ActionEvent event) {
        setActiveButton(btnAcervo);
        loadContent("/fxml/livros.fxml");
    }

    /**
     * Navega para a tela de Gestão de Usuários.
     * Apenas ADMINISTRADOR pode acessar.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleUsuarios(ActionEvent event) {
        // Verificação extra de segurança
        if (autenticacaoService.getUsuarioLogado().getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            showAlert(Alert.AlertType.WARNING, "Acesso Negado", 
                    "Apenas administradores podem acessar Gestão de Usuários.");
            return;
        }
        setActiveButton(btnUsuarios);
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
     * Navega para a tela de Gestão de Exemplares.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleExemplares(ActionEvent event) {
        setActiveButton(btnExemplares);
        loadContent("/fxml/exemplares.fxml");
    }

    /**
     * Navega para a tela de Gestão de Multas.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleMultas(ActionEvent event) {
        setActiveButton(btnMultas);
        loadContent("/fxml/multas.fxml");
    }

    /**
     * Navega para a tela de Gestão de Reservas.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleReservas(ActionEvent event) {
        setActiveButton(btnReservas);
        loadContent("/fxml/reservas.fxml");
    }
    
    /**
     * Navega para a tela de Estatísticas.
     * Apenas ADMINISTRADOR e BIBLIOTECARIO podem acessar.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleEstatisticas(ActionEvent event) {
        // Verificação extra de segurança
        PerfilUsuario perfil = autenticacaoService.getUsuarioLogado().getPerfil();
        if (perfil != PerfilUsuario.ADMINISTRADOR && perfil != PerfilUsuario.BIBLIOTECARIO) {
            showAlert(Alert.AlertType.WARNING, "Acesso Negado", 
                    "Apenas administradores e bibliotecários podem acessar Estatísticas.");
            return;
        }
        setActiveButton(btnEstatisticas);
        loadContent("/fxml/estatisticas.fxml");
    }

    /**
     * Navega para a tela de Configurações/Perfil.
     * 
     * @param event O evento de clique.
     */
    @FXML
    private void handleConfiguracoes(ActionEvent event) {
        setActiveButton(btnConfiguracoes);
        loadContent("/fxml/perfil.fxml");
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
        btnUsuarios.getStyleClass().remove("active");
        btnEmprestimos.getStyleClass().remove("active");
        btnExemplares.getStyleClass().remove("active");
        btnMultas.getStyleClass().remove("active");
        btnReservas.getStyleClass().remove("active");
        if (btnEstatisticas != null) btnEstatisticas.getStyleClass().remove("active");
        btnConfiguracoes.getStyleClass().remove("active");

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

    /**
     * Controla visibilidade e layout de um item do menu.
     */
    private void setMenuVisibility(Button button, boolean visible) {
        if (button == null) {
            return;
        }
        button.setVisible(visible);
        button.setManaged(visible);
        button.setDisable(!visible);
    }
}

package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.MainApp;
import ao.co.imetro.sgbu.model.dao.UsuarioDAO;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
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
 * Controller para gestão de utentes/usuários
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class UtentesController {

    @FXML
    private TableView<Usuario> usersTable;

    @FXML
    private TableColumn<Usuario, Integer> colId;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colPerfil;
    @FXML
    private TableColumn<Usuario, String> colAtivo;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnNovoUtente;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnRemover;

    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> usersData;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        
        // Configurar colunas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNome.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colPerfil.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPerfil().name()));
        colAtivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAtivo() ? "Sim" : "Não"));
        
        // Desabilitar botões até selecionar
        btnEditar.setDisable(true);
        btnRemover.setDisable(true);
        
        // Listener para seleção
        usersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean isSelected = newSelection != null;
                btnEditar.setDisable(!isSelected);
                btnRemover.setDisable(!isSelected);
            }
        );
        
        // Carregar dados
        loadUtentes();
    }

    /**
     * Carrega a lista de utentes da base de dados
     */
    private void loadUtentes() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            usersData = FXCollections.observableArrayList(usuarios);
            usersTable.setItems(usersData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Utentes", 
                    "Erro ao carregar lista de utentes: " + e.getMessage());
        }
    }

    /**
     * Ação: Novo Utente
     */
    @FXML
    private void handleNovoUtente(ActionEvent event) {
        Dialog<Usuario> dialog = createUtenteDialog(null);
        Optional<Usuario> result = dialog.showAndWait();
        
        result.ifPresent(usuario -> {
            try {
                System.out.println("Tentando inserir utente: " + usuario.getNome());
                System.out.println("Email: " + usuario.getEmail());
                System.out.println("Perfil: " + usuario.getPerfil());
                System.out.println("Senha (hash): " + usuario.getSenha());
                
                if (usuarioDAO.inserir(usuario)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Utente cadastrado com sucesso!");
                    loadUtentes();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao cadastrar utente. O método inserir retornou false.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao inserir utente: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro Inesperado", 
                        "Erro inesperado: " + e.getMessage());
            }
        });
    }

    /**
     * Ação: Editar Utente
     */
    @FXML
    private void handleEditar(ActionEvent event) {
        Usuario selectedUtente = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUtente == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione um utente para editar.");
            return;
        }

        Dialog<Usuario> dialog = createUtenteDialog(selectedUtente);
        Optional<Usuario> result = dialog.showAndWait();
        
        result.ifPresent(usuario -> {
            try {
                if (usuarioDAO.atualizar(usuario)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Utente atualizado com sucesso!");
                    loadUtentes();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao atualizar utente.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao atualizar utente: " + e.getMessage());
            }
        });
    }

    /**
     * Ação: Remover Utente
     */
    @FXML
    private void handleRemover(ActionEvent event) {
        Usuario selectedUtente = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUtente == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", 
                    "Selecione um utente para remover.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Remoção");
        confirmDialog.setHeaderText("Remover Utente");
        confirmDialog.setContentText("Tem certeza que deseja remover o utente \"" + selectedUtente.getNome() + "\"?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (usuarioDAO.deletar(selectedUtente.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Utente removido com sucesso!");
                    loadUtentes();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro", 
                            "Erro ao remover utente.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de BD", 
                        "Erro ao remover utente: " + e.getMessage());
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
            usersTable.setItems(usersData);
        } else {
            ObservableList<Usuario> filtered = FXCollections.observableArrayList();
            for (Usuario usuario : usersData) {
                if (usuario.getNome().toLowerCase().contains(searchText) ||
                    usuario.getEmail().toLowerCase().contains(searchText)) {
                    filtered.add(usuario);
                }
            }
            usersTable.setItems(filtered);
        }
    }

    /**
     * Cria um dialog para entrada de dados de utente
     */
    private Dialog<Usuario> createUtenteDialog(Usuario usuario) {
        System.out.println("createUtenteDialog chamado. Usuario existente: " + (usuario != null));
        
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(usuario == null ? "Novo Utente" : "Editar Utente");
        dialog.setHeaderText(usuario == null ? "Cadastrar novo utente" : "Editar dados do utente");

        // Criar campos
        TextField tfNome = new TextField();
        tfNome.setPromptText("Nome completo");
        
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Email");
        
        PasswordField pfSenha = new PasswordField();
        pfSenha.setPromptText(usuario == null ? "Senha" : "Deixe em branco para manter a senha");
        
        ComboBox<PerfilUsuario> cbPerfil = new ComboBox<>();
        cbPerfil.setItems(FXCollections.observableArrayList(PerfilUsuario.values()));
        cbPerfil.setPromptText("Selecione um perfil");
        
        CheckBox cbAtivo = new CheckBox("Ativo");
        cbAtivo.setSelected(true);
        
        Spinner<Integer> spLimite = new Spinner<>(1, 20, 3);
        spLimite.setPrefWidth(100);
        
        Spinner<Integer> spPrazo = new Spinner<>(1, 90, 7);
        spPrazo.setPrefWidth(100);
        
        // Corrigir o Spinner de Double para usar corretamente
        SpinnerValueFactory<Double> multaValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000000.0, 100000.0, 1000.0);
        Spinner<Double> spLimiteMulta = new Spinner<>();
        spLimiteMulta.setValueFactory(multaValueFactory);
        spLimiteMulta.setPrefWidth(100);
        spLimiteMulta.setEditable(true);

        // Preencher com dados existentes
        if (usuario != null) {
            tfNome.setText(usuario.getNome());
            tfEmail.setText(usuario.getEmail());
            // NÃO preencher a senha - manter vazia para evitar double-hash
            cbPerfil.setValue(usuario.getPerfil());
            cbAtivo.setSelected(usuario.isAtivo());
            spLimite.getValueFactory().setValue(usuario.getLimiteEmprestimos());
            spPrazo.getValueFactory().setValue(usuario.getPrazoDias());
            spLimiteMulta.getValueFactory().setValue(usuario.getLimiteMulta());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(tfNome, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(tfEmail, 1, 1);
        grid.add(new Label("Senha:"), 0, 2);
        grid.add(pfSenha, 1, 2);
        grid.add(new Label("Perfil:"), 0, 3);
        grid.add(cbPerfil, 1, 3);
        grid.add(new Label("Limite de Empréstimos:"), 0, 4);
        grid.add(spLimite, 1, 4);
        grid.add(new Label("Prazo em Dias:"), 0, 5);
        grid.add(spPrazo, 1, 5);
        grid.add(new Label("Limite de Multa:"), 0, 6);
        grid.add(spLimiteMulta, 1, 6);
        grid.add(cbAtivo, 0, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Desabilitar o botão OK até que os campos obrigatórios sejam preenchidos
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        
        // Validação em tempo real
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            System.out.println("Event filter do botão OK executado");
            System.out.println("Nome preenchido: " + tfNome.getText().trim());
            System.out.println("Email preenchido: " + tfEmail.getText().trim());
            System.out.println("Perfil selecionado: " + cbPerfil.getValue());
            System.out.println("Senha preenchida: " + (!pfSenha.getText().trim().isEmpty()));
            
            if (tfNome.getText().trim().isEmpty()) {
                System.out.println("BLOQUEADO: Nome vazio");
                showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", 
                        "O campo Nome é obrigatório!");
                event.consume();
                return;
            }
            
            if (tfEmail.getText().trim().isEmpty()) {
                System.out.println("BLOQUEADO: Email vazio");
                showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", 
                        "O campo Email é obrigatório!");
                event.consume();
                return;
            }
            
            if (cbPerfil.getValue() == null) {
                System.out.println("BLOQUEADO: Perfil não selecionado");
                showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", 
                        "Selecione um perfil!");
                event.consume();
                return;
            }
            
            // Validação especial para novo usuário - senha é obrigatória
            if (usuario == null && pfSenha.getText().trim().isEmpty()) {
                System.out.println("BLOQUEADO: Senha vazia para novo usuário");
                showAlert(Alert.AlertType.WARNING, "Senha Obrigatória", 
                        "Para novos usuários, a senha é obrigatória!");
                event.consume();
                return;
            }
            
            System.out.println("Validação passou! Event não será consumido.");
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                System.out.println("Botão OK pressionado no dialog");
                System.out.println("Nome: " + tfNome.getText().trim());
                System.out.println("Email: " + tfEmail.getText().trim());
                System.out.println("Perfil: " + cbPerfil.getValue());
                System.out.println("Senha fornecida: " + (!pfSenha.getText().isEmpty()));
                
                Usuario result = usuario != null ? usuario : new Usuario();
                result.setNome(tfNome.getText().trim());
                result.setEmail(tfEmail.getText().trim());
                
                // Só atualizar a senha se um valor novo foi fornecido
                if (!pfSenha.getText().isEmpty()) {
                    String senhaHash = hashPassword(pfSenha.getText());
                    System.out.println("Hash da senha gerado: " + senhaHash);
                    result.setSenha(senhaHash);
                }
                
                result.setPerfil(cbPerfil.getValue());
                result.setAtivo(cbAtivo.isSelected());
                result.setLimiteEmprestimos(spLimite.getValue());
                result.setPrazoDias(spPrazo.getValue());
                result.setLimiteMulta(spLimiteMulta.getValue());
                
                System.out.println("Usuario criado/atualizado com sucesso");
                return result;
            }
            System.out.println("Botão CANCEL pressionado ou dialog fechado");
            return null;
        });

        return dialog;
    }

    /**
     * Hash simples de senha (em produção, usar bcrypt)
     */
    private String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return Integer.toString(password.hashCode());
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

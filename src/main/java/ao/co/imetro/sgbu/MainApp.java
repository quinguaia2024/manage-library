package ao.co.imetro.sgbu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ao.co.imetro.sgbu.database.DatabaseInitializer;

import java.io.IOException;

/**
 * Classe Principal da Aplicação Desktop SGBU - IMETRO
 * 
 * Esta classe estende Application do JavaFX e é responsável por:
 * - Inicializar a aplicação
 * - Carregar a tela de Login
 * - Configurar a janela principal
 * 
 * @author Sistema SGBU - IMETRO
 * @version 1.0
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    /**
     * Método principal de inicialização do JavaFX.
     * Este é o ponto de partida visual da tua aplicação.
     * 
     * @param stage Janela principal da aplicação.
     * @throws IOException Se houver erro ao carregar o arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        MainApp.primaryStage = stage;

        // Inicializar banco de dados
        try {
            DatabaseInitializer.initializeDatabase();
            DatabaseInitializer.insertDefaultAdmin();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }

        // Carrega o arquivo FXML da tela de login.
        // Podes alterar para carregar outra tela inicial se preferires.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Cria a cena com a tela de login.
        Scene scene = new Scene(root);

        // Aplica o CSS personalizado.
        // Garante que o arquivo styles/application.css existe.
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

        // Configurações da janela.
        stage.setTitle("SGBU - Sistema de Gestão de Biblioteca | IMETRO");
        stage.setScene(scene);
        stage.setResizable(false); // Tamanho fixo conforme solicitado.
        stage.centerOnScreen();

        // Exibe a janela.
        stage.show();
    }

    /**
     * Retorna a referência ao Stage principal.
     * Usa este método para obter a janela principal em outros pontos do código.
     * 
     * @return Stage principal da aplicação.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Método utilitário para trocar de cena (navegação entre telas).
     * Usa este método para navegar entre telas completas (ex: Login -> Main).
     * 
     * @param fxmlPath Caminho do arquivo FXML.
     * @param title    Título da janela.
     * @throws IOException Se houver erro ao carregar o FXML.
     */
    public static void setScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApp.class.getResource("/styles/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.centerOnScreen();
    }

    /**
     * Ponto de entrada da aplicação.
     * É aqui que tudo começa.
     * 
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

package ao.co.imetro.sgbu;

/**
 * Classe Launcher para contornar a verificação de módulos do JavaFX
 * 
 * Esta classe serve apenas como ponto de entrada alternativo para a aplicação.
 * Ela evita o erro "JavaFX runtime components are missing" que ocorre quando
 * se tenta rodar a classe MainApp (que estende Application) diretamente sem
 * os argumentos de módulo VM necessários.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}

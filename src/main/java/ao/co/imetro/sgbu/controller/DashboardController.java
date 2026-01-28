package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.model.dao.*;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import ao.co.imetro.sgbu.model.service.AutenticacaoService;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.sql.SQLException;

/**
 * Controlador do Dashboard principal
 * Responsável por exibir estatísticas básicas do sistema
 */
public class DashboardController {

    @FXML
    private Text totalLivrosText;
    
    @FXML
    private Text emprestimosAtivosText;
    
    @FXML
    private Text multasPendentesText;
    
    @FXML
    private Text totalUtentesText;

    @FXML
    private GridPane statsGrid;

    private ObraDAO obraDAO;
    private EmprestimoDAO emprestimoDAO;
    private MultaDAO multaDAO;
    private UsuarioDAO usuarioDAO;
    
    @FXML
    public void initialize() {
        obraDAO = new ObraDAO();
        emprestimoDAO = new EmprestimoDAO();
        multaDAO = new MultaDAO();
        usuarioDAO = new UsuarioDAO();

        // Carregar dados do dashboard
        carregarDados();
    }

    /**
     * Carrega todos os dados do dashboard consultando o banco de dados
     */
    private void carregarDados() {
        PerfilUsuario perfil = AutenticacaoService.getInstance().getUsuarioLogado() != null
                ? AutenticacaoService.getInstance().getUsuarioLogado().getPerfil()
                : null;

        if (perfil == PerfilUsuario.ESTUDANTE || perfil == PerfilUsuario.DOCENTE) {
            if (statsGrid != null) {
                statsGrid.setVisible(false);
                statsGrid.setManaged(false);
            }
            return;
        }

        try {
            // Total de Obras/Livros
            int totalObras = obraDAO.listarTodos().size();
            totalLivrosText.setText(String.valueOf(totalObras));

            // Total de Empréstimos Ativos
            int emprestimosAtivos = (int) emprestimoDAO.listarTodos().stream()
                    .filter(e -> e.isAtivo())
                    .count();
            emprestimosAtivosText.setText(String.valueOf(emprestimosAtivos));

            // Total de Multas Abertas (Status = ABERTA)
            int multasAbertas = (int) multaDAO.listarTodos().stream()
                    .filter(m -> m.getStatus().name().equals("ABERTA"))
                    .count();
            multasPendentesText.setText(String.valueOf(multasAbertas));

            // Total de Utentes
            int totalUtentes = (int) usuarioDAO.listarTodos().stream()
                    .filter(u -> !u.getPerfil().name().equals(""))
                    .count();
            totalUtentesText.setText(String.valueOf(totalUtentes));

        } catch (SQLException e) {
            // Valores padrão em caso de erro
            totalLivrosText.setText("0");
            emprestimosAtivosText.setText("0");
            multasPendentesText.setText("0");
            totalUtentesText.setText("0");
        }
    }
}

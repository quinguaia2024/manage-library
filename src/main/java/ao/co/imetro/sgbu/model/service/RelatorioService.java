package ao.co.imetro.sgbu.model.service;

import ao.co.imetro.sgbu.model.dao.*;
import ao.co.imetro.sgbu.model.entity.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Serviço para geração de relatórios
 */
public class RelatorioService {
    private final EmprestimoDAO emprestimoDAO;
    private final MultaDAO multaDAO;
    private final UsuarioDAO usuarioDAO;
    private final ObraDAO obraDAO;
    
    public RelatorioService() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.multaDAO = new MultaDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.obraDAO = new ObraDAO();
    }

    /**
     * Obtém empréstimos por período
     */
    public List<Emprestimo> getEmprestimosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        return emprestimoDAO.buscarPorPeriodo(dataInicio, dataFim);
    }

    /**
     * Obtém multas por período
     */
    public List<Multa> getMultasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        return multaDAO.buscarPorPeriodo(dataInicio, dataFim);
    }

    /**
     * Obtém empréstimos atrasados
     */
    public List<Emprestimo> getEmprestimosAtrasados() throws SQLException {
        return emprestimoDAO.buscarAtrasados();
    }

    /**
     * Obtém multas abertas
     */
    public List<Multa> getMultasAbertas() throws SQLException {
        return multaDAO.listarAbertas();
    }

    /**
     * Retorna obras mais emprestadas
     */
    public List<Obra> getObrasMainEmprestadas(int limite) throws SQLException {
        return obraDAO.listarMaisEmprestadas(limite);
    }

    /**
     * Retorna estatísticas de multas
     */
    public List<Object[]> getEstatisticasMultas() throws SQLException {
        return multaDAO.getEstatisticasMultas();
    }

    /**
     * Retorna total de multas abertas em determinado período
     */
    public double getTotalMultasAbertas(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<Multa> multas = getMultasPorPeriodo(dataInicio, dataFim);
        return multas.stream()
            .filter(m -> m.getStatus().name().equals("ABERTA"))
            .mapToDouble(Multa::getValor)
            .sum();
    }

    /**
     * Conta empréstimos em determinado período
     */
    public int countEmprestimosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        return getEmprestimosPorPeriodo(dataInicio, dataFim).size();
    }

    /**
     * Retorna dados para relatório em formato de mapa
     */
    public Map<String, Object> getRelatorioDashboard() throws SQLException {
        Map<String, Object> relatorio = new HashMap<>();
        
        relatorio.put("totalUsuarios", usuarioDAO.listarTodos().size());
        relatorio.put("totalObras", obraDAO.listarTodos().size());
        relatorio.put("emprestimosAtivos", emprestimoDAO.listarTodos().stream()
            .filter(Emprestimo::isAtivo)
            .count());
        relatorio.put("multasAbertas", multaDAO.listarAbertas().size());
        relatorio.put("emprestimosAtrasados", getEmprestimosAtrasados().size());
        
        return relatorio;
    }
}

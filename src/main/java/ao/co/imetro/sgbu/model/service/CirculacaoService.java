package ao.co.imetro.sgbu.model.service;

import ao.co.imetro.sgbu.model.dao.*;
import ao.co.imetro.sgbu.model.entity.*;
import ao.co.imetro.sgbu.model.enums.EstadoExemplar;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Serviço responsável pela lógica de circulação de livros
 * (empréstimos, devoluções, renovações)
 */
public class CirculacaoService {
    private final EmprestimoDAO emprestimoDAO;
    private final ExemplarDAO exemplarDAO;
    private final UsuarioDAO usuarioDAO;
    private final MultaDAO multaDAO;
    private final ReservaDAO reservaDAO;
    
    public CirculacaoService() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.exemplarDAO = new ExemplarDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.multaDAO = new MultaDAO();
        this.reservaDAO = new ReservaDAO();
    }

    /**
     * Registra um novo empréstimo com validações
     */
    public boolean registrarEmprestimo(int usuarioId, int exemplarId) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        Exemplar exemplar = exemplarDAO.buscarPorId(exemplarId);
        
        // Validações
        if (usuario == null || exemplar == null) {
            throw new IllegalArgumentException("Usuário ou exemplar não encontrado");
        }
        
        if (!usuario.isAtivo()) {
            throw new IllegalArgumentException("Usuário inativo");
        }
        
        if (!exemplar.getEstado().isPodeEmprestar()) {
            throw new IllegalArgumentException("Exemplar não está disponível: " + exemplar.getEstado().getDescricao());
        }
        
        // Validar limite de empréstimos
        int emprestimosAtivos = emprestimoDAO.contarEmprestimosAtivos(usuarioId);
        if (emprestimosAtivos >= usuario.getLimiteEmprestimos()) {
            throw new IllegalArgumentException(
                "Limite de empréstimos atingido: " + emprestimosAtivos + "/" + usuario.getLimiteEmprestimos()
            );
        }
        
        // Validar multas pendentes
        double multasAbertas = multaDAO.calcularTotalMultasAbertas(usuarioId);
        if (multasAbertas > usuario.getLimiteMulta()) {
            throw new IllegalArgumentException(
                "Usuário possui multas pendentes acima do limite: " + multasAbertas + "/" + usuario.getLimiteMulta()
            );
        }
        
        // Criar empréstimo
        LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(usuario.getPrazoDias());
        Emprestimo emprestimo = new Emprestimo(usuarioId, exemplarId, dataDevolucaoPrevista);
        
        boolean inserted = emprestimoDAO.inserir(emprestimo);
        
        if (inserted) {
            // Atualizar estado do exemplar para EMPRESTADO
            exemplarDAO.atualizarEstado(exemplarId, EstadoExemplar.EMPRESTADO);
            return true;
        }
        return false;
    }

    /**
     * Registra a devolução de um exemplar e calcula multa se necessário
     */
    public boolean registrarDevolucao(int emprestimoId) throws SQLException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(emprestimoId);
        
        if (emprestimo == null || !emprestimo.isAtivo()) {
            throw new IllegalArgumentException("Empréstimo não encontrado ou já finalizado");
        }
        
        LocalDate hoje = LocalDate.now();
        boolean temAtraso = hoje.isAfter(emprestimo.getDataDevolucaoPrevista());
        
        // Registrar devolução
        emprestimoDAO.registrarDevolucao(emprestimoId, hoje);
        
        // Atualizar estado do exemplar
        exemplarDAO.atualizarEstado(emprestimo.getExemplarId(), EstadoExemplar.DISPONIVEL);
        
        // Calcular e registrar multa se houver atraso
        if (temAtraso) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(
                emprestimo.getDataDevolucaoPrevista(), 
                hoje
            );
            
            Multa multa = new Multa(emprestimo.getUsuarioId(), emprestimoId, (int) diasAtraso);
            multaDAO.inserir(multa);
        }
        
        // Verificar se há reserva aguardando
        Reserva proximaReserva = reservaDAO.buscarProximaReserva(emprestimo.getExemplarId());
        if (proximaReserva != null) {
            // Marcar exemplar como RESERVADO e a reserva como ATENDIDA
            exemplarDAO.atualizarEstado(emprestimo.getExemplarId(), EstadoExemplar.RESERVADO);
            reservaDAO.marcarComAtendida(proximaReserva.getId());
        }
        
        return true;
    }

    /**
     * Renova um empréstimo se não houver reserva ativa
     */
    public boolean renovarEmprestimo(int emprestimoId) throws SQLException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(emprestimoId);
        
        if (emprestimo == null || !emprestimo.isAtivo()) {
            throw new IllegalArgumentException("Empréstimo não encontrado ou já finalizado");
        }
        
        // Verificar se há reserva ativa para a mesma obra
        Exemplar exemplar = exemplarDAO.buscarPorId(emprestimo.getExemplarId());
        Reserva reserva = reservaDAO.buscarProximaReserva(exemplar.getObraId());
        
        if (reserva != null) {
            throw new IllegalArgumentException("Não é possível renovar: há uma reserva ativa para esta obra");
        }
        
        // Renovar empréstimo
        LocalDate novaDevolucao = emprestimo.getDataDevolucaoPrevista().plusDays(7);
        emprestimoDAO.renovar(emprestimoId, novaDevolucao);
        
        return true;
    }

    /**
     * Calcula multa pendente de um empréstimo atrasado
     */
    public double calcularMultaPendente(int emprestimoId) throws SQLException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(emprestimoId);
        
        if (emprestimo == null) {
            return 0;
        }
        
        LocalDate hoje = LocalDate.now();
        if (hoje.isBefore(emprestimo.getDataDevolucaoPrevista())) {
            return 0;
        }
        
        long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(
            emprestimo.getDataDevolucaoPrevista(), 
            hoje
        );
        
        return diasAtraso * Multa.TAXA_DIARIA;
    }

    /**
     * Verifica se um usuário pode fazer novo empréstimo
     */
    public String validarEmprestimo(int usuarioId, int exemplarId) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        Exemplar exemplar = exemplarDAO.buscarPorId(exemplarId);
        
        if (usuario == null) {
            return "Usuário não encontrado";
        }
        
        if (exemplar == null) {
            return "Exemplar não encontrado";
        }
        
        if (!usuario.isAtivo()) {
            return "Usuário inativo";
        }
        
        if (!exemplar.getEstado().isPodeEmprestar()) {
            return "Exemplar indisponível: " + exemplar.getEstado().getDescricao();
        }
        
        int emprestimosAtivos = emprestimoDAO.contarEmprestimosAtivos(usuarioId);
        if (emprestimosAtivos >= usuario.getLimiteEmprestimos()) {
            return "Limite de empréstimos atingido (" + emprestimosAtivos + "/" + usuario.getLimiteEmprestimos() + ")";
        }
        
        double multasAbertas = multaDAO.calcularTotalMultasAbertas(usuarioId);
        if (multasAbertas > usuario.getLimiteMulta()) {
            return "Multas pendentes acima do limite (Kz " + multasAbertas + ")";
        }
        
        return null; // sem erros
    }
}

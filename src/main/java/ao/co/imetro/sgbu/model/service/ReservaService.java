package ao.co.imetro.sgbu.model.service;

import ao.co.imetro.sgbu.model.dao.*;
import ao.co.imetro.sgbu.model.entity.*;
import ao.co.imetro.sgbu.model.enums.StatusReserva;
import java.sql.SQLException;
/**
 * Serviço responsável pela gestão de reservas
 */
public class ReservaService {
    private final ReservaDAO reservaDAO;
    private final ObraDAO obraDAO;
    private final UsuarioDAO usuarioDAO;
    private final ExemplarDAO exemplarDAO;
    public ReservaService() {
        this.reservaDAO = new ReservaDAO();
        this.obraDAO = new ObraDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.exemplarDAO = new ExemplarDAO();
        new EmprestimoDAO();
    }

    /**
     * Registra uma nova reserva
     */
    public boolean registrarReserva(int usuarioId, int obraId) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        Obra obra = obraDAO.buscarPorId(obraId);
        
        if (usuario == null || obra == null) {
            throw new IllegalArgumentException("Usuário ou obra não encontrado");
        }
        
        // Verifica se o usuário já tem uma reserva ativa para esta obra
        Reserva reservaExistente = reservaDAO.buscarReservaAtiva(usuarioId, obraId);
        if (reservaExistente != null) {
            throw new IllegalArgumentException("Usuário já tem uma reserva ativa para esta obra");
        }
        
        // Verifica se há exemplares disponíveis
        int disponveis = exemplarDAO.contarDisponiveisPorObra(obraId);
        if (disponveis > 0) {
            throw new IllegalArgumentException("Há exemplares disponíveis. Registre um empréstimo em vez de reserva");
        }
        
        // Criar reserva
        Reserva reserva = new Reserva(usuarioId, obraId);
        
        // Definir posição na fila
        int posicaoFila = reservaDAO.contarReservasAtivasPorExemplar(obraId) + 1;
        reserva.setPosicaoFila(posicaoFila);
        
        return reservaDAO.inserir(reserva);
    }

    /**
     * Cancela uma reserva
     */
    public boolean cancelarReserva(int reservaId) throws SQLException {
        Reserva reserva = reservaDAO.buscarPorId(reservaId);
        
        if (reserva == null || reserva.getStatus() != StatusReserva.ATIVA) {
            throw new IllegalArgumentException("Reserva não encontrada ou não está ativa");
        }
        
        return reservaDAO.cancelar(reservaId);
    }

    /**
     * Obtém a posição de uma reserva na fila
     */
    public int obterPosicaoFila(int reservaId) throws SQLException {
        Reserva reserva = reservaDAO.buscarPorId(reservaId);
        if (reserva != null) {
            return reserva.getPosicaoFila();
        }
        return -1;
    }

    /**
     * Retorna o tamanho da fila de espera de uma obra
     */
    public int tamanhoFilaDeEspera(int obraId) throws SQLException {
        return reservaDAO.contarReservasAtivasPorExemplar(obraId);
    }

    /**
     * Valida se uma reserva pode ser feita
     */
    public String validarReserva(int usuarioId, int obraId) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        Obra obra = obraDAO.buscarPorId(obraId);
        
        if (usuario == null) {
            return "Usuário não encontrado";
        }
        
        if (obra == null) {
            return "Obra não encontrada";
        }
        
        Reserva reservaExistente = reservaDAO.buscarReservaAtiva(usuarioId, obraId);
        if (reservaExistente != null) {
            return "Já existe uma reserva ativa para esta obra";
        }
        
        int disponveis = exemplarDAO.contarDisponiveisPorObra(obraId);
        if (disponveis > 0) {
            return "Há exemplares disponíveis";
        }
        
        return null; // sem erros
    }
}

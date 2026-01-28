package ao.co.imetro.sgbu.controller;

import ao.co.imetro.sgbu.model.dao.*;
import ao.co.imetro.sgbu.model.entity.Emprestimo;
import ao.co.imetro.sgbu.model.entity.Multa;
import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.service.RelatorioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador da Página de Estatísticas Avançadas
 * Apenas ADMIN e BIBLIOTECARIO têm acesso
 */
public class EstatisticasController {

    // Filtros
    @FXML
    private ComboBox<String> periodoCombo;
    
    @FXML
    private DatePicker dataInicialPicker;
    
    @FXML
    private DatePicker dataFinalPicker;
    
    // Cards de Resumo
    @FXML
    private Text totalEmprestimosPeriodoText;
    
    @FXML
    private Text taxaDevolucaoText;
    
    @FXML
    private Text mediaDiariaText;
    
    @FXML
    private Text receitaMultasText;
    
    // Gráficos
    @FXML
    private LineChart<String, Number> emprestimosLineChart;
    
    @FXML
    private BarChart<String, Number> obrasBarChart;
    
    @FXML
    private PieChart statusPieChart;
    
    @FXML
    private PieChart categoriasPieChart;
    
    @FXML
    private BarChart<String, Number> horarioBarChart;
    
    // Tabela de Usuários
    @FXML
    private TableView<UsuarioAtivo> usuariosAtivosTable;
    
    @FXML
    private TableColumn<UsuarioAtivo, Integer> colPosicao;
    
    @FXML
    private TableColumn<UsuarioAtivo, String> colUsuario;
    
    @FXML
    private TableColumn<UsuarioAtivo, Integer> colEmprestimos;
    
    @FXML
    private TableColumn<UsuarioAtivo, String> colStatus;
    
    // Resumo Financeiro
    @FXML
    private Text multasAbertasValorText;
    
    @FXML
    private Text multasAbertasQtdText;
    
    @FXML
    private Text multasPagasValorText;
    
    @FXML
    private Text multasPagasQtdText;
    
    @FXML
    private Text totalArrecadadoText;
    
    @FXML
    private Text emprestimosAtrasadosText;
    
    // DAOs e Services
    private EmprestimoDAO emprestimoDAO;
    private MultaDAO multaDAO;
    private UsuarioDAO usuarioDAO;
    private RelatorioService relatorioService;
    
    // Período selecionado
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    @FXML
    public void initialize() {
        emprestimoDAO = new EmprestimoDAO();
        multaDAO = new MultaDAO();
        usuarioDAO = new UsuarioDAO();
        relatorioService = new RelatorioService();
        
        // Configurar itens do ComboBox de período
        periodoCombo.setItems(FXCollections.observableArrayList(
            "Últimos 7 dias",
            "Últimos 30 dias",
            "Últimos 90 dias",
            "Último ano",
            "Personalizado"
        ));
        
        // Configurar período padrão (últimos 30 dias)
        dataFim = LocalDate.now();
        dataInicio = dataFim.minusDays(30);
        
        dataInicialPicker.setValue(dataInicio);
        dataFinalPicker.setValue(dataFim);
        periodoCombo.setValue("Últimos 30 dias");
        
        // Configurar listener para o combo de período
        periodoCombo.setOnAction(e -> handlePeriodoChange());
        
        // Configurar tabela de usuários ativos
        configurarTabelaUsuarios();
        
        // Carregar dados iniciais
        atualizarEstatisticas();
    }
    
    /**
     * Configurar colunas da tabela de usuários
     */
    private void configurarTabelaUsuarios() {
        colPosicao.setCellValueFactory(new PropertyValueFactory<>("posicao"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmprestimos.setCellValueFactory(new PropertyValueFactory<>("totalEmprestimos"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    /**
     * Handler para mudança de período no ComboBox
     */
    @FXML
    private void handlePeriodoChange() {
        String periodo = periodoCombo.getValue();
        dataFim = LocalDate.now();
        
        switch (periodo) {
            case "Últimos 7 dias":
                dataInicio = dataFim.minusDays(7);
                break;
            case "Últimos 30 dias":
                dataInicio = dataFim.minusDays(30);
                break;
            case "Últimos 90 dias":
                dataInicio = dataFim.minusDays(90);
                break;
            case "Último ano":
                dataInicio = dataFim.minusYears(1);
                break;
            case "Personalizado":
                // Usuário escolhe manualmente
                return;
        }
        
        dataInicialPicker.setValue(dataInicio);
        dataFinalPicker.setValue(dataFim);
        atualizarEstatisticas();
    }
    
    /**
     * Atualizar todas as estatísticas
     */
    @FXML
    private void atualizarEstatisticas() {
        // Atualizar período baseado nos pickers
        if (periodoCombo.getValue().equals("Personalizado")) {
            dataInicio = dataInicialPicker.getValue();
            dataFim = dataFinalPicker.getValue();
        }
        
        try {
            carregarCardsResumo();
            carregarGraficoEvolucao();
            carregarGraficoObras();
            carregarGraficoStatus();
            carregarGraficoCategorias();
            carregarGraficoHorario();
            carregarTabelaUsuarios();
            carregarResumoFinanceiro();
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao carregar estatísticas: " + e.getMessage());
        }
    }
    
    /**
     * Carregar cards de resumo
     */
    private void carregarCardsResumo() throws SQLException {
        List<Emprestimo> emprestimosPeriodo = filtrarEmprestimosPorPeriodo();
        
        // Total de empréstimos no período
        int total = emprestimosPeriodo.size();
        totalEmprestimosPeriodoText.setText(String.valueOf(total));
        
        // Taxa de devolução
        long devolvidos = emprestimosPeriodo.stream()
                .filter(e -> !e.isAtivo())
                .count();
        double taxa = total > 0 ? (devolvidos * 100.0 / total) : 0;
        taxaDevolucaoText.setText(String.format("%.1f%%", taxa));
        
        // Média diária
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        double media = dias > 0 ? (double) total / dias : 0;
        mediaDiariaText.setText(String.format("%.1f", media));
        
        // Receita de multas no período
        List<Multa> multasPeriodo = filtrarMultasPorPeriodo();
        double receita = multasPeriodo.stream()
                .filter(m -> m.getStatus().name().equals("PAGA"))
                .mapToDouble(Multa::getValor)
                .sum();
        receitaMultasText.setText(String.format("%.2f Kz", receita));
    }
    
    /**
     * Carregar gráfico de evolução de empréstimos
     */
    private void carregarGraficoEvolucao() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Empréstimos");
        
        List<Emprestimo> emprestimos = filtrarEmprestimosPorPeriodo();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Agrupar por dia
        Map<LocalDate, Long> emprestimosPorDia = emprestimos.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDataEmprestimo().toLocalDate(),
                        Collectors.counting()
                ));
        
        // Adicionar todos os dias do período
        LocalDate data = dataInicio;
        while (!data.isAfter(dataFim)) {
            long quantidade = emprestimosPorDia.getOrDefault(data, 0L);
            series.getData().add(new XYChart.Data<>(data.format(formatter), quantidade));
            data = data.plusDays(1);
        }
        
        emprestimosLineChart.getData().clear();
        emprestimosLineChart.getData().add(series);
    }
    
    /**
     * Carregar gráfico de obras mais emprestadas
     */
    private void carregarGraficoObras() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Empréstimos");
        
        List<Emprestimo> emprestimos = filtrarEmprestimosPorPeriodo();
        
        // Agrupar por obra e contar
        Map<Integer, Long> emprestimosPorObra = emprestimos.stream()
                .collect(Collectors.groupingBy(
                        Emprestimo::getExemplarId,
                        Collectors.counting()
                ));
        
        // Pegar top 10
        List<Map.Entry<Integer, Long>> top10 = emprestimosPorObra.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        for (Map.Entry<Integer, Long> entry : top10) {
            try {
                // Buscar título da obra (simplificado)
                String titulo = "Obra #" + entry.getKey();
                if (titulo.length() > 15) {
                    titulo = titulo.substring(0, 12) + "...";
                }
                series.getData().add(new XYChart.Data<>(titulo, entry.getValue()));
            } catch (Exception e) {
                // Ignorar erros
            }
        }
        
        obrasBarChart.getData().clear();
        obrasBarChart.getData().add(series);
    }
    
    /**
     * Carregar gráfico de status
     */
    private void carregarGraficoStatus() throws SQLException {
        List<Emprestimo> emprestimos = filtrarEmprestimosPorPeriodo();
        
        long ativos = emprestimos.stream().filter(Emprestimo::isAtivo).count();
        long devolvidos = emprestimos.size() - ativos;
        
        List<Emprestimo> atrasados = relatorioService.getEmprestimosAtrasados();
        long qtdAtrasados = atrasados.stream()
                .filter(e -> {
                    LocalDate dataEmp = e.getDataEmprestimo().toLocalDate();
                    return !dataEmp.isBefore(dataInicio) && !dataEmp.isAfter(dataFim);
                })
                .count();
        
        statusPieChart.getData().clear();
        if (ativos > 0) {
            statusPieChart.getData().add(new PieChart.Data("Ativos (" + ativos + ")", ativos));
        }
        if (devolvidos > 0) {
            statusPieChart.getData().add(new PieChart.Data("Devolvidos (" + devolvidos + ")", devolvidos));
        }
        if (qtdAtrasados > 0) {
            statusPieChart.getData().add(new PieChart.Data("Atrasados (" + qtdAtrasados + ")", qtdAtrasados));
        }
    }
    
    /**
     * Carregar gráfico de categorias
     */
    private void carregarGraficoCategorias() throws SQLException {
        // Simular categorias (em um sistema real, viria do banco)
        Map<String, Integer> categorias = new HashMap<>();
        categorias.put("Tecnologia", 45);
        categorias.put("Literatura", 32);
        categorias.put("Ciências", 28);
        categorias.put("História", 20);
        categorias.put("Artes", 15);
        categorias.put("Outros", 10);
        
        categoriasPieChart.getData().clear();
        for (Map.Entry<String, Integer> entry : categorias.entrySet()) {
            categoriasPieChart.getData().add(
                new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue())
            );
        }
    }
    
    /**
     * Carregar gráfico de horário
     */
    private void carregarGraficoHorario() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Empréstimos");
        
        List<Emprestimo> emprestimos = filtrarEmprestimosPorPeriodo();
        
        // Agrupar por hora
        Map<Integer, Long> emprestimosPorHora = emprestimos.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDataEmprestimo().getHour(),
                        Collectors.counting()
                ));
        
        // Adicionar todas as horas
        for (int hora = 7; hora <= 22; hora++) {
            long quantidade = emprestimosPorHora.getOrDefault(hora, 0L);
            series.getData().add(new XYChart.Data<>(hora + ":00", quantidade));
        }
        
        horarioBarChart.getData().clear();
        horarioBarChart.getData().add(series);
    }
    
    /**
     * Carregar tabela de usuários mais ativos
     */
    private void carregarTabelaUsuarios() throws SQLException {
        List<Emprestimo> emprestimos = filtrarEmprestimosPorPeriodo();
        
        // Agrupar por usuário
        Map<Integer, Long> emprestimosPorUsuario = emprestimos.stream()
                .collect(Collectors.groupingBy(
                        Emprestimo::getUsuarioId,
                        Collectors.counting()
                ));
        
        // Criar lista de usuários ativos
        ObservableList<UsuarioAtivo> usuarios = FXCollections.observableArrayList();
        
        List<Map.Entry<Integer, Long>> top10 = emprestimosPorUsuario.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        int posicao = 1;
        for (Map.Entry<Integer, Long> entry : top10) {
            try {
                Usuario user = usuarioDAO.buscarPorId(entry.getKey());
                if (user != null) {
                    usuarios.add(new UsuarioAtivo(
                            posicao++,
                            user.getNome(),
                            entry.getValue().intValue(),
                            "Ativo"
                    ));
                }
            } catch (SQLException e) {
                // Ignorar erros
            }
        }
        
        usuariosAtivosTable.setItems(usuarios);
    }
    
    /**
     * Carregar resumo financeiro
     */
    private void carregarResumoFinanceiro() throws SQLException {
        List<Multa> multasPeriodo = filtrarMultasPorPeriodo();
        
        // Multas abertas
        List<Multa> abertas = multasPeriodo.stream()
                .filter(m -> m.getStatus().name().equals("ABERTA"))
                .collect(Collectors.toList());
        double valorAbertas = abertas.stream().mapToDouble(Multa::getValor).sum();
        multasAbertasValorText.setText(String.format("%.2f Kz", valorAbertas));
        multasAbertasQtdText.setText(abertas.size() + " multas");
        
        // Multas pagas
        List<Multa> pagas = multasPeriodo.stream()
                .filter(m -> m.getStatus().name().equals("PAGA"))
                .collect(Collectors.toList());
        double valorPagas = pagas.stream().mapToDouble(Multa::getValor).sum();
        multasPagasValorText.setText(String.format("%.2f Kz", valorPagas));
        multasPagasQtdText.setText(pagas.size() + " multas");
        
        // Total arrecadado
        totalArrecadadoText.setText(String.format("%.2f Kz", valorPagas));
        
        // Empréstimos atrasados
        List<Emprestimo> atrasados = relatorioService.getEmprestimosAtrasados();
        long qtdAtrasados = atrasados.stream()
                .filter(e -> {
                    LocalDate dataEmp = e.getDataEmprestimo().toLocalDate();
                    return !dataEmp.isBefore(dataInicio) && !dataEmp.isAfter(dataFim);
                })
                .count();
        emprestimosAtrasadosText.setText(String.valueOf(qtdAtrasados));
    }
    
    /**
     * Filtrar empréstimos por período
     */
    private List<Emprestimo> filtrarEmprestimosPorPeriodo() throws SQLException {
        List<Emprestimo> todos = emprestimoDAO.listarTodos();
        return todos.stream()
                .filter(e -> {
                    LocalDate data = e.getDataEmprestimo().toLocalDate();
                    return !data.isBefore(dataInicio) && !data.isAfter(dataFim);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Filtrar multas por período
     */
    private List<Multa> filtrarMultasPorPeriodo() throws SQLException {
        return multaDAO.buscarPorPeriodo(dataInicio, dataFim);
    }
    
    /**
     * Exportar relatório em PDF
     */
    @FXML
    private void exportarPDF() {
        showAlert("Exportar PDF", "Funcionalidade em desenvolvimento");
    }
    
    /**
     * Mostrar alerta
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Classe auxiliar para a tabela de usuários ativos
     */
    public static class UsuarioAtivo {
        private final int posicao;
        private final String nome;
        private final int totalEmprestimos;
        private final String status;
        
        public UsuarioAtivo(int posicao, String nome, int totalEmprestimos, String status) {
            this.posicao = posicao;
            this.nome = nome;
            this.totalEmprestimos = totalEmprestimos;
            this.status = status;
        }
        
        public int getPosicao() { return posicao; }
        public String getNome() { return nome; }
        public int getTotalEmprestimos() { return totalEmprestimos; }
        public String getStatus() { return status; }
    }
}

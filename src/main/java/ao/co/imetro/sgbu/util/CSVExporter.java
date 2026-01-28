package ao.co.imetro.sgbu.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilitário para exportação de dados em CSV
 */
public class CSVExporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Exporta dados para CSV
     */
    public static void exportarCSV(String caminhoArquivo, List<String[]> dados, String[] cabecalho) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Escrever cabeçalho
            writer.write(String.join(",", cabecalho));
            writer.newLine();
            
            // Escrever dados
            for (String[] linha : dados) {
                writer.write(String.join(",", linha));
                writer.newLine();
            }
        }
    }

    /**
     * Escapa caracteres especiais CSV
     */
    public static String escaparCSV(String valor) {
        if (valor == null) {
            return "";
        }
        
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    /**
     * Formata data para CSV
     */
    public static String formatarData(LocalDate data) {
        return data != null ? data.format(DATE_FORMATTER) : "";
    }

    /**
     * Formata data e hora para CSV
     */
    public static String formatarDataHora(LocalDateTime dataHora) {
        return dataHora != null ? dataHora.format(DATETIME_FORMATTER) : "";
    }
}

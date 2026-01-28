package ao.co.imetro.sgbu.util;

import java.util.regex.Pattern;

/**
 * Utilitário com validações comuns
 */
public class Validador {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
   
    /**
     * Valida email
     */
    public static boolean isEmailValido(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida ISBN (básico)
     */
    public static boolean isISBNValido(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return true; // ISBN é opcional
        }
        // ISBN-10 ou ISBN-13
        isbn = isbn.replaceAll("[^0-9X]", "");
        return (isbn.length() == 10 || isbn.length() == 13);
    }

    /**
     * Valida se campo não está vazio
     */
    public static boolean isNaoVazio(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    /**
     * Valida se string tem tamanho mínimo
     */
    public static boolean temTamanoMinimo(String texto, int minimo) {
        return texto != null && texto.length() >= minimo;
    }

    /**
     * Valida se um número é positivo
     */
    public static boolean ePositivo(Number numero) {
        return numero != null && numero.doubleValue() > 0;
    }

    /**
     * Valida dados obrigatórios de uma obra
     */
    public static String validarObra(String titulo, String autor, String isbn) {
        if (!isNaoVazio(titulo)) {
            return "Título é obrigatório";
        }
        
        if (!isNaoVazio(autor)) {
            return "Autor é obrigatório";
        }
        
        if (!isISBNValido(isbn)) {
            return "ISBN inválido";
        }
        
        return null; // sem erros
    }
}

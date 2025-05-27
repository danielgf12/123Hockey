package main.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase de utilidad para aplicar funciones hash.
 * Proporciona un método para generar un hash SHA-256 a partir de una cadena.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class HashUtil {

    /**
     * Aplica el algoritmo SHA-256 al texto de entrada y devuelve el resultado en formato hexadecimal.
     *
     * @param input Texto a hashear
     * @return Hash SHA-256 en formato hexadecimal o null si ocurre un error
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

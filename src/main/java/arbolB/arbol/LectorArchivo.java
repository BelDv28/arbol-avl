package arbolB.arbol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorArchivo {
    private static final String SEPARADORES = "[,;\\t]+|,\\s+";

    public static List<Integer> leer(String ruta) throws IOException {

        List<Integer> numeros = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            String linea;

            while ((linea = br.readLine()) != null) {

                // Cada línea puede tener uno o varios números con cualquier separador
                String[] tokens = linea.split(SEPARADORES);

                for (String token : tokens) {
                    String limpio = token.trim();

                    if (!limpio.isEmpty()) {
                        try {
                            numeros.add(Integer.parseInt(limpio));
                        } catch (NumberFormatException e) {
                            // token no numérico → se ignora sin romper la carga
                        }
                    }
                }
            }

        } // BufferedReader se cierra aquí automáticamente

        if (numeros.isEmpty()) {
            throw new IOException("El archivo no contiene numeros validos.");
        }

        return numeros;
    }
}

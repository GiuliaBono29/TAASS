package unito.taass.blackcoffeelog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;

//Parla con il dico fisso del computer
//prende la stringa dal Consumer, apre il file e aggiunge la nuova riga in fondo al file

@Service
public class LogService {

    @Value("${LOG_DIR}")
    private String logDir;

    private final String LOG_FILE_NAME = "system_logs.txt";

    public void writeToLogFile(String message) {
        try {
            // Crea la cartella se non esiste
            Path directoreyPath = Paths.get(logDir);
            Path filePath = directoreyPath.resolve(LOG_FILE_NAME);

            Files.createDirectories(directoreyPath);

            // Scrittura sicura con try-with-resources
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del log: " + e.getMessage());
        }
    }
}

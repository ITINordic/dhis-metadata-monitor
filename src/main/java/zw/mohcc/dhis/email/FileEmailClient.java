/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.email;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author cliffordc
 */

@Data
public class FileEmailClient implements EmailClient{
    @NonNull
    private Path emailRoot;

    @Override
    public void sendMessage(String from, String[] recipients, String subject, String message) {
        final String dirname = Arrays.stream(recipients).collect(Collectors.joining("_"));
        final Path emailDir = emailRoot.resolve(dirname);
        final String filename = "email_"+java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        final File file = emailDir.resolve(filename).toFile();
        try {
            FileUtils.writeStringToFile(file, String.format("From: %s\nSubject: %s\n\n%s", from, subject, message));
        } catch (IOException ex) {
            Logger.getLogger(FileEmailClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

package com.drbagchisclasses.drbagchi_api.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class ExceptionLogger
{
    private static final String LOG_FILE = "exceptions.log"; // will create in project root

    // Reusable method
    public static void logException(Exception ex) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true); // true = append
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("===== Exception at " + LocalDateTime.now() + " =====");
            ex.printStackTrace(pw); // full stack trace into file
            pw.println("====================================================");
            pw.println();

        } catch (IOException ioEx) {
            System.err.println("Failed to log exception: " + ioEx.getMessage());
        }
    }
}

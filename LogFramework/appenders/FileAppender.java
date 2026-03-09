package LogFramework.appenders;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import LogFramework.entity.LogMessage;
import LogFramework.formater.Formatter;
import LogFramework.formater.PlianTextFormatter;

public class FileAppender implements LogAppender {
    private BufferedWriter writer;

    private Formatter format;

     public FileAppender(String filePath) {
        this(filePath, new PlianTextFormatter());
    }

    public FileAppender(String filePath, Formatter formatter) {
        this.format = formatter;
        try {
            this.writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open log file: " + filePath, e);
        }
    }

    @Override
    public synchronized void append(LogMessage message) {
        try {
            writer.append(format.formate(message));
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            System.err.println("FileAppender error: " + e.getMessage());
        }
    }

    public void close(){
        try { writer.close(); } catch (IOException ignored) {}

    }

    @Override
    public void setFormate(Formatter format) {
       this.format=format;
    }
    
}

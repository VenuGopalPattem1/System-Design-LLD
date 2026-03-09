package LogFramework.formater;

import java.time.format.DateTimeFormatter;

import LogFramework.entity.LogMessage;

public class JsonFormatter implements Formatter {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public String formate(LogMessage msg) {
        return String.format(
                "{\"timestamp\":\"%s\",\"level\":\"%s\",\"thread\":\"%s\",\"message\":\"%s\"}",
                msg.getTimestamp().format(FMT),
                msg.getLevel(),
                msg.getThreadName(),
                msg.getMessage().replace("\"", "\\\"")   // escape quotes in message
        );
    }
}
package LogFramework.formater;

import java.time.format.DateTimeFormatter;

import LogFramework.entity.LogMessage;

public class PlianTextFormatter implements Formatter {
    private DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String formate(LogMessage msg) {
       return String.format("[%s] [%s] %s",
                msg.getTimestamp().format(dtf),
                msg.getLevel(),
                msg.getMessage());
    }
    
}

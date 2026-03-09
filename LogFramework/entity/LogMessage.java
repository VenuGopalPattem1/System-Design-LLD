package LogFramework.entity;

import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

public class LogMessage {
    private LocalDateTime timestamp;
    private String message;
    private LogLevel level;
    private String threadName;
   
    // private DateTimeFormatter dtf =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogMessage(LogLevel level,String message){
        this.level=level;
        this.message=message;
        timestamp=LocalDateTime.now();
        this.threadName=Thread.currentThread().getName();
    }

    public String getThreadName() {
        return threadName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLevel() {
        return level;
    }

    // public String toString(){
    //     return String.format("[%s] [%s] %s", timestamp.format(dtf),level,message);
    // }
}

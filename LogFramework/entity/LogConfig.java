package LogFramework.entity;

import LogFramework.appenders.LogAppender;

public class LogConfig {
    private LogLevel level;
    private LogAppender appender;
    public LogConfig(LogLevel level,LogAppender appender ){
        this.level=level;
        this.appender=appender;
    }
    public LogLevel getLevel() {
        return level;
    }
    public void setLevel(LogLevel level) {
        this.level = level;
    }
    public LogAppender getAppender() {
        return appender;
    }
    public void setAppender(LogAppender appender) {
        this.appender = appender;
    }

    
}

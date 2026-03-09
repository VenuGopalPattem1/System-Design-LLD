package LogFramework.entity;

import LogFramework.appenders.ConsoleAppender;

public class Logger {
    private static volatile Logger instance;
    private LogConfig config;

    // default
    private Logger() {
        this.config = new LogConfig(LogLevel.INFO, new ConsoleAppender());
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null)
                    instance = new Logger();
            }
        }
        return instance;
    }

    public synchronized void setConfig(LogConfig config) {
        this.config = config;
    }

    public synchronized LogConfig getConfig() {
        return config;
    }

    public void log(LogLevel lvl, String msg) {
        LogConfig cfg;
        synchronized (this) {
            cfg = this.config;
        }
        if (lvl.getPriority() >= cfg.getLevel().getPriority()) {
            cfg.getAppender().append(new LogMessage(lvl, msg));
        }
    }

    public void debug(String msg) {
        log(LogLevel.DEBUG, msg);
    }

    public void info(String msg) {
        log(LogLevel.INFO, msg);
    }

    public void warning(String msg) {
        log(LogLevel.WARNING, msg);
    }

    public void error(String msg) {
        log(LogLevel.ERROR, msg);
    }

    public void fatal(String msg) {
        log(LogLevel.FATAL, msg);
    }

}

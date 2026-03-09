package LogFramework;

import LogFramework.appenders.ConsoleAppender;
import LogFramework.appenders.DatabaseAppender;
import LogFramework.appenders.FileAppender;
import LogFramework.entity.LogConfig;
import LogFramework.entity.LogLevel;
import LogFramework.entity.Logger;
import LogFramework.formater.JsonFormatter;

public class Main {
    public static void main(String[] args) throws Exception {
        Logger log = Logger.getInstance();
        LogConfig cf = new LogConfig(LogLevel.DEBUG, new ConsoleAppender());
        log.setConfig(cf);
        System.out.println("=== PlainText Format ===");
        log.debug("Server started on port 8080");
        log.info("User login successful");
        log.warning("High memory usage");
        log.error("DB connection failed");

        System.out.println("\n=== JSON Format ===");
        ConsoleAppender ca = new ConsoleAppender();
        ca.setFormate(new JsonFormatter());
        log.setConfig(new LogConfig(LogLevel.DEBUG, ca));
        log.info("Payment processed: amount=\"$100\"");
        log.error("Timeout on service call");

        System.out.println("\n=== PlainText To File ===");
        log.setConfig(new LogConfig(LogLevel.DEBUG, new FileAppender("data.txt")));
        log.debug("Server started on port 8080");
        log.info("User login successful");
        log.warning("High memory usage");
        log.error("DB connection failed");

        System.out.println("\n=== JSON Format => Database ===");
        DatabaseAppender db = new DatabaseAppender(new JsonFormatter());
        log.setConfig(new LogConfig(LogLevel.ERROR, db));
        log.error("Critical failure");
        log.fatal("System shutdown");
        System.out.println("DB records: " + db.fetchAll().size());

        System.out.println("\n=== MultiThreading Functionality ===");
        log.setConfig(new LogConfig(LogLevel.INFO, new ConsoleAppender()));
        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            for (int i = 1; i <= 2; i++)
                log.info(name + " log entry " + i);
        };

        Thread t1 = new Thread(task, "Thread-A");
        Thread t2 = new Thread(task, "Thread-B");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("\nDone.");
    }
}

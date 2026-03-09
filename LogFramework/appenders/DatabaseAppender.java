package LogFramework.appenders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import LogFramework.entity.LogMessage;
import LogFramework.formater.Formatter;
import LogFramework.formater.JsonFormatter;

public class DatabaseAppender implements LogAppender {
    private Formatter formatter;
    private final List<String> db = Collections.synchronizedList(new ArrayList<>());

    public DatabaseAppender() {
        this.formatter = new JsonFormatter();        // JSON suits DB storage well
    }

    public DatabaseAppender(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setFormate(Formatter formatter) { this.formatter = formatter; }

    @Override
    public void append(LogMessage message) {
        String row = formatter.formate(message);
        db.add(row);
        System.out.println("[DB INSERT] " + row);
    }

    public List<String> fetchAll() { return Collections.unmodifiableList(db); }


}
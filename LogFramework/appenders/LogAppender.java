package LogFramework.appenders;


import LogFramework.entity.LogMessage;
import LogFramework.formater.Formatter;

public interface LogAppender {
    public void setFormate(Formatter format);
    public void append(LogMessage message);
}

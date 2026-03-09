package LogFramework.appenders;

import LogFramework.entity.LogLevel;
import LogFramework.entity.LogMessage;
import LogFramework.formater.Formatter;
import LogFramework.formater.PlianTextFormatter;

public class ConsoleAppender implements LogAppender{
    private Formatter format;

    public ConsoleAppender(){
        this.format=new PlianTextFormatter(); //default
    }

    @Override
    public void append(LogMessage message) {
        String output=format.formate(message);
       if(message.getLevel().getPriority()>=LogLevel.ERROR.getPriority()){
        System.err.println(output);
       }else{
        System.out.println(output);
       }
    }

    @Override
    public void setFormate(Formatter format) {
       this.format=format;
    }
    
}

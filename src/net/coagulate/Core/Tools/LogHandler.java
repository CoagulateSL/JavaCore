package net.coagulate.Core.Tools;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author Iain Price
 */
public class LogHandler extends Handler {

    public static final void initialise() {
        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.ALL);
        Logger.getLogger("").addHandler(new LogHandler());
    }
    
    public LogHandler() { super(); setLevel(Level.ALL); }
    @Override
    public void publish(LogRecord record) {
        Object[] parameters=record.getParameters();
        Level level = record.getLevel();
        String system=record.getLoggerName();
        String classname=record.getSourceClassName();
        String method=record.getSourceMethodName();
        int thread=record.getThreadID();
        String message=record.getMessage();
        long when=record.getMillis();
        if (!system.startsWith("net.coagulate.")) { return; }
        system=system.replaceFirst("net\\.coagulate\\.",""); 
        while (classname.indexOf(".")!=-1) {
            classname=classname.substring(classname.indexOf(".")+1);
        }
        if (parameters!=null && parameters.length>0) {
            for (int i=0;i<parameters.length;i++) {
                String replacewith=parameters[i].toString();
                //if (replacewith.length()>100) {
                //    System.out.println("WARNING: Large substitution");
                //} else {
                message=message.replaceAll("\\{"+i+"\\}",parameters[i].toString());
                //}
            }
        }
        System.out.println(formatLevel(level)+"#"+postpad(thread+"",4)+" "+system+" - "+message+" (@"+classname+"."+method+")");
        if (record.getThrown()!=null) { System.out.println(ExceptionTools.toString(record.getThrown())); }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    private String formatLevel(Level level) {
        if (level==Level.CONFIG) { return "conf"; }
        if (level==Level.FINE) { return "D   "; }
        if (level==Level.FINER) { return "d   "; }
        if (level==Level.FINEST) { return "_ "; }
        if (level==Level.INFO) { return "Info"; }
        if (level==Level.SEVERE) { return "XXXX"; }
        if (level==Level.WARNING) { return "WARN"; }
        return "????";
    }
    String postpad(String in,int len) { while (in.length()<len) { in=in+" "; } return in; }
}

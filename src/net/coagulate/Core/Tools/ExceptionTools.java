package net.coagulate.Core.Tools;

/**
 *
 * @author Iain Price
 */
public abstract class ExceptionTools {
    public static String dumpException(Throwable e) {
        String p="";
        if (e.getCause()!=null) { p=p+dumpException(e.getCause()); }
        p=p+"<h3>"+e.getClass().getName()+" - "+e.getLocalizedMessage()+"</h3>";
        for (StackTraceElement st:e.getStackTrace()) {
            p+="<pre>";
            p+=" at "+st.getClassName()+"."+st.getMethodName()+"("+st.getFileName()+":"+st.getLineNumber()+")";
            p+="</pre>";
        }
        return p;
    }
    public static String toString(Throwable e) {
        String p="";
        if (e.getCause()!=null) { p=p+dumpException(e.getCause()); }
        p=p+"***EXCEPTION***: "+e.getClass().getName()+" - "+e.getLocalizedMessage()+"\n";
        for (StackTraceElement st:e.getStackTrace()) {
            p+="___exception___: "+st.getClassName()+"."+st.getMethodName()+":"+st.getLineNumber()+"\n";
        }
        return p;
    }   
    public static String toHTML(Throwable e) { return dumpException(e); }

}

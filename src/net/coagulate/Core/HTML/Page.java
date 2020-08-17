package net.coagulate.Core.HTML;

import org.apache.http.HttpStatus;

import javax.annotation.Nonnull;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// we deliberately don't extend container, even though we kinda do
public class Page {

    private static final Map<Thread,Page> threadmap=new ConcurrentHashMap<>();

    /** Returns a per-thread page object
     *
     * @return Thread's page
     */
    public static Page page() {
        Thread us=Thread.currentThread();
        if (threadmap.containsKey(us)) { return threadmap.get(us); }
        Page page=new Page();
        threadmap.put(us,page);
        return page;
    }
    public static void cleanup() {
        threadmap.remove(Thread.currentThread());
    }
    public static void maintenance() {
        try {
            for (Thread thread:threadmap.keySet()) {
                if (!thread.isAlive()) { threadmap.remove(thread); }
            }
        } catch (ConcurrentModificationException ignored) {}
    }

    private final Container root=new Container();

    public Page add(Container content) { root.add(content); return this; }
    private PageTemplate template=new MinimalPageTemplate();
    public Page template(PageTemplate template) { this.template=template; return this; }
    private boolean requiresauthentication=true;
    private Page unauthenticated() { requiresauthentication=false; return this; }
    private Map<String,String> parameters=new HashMap<>();
    private Page load(Map<String,String> parameters) { this.parameters=parameters; return this; }
    public String toString() {
        root.load(parameters);
        return root.toString();
    }

    private int responsecode= HttpStatus.SC_OK;
    public Container root() { return root; }
    public void responseCode(int responsecode) { this.responsecode=responsecode; }
    public int responseCode() { return responsecode; }
    private final Map<String,String> headersOut=new HashMap<>();
    public void addHeader(@Nonnull final String name, @Nonnull final String value) { headersOut.put(name,value); }
    @Nonnull public Map<String,String> getHeadersOut() { return headersOut; }
    @Nonnull public String render() {
        return template.getHeader()+root().toString()+template.getFooter();
    }
}

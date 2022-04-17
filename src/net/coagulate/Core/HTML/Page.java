package net.coagulate.Core.HTML;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

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
        final Thread us = Thread.currentThread();
        if (threadmap.containsKey(us)) { return threadmap.get(us); }
        final Page page = new Page();
        threadmap.put(us,page);
        return page;
    }
    public static void cleanup() {
        threadmap.remove(Thread.currentThread());
    }
    public static void maintenance() {
        try {
            for (final Thread thread : threadmap.keySet()) {
                if (!thread.isAlive()) {
                    threadmap.remove(thread);
                }
            }
        } catch (final ConcurrentModificationException ignored) {
        }
    }

    private Container root=new Container();

    public Page add(final Container content) {
        root.add(content);
        return this;
    }
    private PageTemplate template=new MinimalPageTemplate();

    public Page template(final PageTemplate template) {
        this.template = template;
        return this;
    }

    private Page unauthenticated() {
        final boolean requiresauthentication = false;
        return this; }
    private Map<String,String> parameters=new HashMap<>();

    private Page load(final Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public String toString() {
        root.load(parameters);
        return root.toString();
    }

    private int responsecode= HttpStatus.SC_OK;

    public Container root() {
        return root;
    }

    public void responseCode(final int responsecode) {
        this.responsecode = responsecode;
    }

    public int responseCode() {
        return responsecode;
    }
    private final Map<String,String> headersOut=new HashMap<>();
    public void addHeader(@Nonnull final String name, @Nonnull final String value) { headersOut.put(name,value); }
    @Nonnull public Map<String,String> getHeadersOut() { return headersOut; }
    @Nonnull public String render() {
        return template.getHeader()+root().toString()+template.getFooter();
    }

    public void resetRoot() {
        root=new Container();
    }

    ContentType contentType=null;

    public ContentType contentType() {
        return contentType;
    }

    public void contentType(@Nonnull final ContentType contentType) {
        this.contentType = contentType;
    }
}

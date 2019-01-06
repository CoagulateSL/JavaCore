package net.coagulate.Core.HTML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coagulate.Core.HTML.Outputs.Renderable;

/**
 *
 * @author Iain Price
 */
public class Page implements Renderable {
    // hmm
    private static final Map<Thread,Page> threadstate=new HashMap<>();
    public static Page create() {
        synchronized(threadstate) { 
            Page s=new Page();
            threadstate.put(Thread.currentThread(), s);
            return s;
        }
    }
    public static Page get() {
        synchronized(threadstate) { 
            Page s=threadstate.get(Thread.currentThread());
            if (s==null) {
                s=new Page();
                threadstate.put(Thread.currentThread(),s);
            }
            return s;
        }
    }
    public static void cleanup() {
        Set<Thread> removeme=new HashSet<>();
        synchronized(threadstate) {
            for (Thread t:threadstate.keySet()) {
                if (!t.isAlive()) { removeme.add(t); }
            }
            for (Thread t:removeme) { threadstate.remove(t); }
        }
    }
    public static void destroy() { synchronized(threadstate) { threadstate.remove(Thread.currentThread()); } }
    
    List<Renderable> content=new ArrayList<>();

    @Override
    public String asHtml() {
        String resp="";
        for (Renderable r:content) {
            resp+=r.asHtml()+"\n";
        }
        return resp;
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> all=new HashSet<Renderable>();
        all.addAll(content);
        return all;
    }
    
    public Page add(Renderable r) { content.add(r); return this; }
}

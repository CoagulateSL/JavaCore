package net.coagulate.Core.HTML;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Page {

    private static Map<Thread,Page> threadmap=new ConcurrentHashMap<>();

    /** Returns a per-thread page object
     *
     * @return Thread's page
     */
    public static Page getPage() {
        Thread us=Thread.currentThread();
        if (threadmap.containsKey(us)) { return threadmap.get(us); }
        Page page=new Page();
        threadmap.put(us,page);
        return page;
    }

    public static void maintenance() {
        try {
            for (Thread thread:threadmap.keySet()) {
                if (!thread.isAlive()) { threadmap.remove(thread); }
            }
        } catch (ConcurrentModificationException ignored) {}
    }

}

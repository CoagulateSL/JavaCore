package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.coagulate.Core.Tools.UnixTime.getUnixTime;

public class Cache <T> {

    private final static Map<String,Cache<?>> caches=new ConcurrentHashMap<>();

    public static <S> Cache<S> getCache(@Nonnull String name) {
        name=name.toLowerCase();
        if (caches.containsKey(name)) {
            //noinspection unchecked
            return (Cache<S>)caches.get(name);
        }
        return makeCache(name);
    }

    private static synchronized <S> Cache<S> makeCache(@Nonnull String name) {
        name=name.toLowerCase();
        if (caches.containsKey(name)) {
            //noinspection unchecked
            return (Cache<S>)caches.get(name);
        }
        Cache<S> cache=new Cache<>();
        caches.put(name,cache);
        return cache;
    }

    public static void maintenance() {
        Collection<Cache<?>> cacheset=caches.values();
        for (Cache<?> cache:cacheset) {
            try { cache.maintenanceThis(); }
            catch (ConcurrentModificationException e) {
                Logger.getLogger("CacheMaintenance").log(Level.FINE,"Cache maintenance died",e);
            }
        }
    }

    private void maintenanceThis() {
        int now=UnixTime.getUnixTime();
        Set<String> deleteset=new HashSet<>();
        for (Map.Entry<String,CacheElement<T>> row:cache.entrySet()) {
            if (now>row.getValue().expires) { deleteset.add(row.getKey()); }
        }
        for (String row:deleteset) { cache.remove(row); }
    }

    private final Map<String,CacheElement<T>> cache=new ConcurrentHashMap<>();

    /**
     * Get an object from the cache
     *
     * @param key Cache key
     *
     * @return Object from the cache
     *
     * @throws CacheMiss If there is no cached object by that key
     */
    public T get(@Nonnull final String key) throws CacheMiss {
        if (!cache.containsKey(key)) { throw new CacheMiss(); }
        final CacheElement<T> ele=cache.get(key);
        if (ele==null) { throw new CacheMiss(); }
        if (ele.expires<getUnixTime()) {
            cache.remove(key);
            throw new CacheMiss();
        }
        return ele.element;
    }

    /**
     * Store an element in the cache
     *
     * @param key             Cache key
     * @param object          Cache element
     * @param lifetimeseconds How long to cache for in seconds
     *
     * @return The object being cached (object)
     */
    @Nonnull
    public T put(@Nonnull final String key,
                    final T object,
                    final int lifetimeseconds) {
        final CacheElement<T> ele=new CacheElement<>(object,getUnixTime()+lifetimeseconds);
        cache.put(key,ele);
        return object;
    }

    public void purge(String name) {
        cache.remove(name);
    }

    public void purgeAll() { cache.clear(); }

    private static class CacheElement<T> {
        @Nonnull
        public final T element;
        public final int expires;

        public CacheElement(@Nonnull final T element,
                            final int expires) {
            this.element=element;
            this.expires=expires;
        }
    }

    public static class CacheMiss extends Exception {
        private static final long serialVersionUID=1L;
    }
}

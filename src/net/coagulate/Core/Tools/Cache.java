package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache <T> {

    private final static Map<String,Cache<?>> caches=new ConcurrentHashMap<>();
    private final int expiration;

    private Cache(int expiration) {
        this.expiration=expiration;
    }

    public static <S> Cache<S> getCache(@Nonnull String name,int expiration) {
        name=name.toLowerCase();
        if (caches.containsKey(name)) {
            //noinspection unchecked
            return (Cache<S>)caches.get(name);
        }
        return makeCache(name,expiration);
    }

    private static synchronized <S> Cache<S> makeCache(@Nonnull String name,int expiration) {
        name=name.toLowerCase();
        if (caches.containsKey(name)) {
            //noinspection unchecked
            return (Cache<S>)caches.get(name);
        }
        Cache<S> cache=new Cache<>(expiration);
        caches.put(name,cache);
        return cache;
    }

    public static void maintenance() {
        Collection<Cache<?>> cacheSet=caches.values();
        for (Cache<?> cache:cacheSet) {
            try { cache.maintenanceThis(); }
            catch (ConcurrentModificationException e) {
                Logger.getLogger("CacheMaintenance").log(Level.FINE,"Cache maintenance died",e);
            }
        }
    }

    public static List<CacheStats> getStats() {
        List<CacheStats> stats=new ArrayList<>();
        Set<String> cacheNames=new TreeSet<>(caches.keySet());
        for (String cacheName:cacheNames) {
            Cache<?> cache=caches.get(cacheName);
            if (cache!=null) {
                stats.add(new CacheStats(cacheName, cache.cache.size(),cache.cacheHit,cache.cacheMiss));
            }
        }
        return stats;
    }
    public static class CacheStats {
        public final String cacheName;
        public final int size;
        public final int cacheHit;
        public final int cacheMiss;

        public CacheStats(String cacheName, int size, int cacheHit, int cacheMiss) {
            this.cacheName=cacheName;
            this.size=size;
            this.cacheHit=cacheHit;
            this.cacheMiss=cacheMiss;
        }
    }

    private void maintenanceThis() {
        int now=UnixTime.getUnixTime();
        Set<Object> deleteSet=new HashSet<>();
        for (Map.Entry<Object,CacheElement<T>> row:cache.entrySet()) {
            if (now>row.getValue().expires) { deleteSet.add(row.getKey()); }
        }
        for (Object row:deleteSet) { cache.remove(row); }
    }

    private final Map<Object,CacheElement<T>> cache=new ConcurrentHashMap<>();

    private int cacheHit=0;
    private int cacheMiss=0;

    /**
     * Get an object from the cache
     *
     * @param key Cache key
     * @param supplier Functional Supplier for the value, if not cached
     *
     * @return Object from the cache
     */
    public T get(@Nonnull final Object key, Supplier<T> supplier) {
        int now=UnixTime.getUnixTime();
        if (cache.containsKey(key)) {
            if (cache.get(key).expires<now) { cache.remove(key); }
        }
        CacheElement<T> cached=cache.getOrDefault(key,null);
        if (cached==null) {
            cached=new CacheElement<>(supplier.get(),UnixTime.getUnixTime()+expiration);
            cache.put(key,cached);
            cacheMiss++;
        } else { cacheHit++; }
        return cached.element;
    }

    public void purge(Object name) {
        cache.remove(name);
    }

    public void purgeAll() { cache.clear(); }

    public void set(Object key, T value) {
        cache.put(key,new CacheElement<>(value,expiration));
    }

    private static class CacheElement<T> {
        @Nullable
        public final T element;
        public final int expires;

        public CacheElement(@Nullable final T element,
                            final int expires) {
            this.element=element;
            this.expires=expires;
        }
    }
}

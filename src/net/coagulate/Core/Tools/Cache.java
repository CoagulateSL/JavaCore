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
    public static List<CacheStats> getSummarisedStats() {
        List<CacheStats> stats = getStats();
        Map<String,CacheStats> statMap=new HashMap<>();
        for (CacheStats stat:stats) {
            String name=stat.cacheName;
            String[] parts=name.split("/");
            if (parts.length>2) { name=parts[0]+"/"+parts[1]; }
            if (!statMap.containsKey(name)) { statMap.put(name,new CacheStats(name,0,0,0)); }
            CacheStats newStat=statMap.get(name);
            newStat.cacheHit+=stat.cacheHit;
            newStat.cacheMiss+=stat.cacheMiss;
            newStat.size+=stat.size;
        }
        Set<String> sorted=new TreeSet<>(statMap.keySet());
        List<CacheStats> ret=new ArrayList<>();
        for (String addName:sorted) { ret.add(statMap.get(addName)); }
        return ret;
    }
    public static class CacheStats {
        public final String cacheName;
        public int size;
        public long cacheHit;
        public long cacheMiss;

        public CacheStats(String cacheName, int size, long cacheHit, long cacheMiss) {
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

    private long cacheHit=0;
    private long cacheMiss=0;

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
        CacheElement<T> cached=cache.getOrDefault(key,null);
        if (cached!=null) {
            if (cached.expires<now) { cache.remove(key); }
        }
        cached=cache.getOrDefault(key,null);
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

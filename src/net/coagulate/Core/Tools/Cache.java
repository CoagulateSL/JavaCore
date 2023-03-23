package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache<U,T> {
	
	/**
	 * If set to true forces all cache elements to refresh every couple of seconds, regardless of their preference.
	 * For use during node transitions.
	 */
	public static        boolean                eagerCacheFlush=false;
	private static final Map<String,Cache<?,?>> caches         =new ConcurrentSkipListMap<>();
	private final        int                    expiration;
	private final Map<U,CacheElement<T>> cache=new ConcurrentSkipListMap<>();
	private long cacheHit;
	private long cacheMiss;
	
	private Cache(final int expiration) {
		this.expiration=expiration;
	}
	
	@SuppressWarnings("unchecked")
	public static <R,S> Cache<R,S> getCache(@Nonnull String name,final int expiration) {
		name=name.toLowerCase();
		if (caches.containsKey(name)) {
			return (Cache<R,S>)caches.get(name);
		}
		return makeCache(name,expiration);
	}
	
	@SuppressWarnings("unchecked")
	private static synchronized <R,S> Cache<R,S> makeCache(@Nonnull String name,final int expiration) {
		name=name.toLowerCase();
		if (caches.containsKey(name)) {
			return (Cache<R,S>)caches.get(name);
		}
		final Cache<R,S> cache=new Cache<>(expiration);
		caches.put(name,cache);
		return cache;
	}
	
	public static void maintenance() {
		final Collection<Cache<?,?>> cacheSet=caches.values();
		for (final Cache<?,?> cache: cacheSet) {
			try {
				cache.maintenanceThis();
			} catch (final ConcurrentModificationException e) {
				Logger.getLogger("CacheMaintenance").log(Level.FINE,"Cache maintenance died",e);
			}
		}
	}
	
	private void maintenanceThis() {
		final int now=UnixTime.getUnixTime();
		final Set<U> deleteSet=new HashSet<>();
		for (final Map.Entry<U,CacheElement<T>> row: cache.entrySet()) {
			if (now>row.getValue().expires) {
				deleteSet.add(row.getKey());
			}
		}
		for (final U row: deleteSet) {
			cache.remove(row);
		}
	}
	
	public static List<CacheStats> getSummarisedStats() {
		final List<CacheStats> stats=getStats();
		final Map<String,CacheStats> statMap=new HashMap<>();
		for (final CacheStats stat: stats) {
			String name=stat.cacheName;
			final String[] parts=name.split("/");
			if (parts.length>2) {
				name=parts[0]+"/"+parts[1];
			}
			if (!statMap.containsKey(name)) {
				statMap.put(name,new CacheStats(name,0,0,0));
			}
			final CacheStats newStat=statMap.get(name);
			newStat.cacheHit+=stat.cacheHit;
			newStat.cacheMiss+=stat.cacheMiss;
			newStat.size+=stat.size;
		}
		final Set<String> sorted=new TreeSet<>(statMap.keySet());
		final List<CacheStats> ret=new ArrayList<>();
		for (final String addName: sorted) {
			ret.add(statMap.get(addName));
		}
		return ret;
	}
	
	public static List<CacheStats> getStats() {
		final List<CacheStats> stats=new ArrayList<>();
		final Set<String> cacheNames=new TreeSet<>(caches.keySet());
		for (final String cacheName: cacheNames) {
			final Cache<?,?> cache=caches.get(cacheName);
			if (cache!=null) {
				stats.add(new CacheStats(cacheName,cache.cache.size(),cache.cacheHit,cache.cacheMiss));
			}
		}
		return stats;
	}
	
	public static String cacheStatus() {
		return eagerCacheFlush?"Caching is eagerly refreshed":"Normal caching operations";
	}

	/**
	 * The name is a lie, this will force the cache to expire things quickly, but not terminate the cache.
	 */
	public static void disableCache() {
		eagerCacheFlush=true;
	}
	
	/**
	 * Restores standard caching operations and purges all the caches in the process...
	 */
	public static synchronized void enableCache() {
		eagerCacheFlush=false;
		// oh god why, dont do this
		// caches=new ConcurrentHashMap<>(); // merry garbage collection day?
		// the using classes have a static reference, you MUST purge all the content instead
		for (final Cache cache:
		     caches.values()) {
			cache.purgeAll();
		}
	}
	
	/**
	 * Get an object from the cache
	 *
	 * @param key      Cache key
	 * @param supplier Functional Supplier for the value, if not cached
	 * @return T from the cache
	 */
	public T get(@Nonnull final U key,final Supplier<T> supplier) {
		final int now=UnixTime.getUnixTime();
		CacheElement<T> cached=cache.getOrDefault(key,null);
		if (cached!=null) {
			if (cached.expires<now||(eagerCacheFlush&&(UnixTime.getUnixTime()-cached.cachedAt)>5)) {
				cache.remove(key);
				cached=null;
			}
		}
		if (cached==null) {
			cached=new CacheElement<>(supplier.get(),UnixTime.getUnixTime()+expiration);
			cache.put(key,cached);
			cacheMiss++;
		} else {
			cacheHit++;
		}
		return cached.element;
	}
	
	public void purge(final U name) {
		cache.remove(name);
	}
	
	public void purgeAll() {
		cache.clear();
	}
	
	public void set(final U key,final T value) {
		cache.put(key,new CacheElement<>(value,expiration));
	}
	
	public static class CacheStats {
		public final String cacheName;
		public       int    size;
		public       long   cacheHit;
		public       long   cacheMiss;
		
		public CacheStats(final String cacheName,final int size,final long cacheHit,final long cacheMiss) {
			this.cacheName=cacheName;
			this.size=size;
			this.cacheHit=cacheHit;
			this.cacheMiss=cacheMiss;
		}
	}
	
	private static class CacheElement<T> {
		@Nullable public final T   element;
		public final           int expires;
		public final           int cachedAt;
		
		public CacheElement(@Nullable final T element,final int expires) {
			this.cachedAt=UnixTime.getUnixTime();
			this.element=element;
			this.expires=expires;
		}
	}
}

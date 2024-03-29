package net.coagulate.Core.HTTP;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Iain Price
 */
public final class URLDistribution implements HttpRequestHandlerMapper {
	
	public static final boolean                  DEBUG_LOOKUP=false;
	private static      Logger                   logger;
	private static URLDistribution singleton;
	final               Map<String,URLMapper<?>> prefixes    =new HashMap<>();
	
	private URLDistribution() {
	
	}
	
	public static void register(@Nonnull final String prefix,@Nonnull final URLMapper<?> handler) {
		if (DEBUG_LOOKUP) {
			logger().fine("Registering "+prefix+" to "+handler);
		}
		getPageMapper().prefixes.put(prefix.toLowerCase(),handler);
	}
	
	private static Logger logger() {
		if (logger==null) {
			logger=Logger.getLogger(URLDistribution.class.getCanonicalName());
		}
		return logger;
	}
	
	public static synchronized URLDistribution getPageMapper() {
		if (singleton==null) {
			singleton=new URLDistribution();
		}
		return singleton;
	}
	
	@Override
	public HttpRequestHandler lookup(final HttpRequest httpRequest) {
		final String url=httpRequest.getRequestLine().getUri().toLowerCase();
		if (DEBUG_LOOKUP) {
			logger().fine("HTTP Distribution Lookup for "+url);
		}
		if (prefixes.containsKey(url)) {
			if (DEBUG_LOOKUP) {
				logger().fine("HTTP Distribution Lookup for "+url+" exact match "+prefixes.get(url));
			}
			return prefixes.get(url);
		}
		int matchLength=-1;
		URLMapper<?> match=null;
		for (final Map.Entry<String,URLMapper<?>> prefix: prefixes.entrySet()) {
			if (DEBUG_LOOKUP) {
				System.out.println("Compare to "+prefix.getKey());
			}
			if (url.startsWith(prefix.getKey())) {
				if (prefix.getKey().length()>matchLength) {
					matchLength=prefix.getKey().length();
					match=prefix.getValue();
				}
			}
		}
		if (DEBUG_LOOKUP) {
			logger().fine("HTTP Distribution Lookup for "+url+" prefix match len "+matchLength+" "+prefixes.get(url));
		}
		return match;
	}
}

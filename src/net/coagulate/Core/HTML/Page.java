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
	private final Map<String,String> headersOut=new HashMap<>();
	ContentType contentType;
	private Container root=new Container();
	private PageTemplate template=new MinimalPageTemplate();
	private Map<String,String> parameters=new HashMap<>();
	private int responsecode=HttpStatus.SC_OK;
	
	/**
	 * Returns a per-thread page object
	 *
	 * @return Thread's page
	 */
	public static Page page() {
		final Thread us=Thread.currentThread();
		if (threadmap.containsKey(us)) {
			return threadmap.get(us);
		}
		final Page page=new Page();
		threadmap.put(us,page);
		return page;
	}
	
	public static void cleanup() {
		threadmap.remove(Thread.currentThread());
	}
	
	public static void maintenance() {
		try {
			for (final Thread thread: threadmap.keySet()) {
				if (!thread.isAlive()) {
					threadmap.remove(thread);
				}
			}
		} catch (final ConcurrentModificationException ignored) {
		}
	}
	
	public Page add(final Container content) {
		root.add(content);
		return this;
	}
	
	public Page template(final PageTemplate template) {
		this.template=template;
		return this;
	}
	
	private Page unauthenticated() {
		final boolean requiresAuth=false;
		return this;
	}
	
	private Page load(final Map<String,String> parameters) {
		this.parameters=parameters;
		return this;
	}
	
	public String toString() {
		root.load(parameters);
		return root.toString();
	}
	
	public void responseCode(final int responsecode) {
		this.responsecode=responsecode;
	}
	
	public int responseCode() {
		return responsecode;
	}
	
	public void addHeader(@Nonnull final String name,@Nonnull final String value) {
		headersOut.put(name,value);
	}
	
	@Nonnull
	public Map<String,String> getHeadersOut() {
		return headersOut;
	}
	
	@Nonnull
	public String render() {
		return template.getHeader()+root().toString()+template.getFooter();
	}
	
	public Container root() {
		return root;
	}
	
	public void resetRoot() {
		root=new Container();
	}
	
	public ContentType contentType() {
		return contentType;
	}
	
	public void contentType(@Nonnull final ContentType contentType) {
		this.contentType=contentType;
	}
}

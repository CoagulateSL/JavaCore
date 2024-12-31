package net.coagulate.Core.HTTP;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.Exceptions.SystemException;
import net.coagulate.Core.Exceptions.UserException;
import net.coagulate.Core.HTML.Page;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

/**
 * Guides a URL through the process of handling the request.
 * <p>
 * The lifecycle for a request is roughly as follows
 * (URLDistributor assigns URL to a URLMapper, which is a subclass of us, and we perform):
 * handle() calls _handle() which calls (+ denotes abstract method)
 * earlyInitialiseState()
 * * if failed, getDefaultPage()
 * * processInputs() -> processUri, processPostData->processPostEntity, processCookies, +initialiseState
 * * +loadSession()
 * * lookupPage() -> lookupPageFromUri()
 * * +checkAuthenticationNeeded() -> authenticationPage()
 * * +executePage()
 * * processOutput() -> responseCode(from Page) and getContentType();
 * * +cleanup()
 *
 * @param <T> Type of handler (e.g. Method is a common choice)
 */
public abstract class URLMapper<T> implements HttpRequestHandler {
	private static final boolean       DEBUG_PARAMS =false;
	private static final boolean       DEBUG_MAPPING=false;
	public static        boolean       LOGREQUESTS  =false;
	final                Map<String,T> prefixes     =new HashMap<>();
	final                Map<String,T> exact        =new HashMap<>();
	private final        Logger        logger;
	
	protected URLMapper() {
		logger=Logger.getLogger(getClass().getCanonicalName());
	}
	
	@Override
	public final void handle(final HttpRequest httpRequest,
	                         final HttpResponse httpResponse,
	                         final HttpContext httpContext) {
		// a "generally extendable" pipeline for handling a HTTP Request
		// wrapped in a exception handler.  don't leak exceptions! bad subclasses!
		try {
			_handle(httpRequest,httpContext,httpResponse);
		} catch (final Throwable e) {
			logger.log(SEVERE,"Exception escaped page handler",e);
			throw new SystemImplementationException("Exception escaped page handlers",e);
		}
	}
	
	/**
	 * Handler for a connection.
	 * <p>
	 * Lifecycle is:
	 * Process inputs
	 * Load session data
	 * Check authentication (is present if required)
	 * Execute target (or authenticationRequired target)
	 * Output results
	 *
	 * @param request  The HttpRequest
	 * @param context  HttpContext
	 * @param response The HttpResponse
	 */
	protected final void _handle(final HttpRequest request,final HttpContext context,final HttpResponse response) {
		int inputSize=-1;
		int outputSize=-1;
		final Date startTime=new Date();
		Thread.currentThread().setName("Handler for "+request.getRequestLine().getUri());
		try {
			earlyInitialiseState(request,context);
			inputSize=processInputs(request,context);
			loadSession();
			T content=lookupPage(request);
			if (content==null) {
				content=getDefaultPage();
			}
			if (checkAuthenticationNeeded(content)) {
				content=authenticationPage();
			}
			if (content instanceof Method) {
				Thread.currentThread()
				      .setName("Invoking "+(((Method)content).getDeclaringClass().getCanonicalName()+"."+
				                            ((Method)content).getName()).replaceFirst("net.coagulate.",""));
			} else {
				if (content==null) {
					Thread.currentThread().setName("Invoking null");
				} else {
					Thread.currentThread().setName("Invoking "+content.getClass().getName());
				}
			}
			executePage(content);
			outputSize=processOutput(response,content);
		} catch (final UserException ue) {
			renderUserError(request,context,response,ue);
		} catch (final SystemException se) {
			renderSystemError(request,context,response,se);
		} catch (final InvocationTargetException ite) {
			final Throwable content=ite.getCause();
			boolean handled=false;
			if (UserException.class.isAssignableFrom(content.getClass())) {
				renderUserError(request,context,response,(UserException)content);
				handled=true;
			}
			if (SystemException.class.isAssignableFrom(content.getClass())) {
				renderSystemError(request,context,response,(SystemException)content);
				handled=true;
			}
			if (!handled) {
				renderUnhandledError(request,context,response,content);
			}
		} catch (final Throwable t) {
			renderUnhandledError(request,context,response,t);
		} finally {
			cleanup();
			if (LOGREQUESTS) {
				System.out.println(
						"ReqLog:'"+request.getRequestLine().getUri()+"','"+Thread.currentThread().getName()+"',"+
						inputSize+","+outputSize+","+((new Date().getTime())-(startTime.getTime())));
			}
			Thread.currentThread().setName("Idle connection handler for "+getClass().getSimpleName());
		}
	}
	
	protected void earlyInitialiseState(final HttpRequest request,final HttpContext context) {
		Page.cleanup();
	}
	
	/**
	 * Collect all the input data from the input streams.
	 * <p>
	 * Notably:
	 * Process URI Encoded data in the URI
	 * Process whatever is Post-ed, if appropriate
	 * Load cookies
	 *
	 * @param request The HttpRequest
	 * @param context HttpContext
	 */
	protected int processInputs(final HttpRequest request,final HttpContext context) {
		final Map<String,String> parameters=new TreeMap<>();
		processUri(request,parameters);
		final int inputSize=processPostData(request,parameters);
		final Map<String,String> cookies=new TreeMap<>();
		processCookies(request,cookies);
		initialiseState(request,context,parameters,cookies);
		return inputSize;
	}
	
	/**
	 * Load data related to any existing session
	 */
	protected abstract void loadSession();
	
	/**
	 * Look up the target, whatever type of thing that might be
	 *
	 * @return Page handler
	 */
	@Nullable
	protected T lookupPage(final HttpRequest request) {
		if (DEBUG_MAPPING) {
			System.out.println("REQUEST URI:"+request.getRequestLine().getUri());
		}
		final String line=request.getRequestLine().getUri().toLowerCase();
		return lookupPageFromUri(line);
	}
	
	// ====================== INPUT =================
	
	protected T getDefaultPage() {
		return lookupPageFromUri("/404");
	}
	
	/**
	 * Check authentication - is the user authenticated IF the target page requires authentication.
	 *
	 * @return true if authentication is OK, false if authentication is required
	 */
	protected abstract boolean checkAuthenticationNeeded(T content);
	
	/**
	 * A page handler that gives a login page, if not authenticated and requires authentication.
	 *
	 * @return An implementation for an authentication page
	 */
	protected T authenticationPage() {
		final T page=lookupPageFromUri("/login");
		if (page==null) {
			throw new SystemImplementationException("There is no page at /login during authentication step");
		}
		return page;
	}
	
	/**
	 * Run the actual page.
	 *
	 * @param content The implementation to execute
	 */
	protected abstract void executePage(T content) throws InvocationTargetException;
	
	/**
	 * Render the final output back into a HttpEntity
	 *
	 * @param response The HttpResponse object to direct output to
	 */
	protected int processOutput(final HttpResponse response,final T content) {
		String stringOutput;
		try {
			stringOutput=Page.page().render();
		} catch (@Nonnull final UserException ue) {
			Logger.getLogger(getClass().getName()).log(WARNING,"PageHandlerCaught",ue);
			stringOutput="<p>Exception: "+ue.getLocalizedMessage()+"</p>";
		}
		response.setEntity(new StringEntity(stringOutput,getContentType()));
		for (final Map.Entry<String,String> entry: Page.page().getHeadersOut().entrySet()) {
			response.addHeader(entry.getKey(),entry.getValue());
		}
		response.setStatusCode(Page.page().responseCode());
		return stringOutput.length();
	}
	
	// ====================== LOAD =======================
	
	protected abstract void renderUserError(HttpRequest request,
	                                        HttpContext context,
	                                        HttpResponse response,
	                                        UserException userException);
	
	// ===================== PAGE LOOKUP ===================
	
	protected abstract void renderSystemError(HttpRequest request,
	                                          HttpContext context,
	                                          HttpResponse response,
	                                          SystemException systemException);
	
	protected abstract void renderUnhandledError(HttpRequest request,
	                                             HttpContext context,
	                                             HttpResponse response,
	                                             Throwable t);
	
	protected abstract void cleanup();
	
	/**
	 * Process data from the URI
	 *
	 * @param request    The source HttpRequest
	 * @param parameters The parameter map to update
	 */
	protected void processUri(final HttpRequest request,final Map<String,String> parameters) {
		try {
			final List<NameValuePair> uriParams=
					URLEncodedUtils.parse(new URI(request.getRequestLine().getUri()),StandardCharsets.UTF_8);
			for (final NameValuePair up: uriParams) {
				parameters.put(up.getName(),up.getValue());
				if (DEBUG_PARAMS) {
					System.out.println("Imported URI parameter '"+up.getName()+"'='"+up.getValue()+"'");
				}
			}
		} catch (final URISyntaxException e) {
			logger.log(WARNING,"Failed to process URI from the request: "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * Process entity (post) data, might be JSON, or www-form etc etc
	 * <p>
	 * The default implementation assumes a form-post of www-uri-encoded data.
	 *
	 * @param request    The source HttpRequest
	 * @param parameters The parameter map to update
	 */
	protected int processPostData(final HttpRequest request,final Map<String,String> parameters) {
		if (request instanceof final HttpEntityEnclosingRequest r) {
			final int inputSize=(int)r.getEntity().getContentLength();
			processPostEntity(r.getEntity(),parameters);
			return inputSize;
		}
		return 0;
	}
	
	/**
	 * Load cookie data from the HTTP headers
	 *
	 * @param request HttpRequest
	 * @param cookies Map to load cookies in to.
	 */
	protected void processCookies(final HttpRequest request,final Map<String,String> cookies) {
		for (final Header header: request.getHeaders("Cookie")) {
			for (final String component: header.getValue().split(";")) {
				final String[] kv=component.split("=");
				if (kv.length==2) {
					//System.out.println(kv[0]+"="+kv[1]);
					cookies.put(kv[0].trim(),kv[1].trim());
				} else {
					Logger.getLogger(getClass().getName())
					      .log(WARNING,
					           "Unusual cookie element to parse in line "+header.getValue()+" piece "+component);
				}
			}
		}
	}
	
	/**
	 * Store the results of parsing the URI/post data
	 *
	 * @param request    HttpRequest
	 * @param context    HttpContext
	 * @param parameters A map of string to string parameters read from the URI and written on top of from the form post
	 * @param cookies    A map of string to string cookies loaded from the HTTP Headers
	 */
	protected abstract void initialiseState(HttpRequest request,
	                                        HttpContext context,
	                                        Map<String,String> parameters,
	                                        Map<String,String> cookies);
	
	protected T lookupPageFromUri(final String uri) {
		if (exact.containsKey(uri)) {
			if (DEBUG_MAPPING) {
				System.out.println("Exact match "+exact.get(uri).getClass().getCanonicalName());
			}
			return exact.get(uri);
		} else {
			if (DEBUG_MAPPING) {
				System.out.println("Exact match against "+exact.size()+" elements returned nothing");
			}
		}
		for (final String s: exact.keySet()) {
			if (DEBUG_MAPPING) {
				System.out.println(s);
			}
		}
		String matchedPrefix="";
		int matchedPrefixLength=-1;
		T matchedHandler=null;
		for (final String prefix: prefixes.keySet()) {
			if (uri.startsWith(prefix)) {
				if (prefix.length()>matchedPrefixLength) {
					matchedPrefix=prefix;
					matchedHandler=prefixes.get(prefix);
					matchedPrefixLength=prefix.length();
				}
			}
		}
		if (DEBUG_MAPPING) {
			System.out.println("Matched prefix "+matchedPrefix+" for url "+uri);
			if (matchedHandler==null) {
				System.out.println("Prefix match returned null match, this is now a 404");
			} else {
				System.out.println("Prefix match "+matchedHandler.getClass().getCanonicalName());
			}
		}
		if (matchedHandler==null) {
			logger.log(Level.FINE,"Requested URI '{0}' was not mapped to a page - returning 404.",uri);
			return null;
		}
		return matchedHandler;
	}
	
	// ====================== EXECUTION ============================
	
	protected ContentType getContentType() {
		//System.out.println("Page content type is "+Page.page().contentType());
		return Page.page().contentType()==null?ContentType.TEXT_HTML:Page.page().contentType();
	}
	
	/**
	 * Process the extracted posted entity, if one exists
	 *
	 * @param entity     The HttpEntity
	 * @param parameters The parameters map to update
	 */
	protected void processPostEntity(final HttpEntity entity,final Map<String,String> parameters) {
		try {
			if (DEBUG_PARAMS) {
				System.out.println("Post entity is "+entity.getClass());
			}
			final BasicHttpEntity e=(BasicHttpEntity)entity;
			if (DEBUG_PARAMS) {
				System.out.println("Post entity content type is "+e.getContentType().getValue());
			}
			if ("application/x-www-form-urlencoded".equalsIgnoreCase(e.getContentType().getValue())) {
				final List<NameValuePair> map=URLEncodedUtils.parse(entity);
				for (final NameValuePair kv: map) {
					parameters.put(kv.getName(),kv.getValue());
					if (DEBUG_PARAMS) {
						System.out.println("Imported URIEncoded parameter '"+kv.getName()+"'='"+kv.getValue()+"'");
					}
				}
				return;
			}
			if (e.getContentType().getValue().startsWith("multipart/form-data; boundary=")) {
				// this is bad and prone to failure from any number of valid formatting variations:)
				final String boundary=e.getContentType().getValue().replaceAll("multipart/form-data; boundary=","");
				final MultipartStream stream=
						new MultipartStream(entity.getContent(),boundary.getBytes(StandardCharsets.UTF_8));
				boolean moreParts=stream.skipPreamble();
				while (moreParts) {
					final String header=stream.readHeaders();
					final Pattern regexp=Pattern.compile(".*; ?name=\"([^\"]+)\".*",Pattern.MULTILINE|Pattern.DOTALL);
					final Matcher matcher=regexp.matcher(header);
					String name=null;
					if (matcher.matches()) {
						name=matcher.group(1);
					}
					final ByteArrayOutputStream baos=new ByteArrayOutputStream();
					stream.readBodyData(baos);
					if (name!=null) {
						parameters.put(name,baos.toString());
						if (DEBUG_PARAMS) {
							System.out.println("Imported multipart data '"+name+"'='"+baos+"'");
						}
					} else {
						logger.warning(
								"Failed to consume multipart/form-data section with header "+header+" and body "+baos);
					}
					moreParts=stream.readBoundary();
				}
				return;
			}
			System.out.println(new String(entity.getContent().readAllBytes()));
			logger.log(SEVERE,"Do not know how to decode "+e.getContentType().getValue());
			throw new SystemImplementationException(
					"There is no HTTP POST handler for content type "+e.getContentType().getValue());
		} catch (final IOException e) {
			logger.log(WARNING,"Failed to Decode posted entity",e);
		}
	}
	
	/**
	 * Add an exact match URL.
	 *
	 * @param url    The URL
	 * @param target The target implementer
	 */
	public void exact(String url,final T target) {
		url=url.toLowerCase();
		if (exact.containsKey(url)) {
			throw new SystemImplementationException(
					"Duplicate exact url registered:"+url+" between "+target+" and "+exact.get(url));
		}
		exact.put(url,target);
	}
	
	/**
	 * Add a prefix match URL
	 *
	 * @param url    The URL prefix
	 * @param target The target implementer
	 */
	public void prefix(String url,final T target) {
		url=url.toLowerCase();
		if (prefixes.containsKey(url)) {
			throw new SystemImplementationException(
					"Duplicate prefix url registered:"+url+" between "+target+" and "+exact.get(url));
		}
		prefixes.put(url,target);
	}
	
}

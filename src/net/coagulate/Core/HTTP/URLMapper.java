package net.coagulate.Core.HTTP;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.Exceptions.SystemException;
import net.coagulate.Core.Exceptions.UserException;
import net.coagulate.Core.HTML.Page;
import net.coagulate.SL.SL;
import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

/** Guides a URL through the process of handling the request.
 *
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
    private static final boolean DEBUG_PARAMS = false;
    private final Logger logger;
    private static final boolean DEBUG_MAPPING = false;

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) {
        // a "generally extendable" pipeline for handling a HTTP Request
        // wrapped in a exception handler.  don't leak exceptions! bad subclasses!
        try {
            _handle(request, context, response);
        } catch (Throwable e) {
            logger.log(SEVERE, "Exception escaped page handler", e);
            throw new SystemImplementationException("Exception escaped page handlers", e);
        }
    }

    public URLMapper() {
        logger = Logger.getLogger(getClass().getCanonicalName());
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
     *  @param request  The HttpRequest
     * @param context HttpContext
     * @param response The HttpResponse
     */
    protected void _handle(HttpRequest request, HttpContext context, HttpResponse response) {
        try {
            earlyInitialiseState(request, context);
            processInputs(request, context);
            loadSession();
            T content = lookupPage(request);
            if (content == null) {
                content = getDefaultPage();
            }
            if (checkAuthenticationNeeded(content)) {
                content = authenticationPage();
            }
            executePage(content);
            processOutput(response, content);
        }
        catch (UserException ue) { renderUserError(request,context,response,ue); }
        catch (SystemException se) { renderSystemError(request,context,response,se); }
        catch (InvocationTargetException ite) {
            Throwable content=ite.getCause();
            boolean handled=false;
            if (UserException.class.isAssignableFrom(content.getClass())) { renderUserError(request,context,response, (UserException) content); handled=true; }
            if (SystemException.class.isAssignableFrom(content.getClass())) { renderSystemError(request,context,response, (SystemException) content); handled=true; }
            if (!handled) { renderUnhandledError(request,context,response,content); }
        }
        catch (Throwable t) { renderUnhandledError(request,context,response,t); }
        finally { cleanup(); }
    }

    protected abstract void renderUnhandledError(HttpRequest request, HttpContext context, HttpResponse response, Throwable t);

    protected abstract void renderSystemError(HttpRequest request, HttpContext context, HttpResponse response, SystemException ite);

    protected abstract void renderUserError(HttpRequest request, HttpContext context, HttpResponse response, UserException ite);

    protected void earlyInitialiseState(HttpRequest request, HttpContext context) {
        Page.cleanup();
    }

    protected T getDefaultPage() {
        T m=lookupPageFromUri("/404");
        if (m==null) { throw new SystemImplementationException("404 page does not exist"); }
        return m;
    }

    /**
     * Collect all the input data from the input streams.
     * <p>
     * Notably:
     * Process URI Encoded data in the URI
     * Process whatever is Post-ed, if appropriate
     * Load cookies
     *  @param request The HttpRequest
     * @param context HttpContext
     */
    protected void processInputs(HttpRequest request, HttpContext context) {
        final Map<String, String> parameters = new TreeMap<>();
        processUri(request, parameters);
        processPostData(request, parameters);
        final Map<String, String> cookies = new TreeMap<>();
        processCookies(request, cookies);
        initialiseState(request,context,parameters,cookies);
    }

    // ====================== INPUT =================

    /**
     * Process data from the URI
     *
     * @param request    The source HttpRequest
     * @param parameters The parameter map to update
     */
    protected void processUri(HttpRequest request, Map<String, String> parameters) {
        try {
            final List<NameValuePair> uriParams = URLEncodedUtils.parse(new URI(request.getRequestLine().getUri()), StandardCharsets.UTF_8);
            for (final NameValuePair up : uriParams) {
                parameters.put(up.getName(), up.getValue());
                if (DEBUG_PARAMS) {
                    System.out.println("Imported URI parameter '" + up.getName() + "'='" + up.getValue() + "'");
                }
            }
        } catch (URISyntaxException e) {
            logger.log(WARNING, "Failed to process URI from the request", e);
        }
    }

    /**
     * Process entity (post) data, might be JSON, or www-form etc etc
     * <p>
     * The default implementation assumes a form-post of www-uri-encoded data.
     *  @param request    The source HttpRequest
     * @param parameters The parameter map to update
     */
    protected void processPostData(HttpRequest request, Map<String, String> parameters) {
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntityEnclosingRequest r = (HttpEntityEnclosingRequest) request;
            processPostEntity(r.getEntity(), parameters);
        }
    }

    /**
     * Process the extracted posted entity, if one exists
     *  @param entity     The HttpEntity
     * @param parameters The parameters map to update
     */
    protected void processPostEntity(HttpEntity entity, Map<String, String> parameters) {
        try {
            final List<NameValuePair> map = URLEncodedUtils.parse(entity);
            for (final NameValuePair kv : map) {
                parameters.put(kv.getName(), kv.getValue());
                if (DEBUG_PARAMS) {
                    System.out.println("Imported POST parameter '" + kv.getName() + "'='" + kv.getValue() + "'");
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to URLDecode posted entity", e);
        }
    }

    /**
     * Load cookie data from the HTTP headers
     *
     * @param request HttpRequest
     * @param cookies Map to load cookies in to.
     */
    protected void processCookies(HttpRequest request, Map<String, String> cookies) {
        for (final Header header : request.getHeaders("Cookie")) {
            for (final String component : header.getValue().split(";")) {
                final String[] kv = component.split("=");
                if (kv.length != 2) {
                    SL.log().log(Level.WARNING, "Unusual cookie element to parse in line " + header.getValue() + " piece " + component);
                } else {
                    //System.out.println(kv[0]+"="+kv[1]);
                    cookies.put(kv[0].trim(), kv[1].trim());
                }
            }
        }
    }

    /**
     * Store the results of parsing the URI/post data
     *  @param request HttpRequest
     * @param context HttpContext
     * @param parameters A map of string to string parameters read from the URI and written on top of from the form post
     * @param cookies    A map of string to string cookies loaded from the HTTP Headers
     */
    protected abstract void initialiseState(HttpRequest request, HttpContext context, Map<String, String> parameters, Map<String, String> cookies);

    // ====================== LOAD =======================

    /**
     * Load data related to any existing session
     */
    protected abstract void loadSession();

    // ===================== PAGE LOOKUP ===================

    final Map<String, T> prefixes = new HashMap<>();
    final Map<String, T> exact = new HashMap<>();

    /**
     * Add an exact match URL.
     *
     * @param url    The URL
     * @param target The target implementer
     */
    public void exact(String url, T target) {
        url = url.toLowerCase();
        if (exact.containsKey(url)) {
            throw new SystemImplementationException("Duplicate exact url registered:" + url +" between "+target+" and "+exact.get(url));
        }
        exact.put(url, target);
    }

    /**
     * Add a prefix match URL
     *
     * @param url    The URL prefix
     * @param target The target implementer
     */
    public void prefix(String url, T target) {
        url = url.toLowerCase();
        if (prefixes.containsKey(url)) {
            throw new SystemImplementationException("Duplicate prefix url registered:" + url+" between "+target+" and "+exact.get(url));
        }
        prefixes.put(url, target);
    }

    /**
     * Look up the target, whatever type of thing that might be
     *
     * @return Page handler
     */
    @Nullable
    protected T lookupPage(HttpRequest request) {
        if (DEBUG_MAPPING) {
            System.out.println("REQUEST URI:" + request.getRequestLine().getUri());
        }
        final String line = request.getRequestLine().getUri().toLowerCase();
        return lookupPageFromUri(line);
    }
    protected T lookupPageFromUri(String line) {
        if (exact.containsKey(line)) {
            if (DEBUG_MAPPING) {
                System.out.println("Exact match " + exact.get(line).getClass().getCanonicalName());
            }
            return exact.get(line);
        } else {
            if (DEBUG_MAPPING) {
                System.out.println("Exact match against " + exact.size() + " elements returned nothing");
            }
        }
        for (final String s : exact.keySet()) {
            if (DEBUG_MAPPING) {
                System.out.println(s);
            }
        }
        String matchedPrefix = "";
        int matchedPrefixLength=-1;
        T matchedHandler = null;
        for (final String prefix : prefixes.keySet()) {
            if (line.startsWith(prefix)) {
                if (prefix.length() > matchedPrefixLength) {
                    matchedPrefix = prefix;
                    matchedHandler = prefixes.get(prefix);
                    matchedPrefixLength=prefix.length();
                }
            }
        }
        if (DEBUG_MAPPING) {
            System.out.println("Matched prefix "+matchedPrefix+" for url "+line);
            if (matchedHandler != null) {
                System.out.println("Prefix match " + matchedHandler.getClass().getCanonicalName());
            } else {
                System.out.println("Prefix match returned null match, this is now a 404");
            }
        }
        if (matchedHandler == null) {
            logger.log(Level.FINE, "Requested URI '{0}' was not mapped to a page - returning 404.", line);
            return null;
        }
        return matchedHandler;
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
        T page=lookupPageFromUri("/login");
        if (page==null) { throw new SystemImplementationException("There is no page at /login during authentication step"); }
        return page;
    }

    // ====================== EXECUTION ============================

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
    protected void processOutput(HttpResponse response, T content) {
        String stringOutput;
        try {
            stringOutput = Page.page().render();
        } catch (@Nonnull final UserException ue) {
            SL.log().log(WARNING, "PageHandlerCaught", ue);
            stringOutput = "<p>Exception: " + ue.getLocalizedMessage() + "</p>";
        }
        response.setEntity(new StringEntity(stringOutput, getContentType()));
        for (Map.Entry<String,String> entry:Page.page().getHeadersOut().entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }
        response.setStatusCode(Page.page().responseCode());
    }

    protected ContentType getContentType() {
        return ContentType.TEXT_HTML;
    }

    protected abstract void cleanup();

}

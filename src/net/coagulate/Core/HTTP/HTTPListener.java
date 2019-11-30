package net.coagulate.Core.HTTP;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpRequestHandlerMapper;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.SEVERE;

/**
 * Listen for HTTP on a port.
 *
 * @author Iain Price
 */
public class HTTPListener {

	private final Object hasshutdownlock = new Object();
	private ServerBootstrap bootstrap = null;
	private Logger logger = null;
	private HttpServer server = null;
	private String name = "HTTPS";
	private int port = -1;
	private boolean hasshutdown = false;

	public HTTPListener(int port, String pemfile, HttpRequestHandlerMapper mapper) {
		this.port = port;
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
		try {
			// start creating a server, on the port.  disable keepalive.  probably can get rid of that.
			SocketConfig reuse = SocketConfig.custom()
					.setBacklogSize(100)
					.setSoTimeout(15000)
					.setTcpNoDelay(true)
					.setSoReuseAddress(true)
					.build();

			final ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
					.setListenerPort(port)
					.setSocketConfig(reuse)
					.setServerInfo("CoagulateHTTP/1.1")
					.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
					.setHandlerMapper(mapper);
			// NOTE HOW THE HANDLERS ARE A SINGLE INSTANCE.
			// no instance level data storage.  USE HTTPCONTEXT (superceeded by "State")
			name = "HTTP:" + port;
			//addHandlers(bootstrap);
			server = bootstrap.create();
			logger().config("HTTP Services starting");
			server.start();
		} catch (Exception e) {
			// "whoops"
			logger().log(SEVERE, "Listener startup crashed", e);
			System.exit(1);
		}
	}

	private Logger logger() {
		if (logger != null) { return logger; }
		logger = Logger.getLogger(HTTPListener.class.getCanonicalName() + "." + port);
		return logger;
	}

	public void shutdown() {
		new ShutdownHook(this).start();
	}

	public void blockingShutdown() {
		synchronized (hasshutdownlock) {
			if (hasshutdown) { return; }
			hasshutdown = true;
		}
		if (server != null) {
			logger().log(CONFIG, "Stopping listener");
			server.stop();
			try { server.awaitTermination(15, TimeUnit.SECONDS); } catch (InterruptedException e) {}
			logger().log(CONFIG, "Shutting down remaining connections in 15 seconds");
			server.shutdown(15, TimeUnit.SECONDS);
			logger().log(CONFIG, "All connections have ended");
		}
	}

	private static class ShutdownHook extends Thread {
		private final HTTPListener target;

		public ShutdownHook(HTTPListener target) { this.target = target; }

		@Override
		public void run() {
			target.blockingShutdown();
		}
	}
}

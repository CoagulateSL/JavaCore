package net.coagulate.Core.HTTP;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.concurrent.TimeUnit;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import net.coagulate.Core.Tools.CertUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpRequestHandlerMapper;

/** Listen for HTTP on a port.
 * @author Iain Price
 */
public class HTTPSListener {
    
    private ServerBootstrap bootstrap=null;
    private Logger logger;
    
    private Logger logger() { 
        if (logger!=null) { return logger; }
        logger=Logger.getLogger(HTTPSListener.class.getCanonicalName()+"."+port);
        return logger;
    }
   
    private HttpServer server=null;
    private String name="HTTPS";
    private int port=-1;
    public HTTPSListener(int port,String pemfile,HttpRequestHandlerMapper mapper) {
        this.port=port;
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            
            byte[] certAndKey = Files.readAllBytes(new File(pemfile).toPath());
            byte[] certBytes = CertUtils.parseDERFromPEM(certAndKey, "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
            byte[] keyBytes = CertUtils.parseDERFromPEM(certAndKey, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

            X509Certificate cert = CertUtils.generateCertificateFromDER(certBytes);              
            RSAPrivateKey key  = CertUtils.generatePrivateKeyFromDER(keyBytes);    

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null);
            keystore.setCertificateEntry("TLS Presentation", cert);
            keystore.setKeyEntry("key-alias", key, "changeit".toCharArray(), new Certificate[] {cert});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, "changeit".toCharArray());
            KeyManager[] km = kmf.getKeyManagers(); 
            sslcontext.init(km, null, null);            
            



            // start creating a server, on the port.  disable keepalive.  probably can get rid of that.
            SocketConfig reuse=SocketConfig.custom()
                    .setBacklogSize(100)
                    .setSoTimeout(15000)
                    .setTcpNoDelay(true)
                    .setSoReuseAddress(true)
                    .build();
                    
            final ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
                    .setListenerPort(port)
                    .setSslContext(sslcontext)
                    .setSocketConfig(reuse)
                    .setServerInfo("CoagulateHTTPS/1.1")
                    .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
                    .setHandlerMapper(mapper);
            // NOTE HOW THE HANDLERS ARE A SINGLE INSTANCE.
            // no instance level data storage.  USE HTTPCONTEXT (superceeded by "State")
            name="HTTPS:"+port;
            //addHandlers(bootstrap);
            server=bootstrap.create();
            logger().config("HTTPS Services starting");
            server.start();
        }
        catch (Exception e) {
            // "whoops"
            logger().log(SEVERE,"Listener startup crashed",e);
            System.exit(1);
        }
    }
    private final Object hasshutdownlock=new Object();
    private boolean hasshutdown=false;
    public void shutdown() {
        new ShutdownHook(this).start();
    }
    public void blockingShutdown() {
        synchronized(hasshutdownlock) {
            if (hasshutdown) { return; }
            hasshutdown=true;
        }
        if (server!=null) {
            logger().log(CONFIG,"Stopping listener");
            server.stop();
            try { server.awaitTermination(15, TimeUnit.SECONDS); } catch (InterruptedException e) {}
            logger().log(CONFIG,"Shutting down remaining connections in 15 seconds");
            server.shutdown(15,TimeUnit.SECONDS);
            logger().log(CONFIG,"All connections have ended");
        }
    }
    
    private class ShutdownHook extends Thread {
        private HTTPSListener target;
        public ShutdownHook(HTTPSListener target) { this.target=target; }
        @Override
        public void run() { 
            target.blockingShutdown();
        }
    }
}

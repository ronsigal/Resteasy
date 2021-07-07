package org.jboss.resteasy.test.security.testjar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

/**
 * ClientConfigProvider implementation used in jar that tests ClientConfigProvider functionality regarding HTTP BASIC auth and SSLContext.
 */

public class ClientConfigTestMainClass {
    public static void main(String[] args) throws IOException, URISyntaxException, NoSuchAlgorithmException {
       ExecutorService executor = Executors.newSingleThreadExecutor();
       Future<String> future = executor.submit(new Callable<String>() {

           public String call() throws Exception {
              runMain(args);
              return "OK";
           }
       });
       try {
        //CHECKSTYLE.OFF
           System.out.println(future.get(10, TimeUnit.SECONDS)); //timeout is in 2 seconds
         //CHECKSTYLE.ON
       } catch (Exception e) {
        //CHECKSTYLE.OFF
           System.err.println("Timeout");
         //CHECKSTYLE.ON
       }
       executor.shutdownNow();
    }
    
    static void runMain(String[] args) throws Exception {
        if (args.length <= 1) {
            throw new IllegalArgumentException("Url must be supplied!");
        }

        if (args.length > 2) {
            ClientConfigProviderImplMocked.KEYSTORE_PATH = args[2];
        }

        String testType = args[0];
        String result = null;
        URL url = new URL(args[1]);
        ResteasyClientBuilder resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = resteasyClientBuilder.build();
        Response response;

        if (testType.equals("TEST_CREDENTIALS_ARE_USED_FOR_BASIC") || testType.equals("TEST_SSLCONTEXT_USED")) {
            response = client.target(url.toURI()).request().get();
            result = Integer.toString(response.getStatus());
        }

        if (testType.equals("TEST_CLIENTCONFIG_CREDENTIALS_ARE_IGNORED_IF_DIFFERENT_SET")) {
            client.register(new BasicAuthentication("invalid", "invalid_pass"));
            response = client.target(url.toURI()).request().get();
            result = Integer.toString(response.getStatus());
        }

        if (testType.equals("TEST_CLIENTCONFIG_SSLCONTEXT_IGNORED_WHEN_DIFFERENT_SET")) {
            ResteasyClient clientWithSSLContextSetByUser = resteasyClientBuilder.sslContext(SSLContext.getDefault()).build();
            try {
                response = clientWithSSLContextSetByUser.target(url.toURI()).request().get();
                result = Integer.toString(response.getStatus());
            } catch (Exception e) {
                if (e.getCause().getMessage().contains("unable to find valid certification path to requested target")) {
                    result = "SSLHandshakeException";
                }
            }
        }
        //CHECKSTYLE.OFF: RegexpSinglelineJava
        System.out.println(result);
        //CHECKSTYLE.ON: RegexpSinglelineJava
        client.close();
    }
}

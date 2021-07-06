package org.jboss.resteasy.test.security.testjar;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * ClientConfigProvider implementation used in jar that tests ClientConfigProvider functionality regarding HTTP BASIC auth and SSLContext.
 */
public class ClientConfigTestMainClass {
   public static void main(String[] args) throws IOException, URISyntaxException, NoSuchAlgorithmException {
//      System.out.println("entering ClientConfigTestMainClass");
//    System.out.println("classpath: " + System.getProperty("java.class.path"));
      if (args.length <= 1) {
         throw new IllegalArgumentException("Url must be supplied!");
      }
//      System.out.println("ClientConfigTestMainClass: checking args");
      try {
      if (args.length > 2) {
//         System.out.println("ClientConfigTestMainClass(): copying args[2]");
//         System.out.println("ClientConfigProviderImplMocked: ");
         try {
//         System.out.println(ClientConfigProviderImplMocked.KEYSTORE_PATH);
         } catch (Exception e) {
            System.out.println("trying to access ClientConfigProviderImplMocked: " + e.getMessage());
         }
         ClientConfigProviderImplMocked.KEYSTORE_PATH = args[2];
//         System.out.println("ClientConfigTestMainClass(): copied args[2]");
      }
      } catch (Throwable e) {
         System.out.println("exception: " + e.getClass());
         System.out.println("Exception.getMessage(): " + e.getMessage());
//       e.printStackTrace();
      }
//      System.out.println("ClientConfigTestMainClass: checked args");    
      for (int i = 0; i < args.length; i++) {
//         System.out.println(" arg[" + i + "]: " + args[i]);
      }
//      System.out.println("ClientConfigTestMainClass: printed args");
      
      try {
         String testType = args[0];
         String result = null;
         URL url = new URL(args[1]);
//         System.out.println("getting Builder");
         ResteasyClientBuilder resteasyClientBuilder = null;
         try {
            resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
         } catch (Throwable e) {
            System.out.println("builder e: " + e);
         }
//         System.out.println("got Builder");
         
         ResteasyClient client = null;
         try {
            client = resteasyClientBuilder.build();
         } catch (Throwable e) {
            System.out.println("build error: " + e.getMessage());
         }
//         System.out.println("got client");
//       if (true) return;
         Response response = null;
//System.out.println("entered ClientConfigTestMainClass()");
         try {
         if (testType.equals("TEST_CREDENTIALS_ARE_USED_FOR_BASIC") || testType.equals("TEST_SSLCONTEXT_USED")) {
            response = client.target(url.toURI()).request().get();
            result = Integer.toString(response.getStatus());
         }
         } catch (Throwable e) {
            System.out.println("e1: " + e.getMessage());
            System.out.println("response.readEntity(1): " + response.readEntity(String.class));
         }

         try {
         if (testType.equals("TEST_CLIENTCONFIG_CREDENTIALS_ARE_IGNORED_IF_DIFFERENT_SET")) {
            client.register(new BasicAuthentication("invalid", "invalid_pass"));
            response = client.target(url.toURI()).request().get();
            result = Integer.toString(response.getStatus());
         }
         } catch (Exception e) {
            System.out.println("e2: " + e.getMessage());
            System.out.println("response.readEntity(2): " + response.readEntity(String.class));
         }

         if (testType.equals("TEST_CLIENTCONFIG_SSLCONTEXT_IGNORED_WHEN_DIFFERENT_SET")) {
            ResteasyClient clientWithSSLContextSetByUser = resteasyClientBuilder.sslContext(SSLContext.getDefault()).build();
            try {
               response = clientWithSSLContextSetByUser.target(url.toURI()).request().get();
               result = Integer.toString(response.getStatus());
            } catch (Exception e) {
               System.out.println("e3: " + e.getMessage());
               System.out.println("response.readEntity(3): " + response.readEntity(String.class));
               if (e.getCause().getMessage().contains("unable to find valid certification path to requested target")) {
                  result = "SSLHandshakeException";
               }
            }
         }
//         System.out.println("response.readEntity(4): '" + response.readEntity(String.class) + "'");
         //CHECKSTYLE.OFF: RegexpSinglelineJava
//         System.out.println("ClientConfigTestMainClass: result: '" + result + "'");
         //CHECKSTYLE.ON: RegexpSinglelineJava
         client.close();

      } catch (Exception e) {
System.out.println("e message: " + e.getMessage());      }
   }
    public static void main2(String[] args) throws IOException, URISyntaxException, NoSuchAlgorithmException {
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

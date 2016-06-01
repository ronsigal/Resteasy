package org.jboss.resteasy.test.security.smime;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jboss.resteasy.security.KeyTools;
import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class IntegrationTest extends BaseResourceTest
{
   private static Client client;
   
   @Path("/smime/encrypted")
   public static class EncryptedResource
   {
      @GET
      public EnvelopedOutput get()
      {
         EnvelopedOutput output = new EnvelopedOutput("hello world", "text/plain");
         output.setCertificate(cert);
         return output;
      }

      @POST
      public void post(EnvelopedInput<String> input)
      {
         String str = input.getEntity(privateKey, cert);
         Assert.assertEquals("input", str);
      }
   }

   @Path("/smime/signed")
   public static class SignedResource
   {
      @GET
      @Produces("multipart/signed")
      public SignedOutput get()
      {
         SignedOutput output = new SignedOutput("hello world", "text/plain");
         output.setCertificate(cert);
         output.setPrivateKey(privateKey);
         return output;
      }

      @POST
      @Consumes("multipart/signed")
      public void post(SignedInput<String> input) throws Exception
      {
         String str = input.getEntity();
         Assert.assertEquals("input", str);
         Assert.assertTrue(input.verify(cert));
      }
   }

   @Path("/smime/pkcs7-signature")
   public static class Pkcs7SignedResource
   {
      @GET
      @Produces("application/pkcs7-signature")
      public SignedOutput get()
      {
         SignedOutput output = new SignedOutput("hello world", "text/plain");
         output.setCertificate(cert);
         output.setPrivateKey(privateKey);
         return output;
      }

      @GET
      @Path("text")
      @Produces("text/plain")
      public SignedOutput getText()
      {
         SignedOutput output = new SignedOutput("hello world", "text/plain");
         output.setCertificate(cert);
         output.setPrivateKey(privateKey);
         return output;
      }


      @POST
      @Consumes("application/pkcs7-signature")
      public void post(PKCS7SignatureInput<String> input) throws Exception
      {
         String str = input.getEntity(MediaType.TEXT_PLAIN_TYPE);
         Assert.assertEquals("input", str);
         Assert.assertTrue(input.verify(cert));
      }
   }


   @Path("/smime/encrypted/signed")
   public static class EncryptedSignedResource
   {
      @GET
      public EnvelopedOutput get()
      {
         SignedOutput signed = new SignedOutput("hello world", "text/plain");
         signed.setCertificate(cert);
         signed.setPrivateKey(privateKey);

         EnvelopedOutput output = new EnvelopedOutput(signed, "multipart/signed");
         output.setCertificate(cert);
         return output;
      }

      @POST
      public void post(EnvelopedInput<SignedInput<String>> input) throws Exception
      {
         SignedInput<String> str = input.getEntity(privateKey, cert);
         Assert.assertEquals("input", str.getEntity());
         Assert.assertTrue(str.verify(cert));
      }
   }

   private static X509Certificate cert;
   private static PrivateKey privateKey;

   @BeforeClass
   public static void setup() throws Exception
   {
      Security.addProvider(new BouncyCastleProvider());

      /*
      InputStream certIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("mycert.pem");
      cert = PemUtils.decodeCertificate(certIs);

      InputStream privateIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("mycert-private.pem");
      privateKey = PemUtils.decodePrivateKey(privateIs);
      */

      KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
      privateKey = keyPair.getPrivate();
      cert = KeyTools.generateTestCertificate(keyPair);


      dispatcher.getRegistry().addPerRequestResource(EncryptedResource.class);
      dispatcher.getRegistry().addPerRequestResource(SignedResource.class);
      dispatcher.getRegistry().addPerRequestResource(EncryptedSignedResource.class);
      dispatcher.getRegistry().addPerRequestResource(Pkcs7SignedResource.class);
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }

   @Test
   public void testSignedOutput() throws Exception
   {
      Response res = client.target(generateURL("/smime/signed")).request().get();
      Assert.assertEquals(200, res.getStatus());
      System.out.println(res.readEntity(String.class));
      MediaType contentType = MediaType.valueOf(res.getHeaderString("Content-Type"));
      System.out.println(contentType);
   }

   @Test
   public void testSignedOutput2() throws Exception
   {
      SignedInput<?> signed = client.target(generateURL("/smime/signed")).request().get(SignedInput.class);
      String output = (String) signed.getEntity(String.class);
      System.out.println(output);
      Assert.assertEquals("hello world", output);
      Assert.assertTrue(signed.verify(cert));
   }

   @Test
   public void testPKCS7SignedOutput() throws Exception
   {
      PKCS7SignatureInput<?> signed = client.target(generateURL("/smime/pkcs7-signature")).request().get(PKCS7SignatureInput.class);
      String output = (String) signed.getEntity(String.class, MediaType.TEXT_PLAIN_TYPE);
      System.out.println(output);
      Assert.assertEquals("hello world", output);
   }

   @Test
   public void testPKCS7SignedTextOutput() throws Exception
   {
      String base64 = client.target(generateURL("/smime/pkcs7-signature/text")).request().get(String.class);
      
      System.out.println(base64);
      PKCS7SignatureInput<?> signed = new PKCS7SignatureInput<Object>(base64);
      signed.setProviders(ResteasyProviderFactory.getInstance());

      String output = (String) signed.getEntity(String.class, MediaType.TEXT_PLAIN_TYPE);
      System.out.println(output);
      Assert.assertEquals("hello world", output);
      Assert.assertTrue(signed.verify(cert));
   }


   @Test
   public void testEncryptedOutput() throws Exception
   {
      Response res = client.target(generateURL("/smime/encrypted")).request().get();
      Assert.assertEquals(200, res.getStatus());
      System.out.println(res.readEntity(String.class));
      MediaType contentType = MediaType.valueOf(res.getHeaderString("Content-Type"));
      System.out.println(contentType);
   }

   @Test
   public void testEncryptedOutput2() throws Exception
   {
      EnvelopedInput<?> enveloped = client.target(generateURL("/smime/encrypted")).request().get(EnvelopedInput.class);
      String output = (String) enveloped.getEntity(String.class, privateKey, cert);
      System.out.println(output);
      Assert.assertEquals("hello world", output);
   }

   @Test
   public void testEncryptedSignedOutputToFile() throws Exception
   {
      Response res = client.target(generateURL("/smime/encrypted/signed")).request().get();
      MediaType contentType = MediaType.valueOf(res.getHeaderString("Content-Type"));
      System.out.println(contentType);
      System.out.println();
      String entity = res.readEntity(String.class);
      System.out.println(entity);

      FileOutputStream os = new FileOutputStream("target/python_encrypted_signed.txt");
      os.write("Content-Type: ".getBytes());
      os.write(contentType.toString().getBytes());
      os.write("\r\n".getBytes());
      os.write(entity.getBytes());
      os.close();
   }

   @Test
   public void testEncryptedSignedOutput() throws Exception
   {
      EnvelopedInput<?> enveloped = client.target(generateURL("/smime/encrypted/signed")).request().get(EnvelopedInput.class);
      SignedInput<?> signed = (SignedInput<?>) enveloped.getEntity(SignedInput.class, privateKey, cert);
      String output = (String) signed.getEntity(String.class);
      System.out.println(output);
      Assert.assertEquals("hello world", output);
      Assert.assertTrue(signed.verify(cert));
      Assert.assertEquals("hello world", output);
   }

   @Test
   public void testEncryptedInput() throws Exception
   {
      Builder request = client.target(generateURL("/smime/encrypted")).request();
      EnvelopedOutput output = new EnvelopedOutput("input", "text/plain");
      output.setCertificate(cert);
      Response res = request.post(Entity.entity(output, "*/*"));
      Assert.assertEquals(204, res.getStatus());
      res.close();
   }

   @Test
   public void testEncryptedSignedInput() throws Exception
   {
      Builder request = client.target(generateURL("/smime/encrypted/signed")).request();
      SignedOutput signed = new SignedOutput("input", "text/plain");
      signed.setPrivateKey(privateKey);
      signed.setCertificate(cert);
      EnvelopedOutput output = new EnvelopedOutput(signed, "multipart/signed");
      output.setCertificate(cert);
      Response res = request.post(Entity.entity(output, "*/*"));
      Assert.assertEquals(204, res.getStatus());
      res.close();
   }

   @Test
   public void testSignedInput() throws Exception
   {
      Builder request = client.target(generateURL("/smime/signed")).request();
      SignedOutput output = new SignedOutput("input", "text/plain");
      output.setCertificate(cert);
      output.setPrivateKey(privateKey);
      Response res = request.post(Entity.entity(output, "multipart/signed"));
      Assert.assertEquals(204, res.getStatus());
      res.close();
   }

   @Test
   public void testPKCS7SignedInput() throws Exception
   {
      Builder request = client.target(generateURL("/smime/pkcs7-signature")).request();
      SignedOutput output = new SignedOutput("input", "text/plain");
      output.setCertificate(cert);
      output.setPrivateKey(privateKey);
      Response res = request.post(Entity.entity(output, "application/pkcs7-signature"));
      Assert.assertEquals(204, res.getStatus());
      res.close();
   }
}

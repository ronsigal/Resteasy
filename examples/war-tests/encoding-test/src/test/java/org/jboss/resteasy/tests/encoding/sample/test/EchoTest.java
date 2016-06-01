package org.jboss.resteasy.tests.encoding.sample.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tests.encoding.sample.HelloClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-208, RESTEASY-214
 */
public class EchoTest
{

   private static final String SPACES_REQUEST = "something something";
   private static final String QUERY = "select p from VirtualMachineEntity p where guest.guestId = :id";
   private static Client client;
   
   @BeforeClass
   public static void beforeClass()
   {
      client = ResteasyClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }
   
   @Test
   public void testEcho()
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:9095");
      HelloClient helloClient = target.proxy(HelloClient.class);
      Assert.assertEquals(SPACES_REQUEST, helloClient.sayHi(SPACES_REQUEST));

      Assert.assertEquals(QUERY, helloClient.compile(QUERY));
   }

   @Test
   public void testIt() throws Exception
   {
      Builder request = client.target("http://localhost:9095/sayhello/widget/08%2F26%2F2009").request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   @Test
   public void testPlus() throws Exception
   {
      Builder request = client.target("http://localhost:9095/sayhello/plus/foo+bar").request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }


   @Test
   public void testPlus2() throws Exception
   {
      Builder request = client.target("http://localhost:9095/sayhello/plus/foo+bar").request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }
}


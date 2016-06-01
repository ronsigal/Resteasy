package org.jboss.resteasy.test.application;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-381
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationTest
{
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
   public void testCount() throws Exception
   {
      String count = client.target("http://localhost:9095/my/application/count").request().get(String.class);
      Assert.assertEquals("1", count);
   }

   /**
    *
    * RESTEASY-518
    *
    * @throws Exception
    */
   @Test
   public void testNullJaxb() throws Exception
   {
      Builder request = client.target("http://localhost:9095/my/null").request();
      request.header("Content-Type", "application/xml");
      Response res = request.post(null);
      Assert.assertEquals(400, res.getStatus());
      res.close();
   }

   /**
    *
    * RESTEASY-582
    *
    * @throws Exception
    */
   @Test
   public void testBadMediaTypeNoSubtype() throws Exception
   {
      Builder request = client.target("http://localhost:9095/my/application/count").request();
      request.accept("text");
      Response response = request.get();
      Assert.assertEquals(400, response.getStatus());
      response.close();
   }

   /**
    *
    * RESTEASY-582
    *
    * @throws Exception
    */
   @Test
   public void testBadMediaTypeNonNumericQualityValue() throws Exception
   {
      Builder request = client.target("http://localhost:9095/my/application/count").request();
      request.accept("text/plain; q=bad");
      Response response = request.get();
      Assert.assertEquals(400, response.getStatus());
      response.close();
   }
}

package org.jboss.resteasy.test.async;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncTest
{
   @Test
   public void testAsync() throws Exception
   {
      Response response = ClientBuilder.newClient().target("http://localhost:8080/").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.readEntity(String.class));
   }

   @Test
   public void testTimeout() throws Exception
   {
      Response response = ClientBuilder.newClient().target("http://localhost:8080/timeout").request().get();
      Assert.assertEquals(408, response.getStatus()); // exception mapper from another test overrides 503 to 408
   }
}

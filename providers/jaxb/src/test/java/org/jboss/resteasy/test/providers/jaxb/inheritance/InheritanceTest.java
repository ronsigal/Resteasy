package org.jboss.resteasy.test.providers.jaxb.inheritance;

import org.junit.Assert;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InheritanceTest extends BaseResourceTest
{
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(ZooWS.class);
   }

   @Test
   public void testInheritance() throws Exception
   {
      Response response = ClientBuilder.newClient().target(generateURL("/zoo")).request().get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

}

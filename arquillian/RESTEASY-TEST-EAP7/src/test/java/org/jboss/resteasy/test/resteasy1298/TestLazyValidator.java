package org.jboss.resteasy.test.resteasy1298;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy1298.JaxRsActivator;
import org.jboss.resteasy.resteasy1298.TestResourceLazyValidator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.junit.Assert;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Junk 8, 2013
 */
@RunWith(Arquillian.class)
public class TestLazyValidator
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(TestResourceLazyValidator.class)
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   @Ignore
   public void testLazyValidator() throws Exception
   {
      // Valid native constraint
//      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/lazy");
//      ClientResponse<?> response = request.get();  
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/lazy").request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.readEntity(boolean.class));
   }
}

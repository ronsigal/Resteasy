package org.jboss.resteasy.test.validation;

import org.junit.Assert;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.validation.JaxRsActivator;
import org.jboss.resteasy.validation.TestResourceWithGetterViolation;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created June 8, 2013
 */
@RunWith(Arquillian.class)
public class TestGetterReturnValueNotValidated
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(TestResourceWithGetterViolation.class)
            .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testReturnValues() throws Exception
   {
      // Valid native constraint
      Response response = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/get").request().get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNull(header);
      Object entity = response.readEntity(String.class);
      System.out.println("entity: " + entity);
      Assert.assertEquals("a", entity);
   }
}

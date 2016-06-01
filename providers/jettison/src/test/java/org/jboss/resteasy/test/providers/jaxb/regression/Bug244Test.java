package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Bug244Test extends BaseResourceTest
{
   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class MyObject
   {
      @XmlAttribute
      String name = "bill";

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Path("/test")
   public static class TestService
   {
      @GET
      @Path("bug")
      @Produces({"application/json;charset=UTF-8"})
      public MyObject bug()
      {
         return new MyObject();
      }

      @GET
      @Path("nobug")
      @Produces({"application/json"})
      public MyObject nobug()
      {
         return new MyObject();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestService.class);
   }

   @Test
   public void testCharset() throws Exception
   {
      Response response = ClientBuilder.newClient().target(generateURL("/test/bug")).request().get();
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.readEntity(String.class));
   }

}

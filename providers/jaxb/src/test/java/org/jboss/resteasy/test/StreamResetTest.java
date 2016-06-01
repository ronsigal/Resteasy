package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamResetTest extends BaseResourceTest
{
   @Path("/test")
   public static class SimpleResource
   {
      @GET
      @Produces("application/xml")
      public String get()
      {
         return "<person name=\"bill\"/>";
      }
   }

   @XmlRootElement(name = "person")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   public static class Person
   {
      private String name;

      @XmlAttribute
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @XmlRootElement(name = "place")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   public static class Place
   {
      private String name;

      @XmlAttribute
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(SimpleResource.class);
   }

   @Test
   public void testJBEAP2138() throws Exception {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/test"));
      Response response = target.request().get();

      response.bufferEntity();

      try {
         response.readEntity(Place.class);
      } catch (Exception e) {}

      response.readEntity(Person.class);
   }

   @Test(expected = IllegalStateException.class)
   public void testJBEAP2138WithoutBufferedEntity() throws Exception {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/test"));
      Response response = target.request().get();

      try {
         response.readEntity(Place.class);
      } catch (Exception e) {}

      response.readEntity(Person.class);
   }

   @Test
   public void test456() throws Exception
   {
      ClientResponse response = (ClientResponse) ClientBuilder.newClient().target(TestPortProvider.generateURL("/test")).request().get();
      boolean exceptionThrown = false;
      try
      {
         response.bufferEntity();
         response.readEntity(Place.class);
      }
      catch (Exception e)
      {
         exceptionThrown = true;
      }
      Assert.assertTrue(exceptionThrown);

//      response.resetStream(); // Out of date.

      Person person = response.readEntity(Person.class);
      Assert.assertNotNull(person);
      Assert.assertEquals("bill", person.getName());
   }

}
